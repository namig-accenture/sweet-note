package app.ext

import android.app.Dialog
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.extensions.Duration
import app.extensions.showSnackBar

internal abstract class BaseDialogFragment<DB : ViewDataBinding> : AppCompatDialogFragment(), LifecycleOwner {
    lateinit var dataBinding: DB

    @get:LayoutRes
    abstract val layoutRes: Int

    abstract fun bindVariables(dataBinding: DB)
    abstract fun addLifecycleObservers(lifecycle: Lifecycle)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addLifecycleObservers(lifecycle)
    }

    open fun onBackPressed() {}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dataBinding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        bindVariables(dataBinding)
        return dataBinding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(activity, theme) {
            override fun onBackPressed() {
                this@BaseDialogFragment.onBackPressed()
            }
        }
    }

    fun showMessage(@StringRes messageId: Int? = null,
                    message: String = "") {
        dataBinding.root.showSnackBar(messageId = messageId, message = message, duration = Duration.LONG)
    }
}