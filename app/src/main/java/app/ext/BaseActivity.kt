package app.ext

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDialogFragment
import app.extensions.Duration
import app.extensions.showSnackBar

internal abstract class BaseActivity<DB : ViewDataBinding> : AppCompatActivity(), LifecycleOwner {
    abstract val dataBinding: DB
    abstract fun addLifecycleObservers(lifecycle: Lifecycle)

    override fun onCreate(savedInstanceState: Bundle?) {
        addLifecycleObservers(lifecycle)
        super.onCreate(savedInstanceState)
        setContentView(dataBinding.root)
    }

    inline fun <reified A : BaseActivity<DB>> A.provideDataBinding(@LayoutRes layoutRes: Int): DB {
        return DataBindingUtil.setContentView(this, layoutRes)
    }

    inline fun <reified F : Fragment> addFragment(@IdRes container: Int, block: () -> F) {
        val tag = F::class.java.simpleName
        supportFragmentManager.findFragmentByTag(tag)?.let {
            return
        }
        supportFragmentManager.beginTransaction()
                .replace(container, block(), tag)
                .commit()
    }

    inline fun <reified F : AppCompatDialogFragment> addDialogFragment(block: () -> F,
                                                                       transition: FragmentTransaction.() -> FragmentTransaction = { this }) {
        val tag = F::class.java.name
        supportFragmentManager.findFragmentByTag(tag)?.let {
            return
        }
        supportFragmentManager.beginTransaction()
                .add(block(), tag)
                .transition()
                .commit()
    }

    fun showMessage(@StringRes messageId: Int? = null, message: String = "") {
        dataBinding.root.showSnackBar(messageId = messageId, message = message, duration = Duration.LONG)
    }
}