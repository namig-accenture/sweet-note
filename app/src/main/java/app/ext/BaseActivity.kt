package app.ext

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

internal abstract class BaseActivity<out DB : ViewDataBinding> : AppCompatActivity() {
    abstract val dataBinding: DB

    inline fun <reified DB : ViewDataBinding> getDataBinding(@LayoutRes res: Int): DB {
        return DataBindingUtil.setContentView(this, res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(dataBinding.root)
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
}