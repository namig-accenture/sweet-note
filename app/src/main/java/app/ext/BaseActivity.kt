package app.ext

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

internal abstract class BaseActivity<out VM : BaseViewModel, out DB : ViewDataBinding> : AppCompatActivity(), HasSupportFragmentInjector {
    abstract val viewModel: VM
    abstract val dataBinding: DB

    @Inject
    lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>

    inline fun <reified VM : BaseViewModel, F : ViewModelProvider.Factory> getViewModel(factory: F): VM {
        return ViewModelProviders.of(this, factory)[VM::class.java]
    }

    inline fun <reified DB : ViewDataBinding> getDataBinding(@LayoutRes res: Int): DB {
        return DataBindingUtil.setContentView(this, res)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = supportFragmentInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(dataBinding.root)
    }
}