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
import android.view.*
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

    private fun onUserInteraction() {
        activity?.onUserInteraction()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(activity, theme) {
            override fun onBackPressed() {
                this@BaseDialogFragment.onBackPressed()
            }

            override fun dispatchGenericMotionEvent(ev: MotionEvent?): Boolean {
                onUserInteraction()
                return super.dispatchGenericMotionEvent(ev)
            }

            override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
                onUserInteraction()
                return super.dispatchKeyEvent(event)
            }

            override fun dispatchKeyShortcutEvent(event: KeyEvent?): Boolean {
                onUserInteraction()
                return super.dispatchKeyShortcutEvent(event)
            }

            override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
                onUserInteraction()
                return super.dispatchTouchEvent(ev)
            }

            override fun dispatchTrackballEvent(ev: MotionEvent?): Boolean {
                onUserInteraction()
                return super.dispatchTrackballEvent(ev)
            }
        }
    }

    fun showMessage(@StringRes messageId: Int? = null,
                    message: String = "") {
        dataBinding.root.showSnackBar(messageId = messageId, message = message, duration = Duration.LONG)
    }
}