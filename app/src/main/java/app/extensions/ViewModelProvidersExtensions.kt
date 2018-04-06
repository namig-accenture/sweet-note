package app.extensions

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.ViewDataBinding
import app.ext.BaseActivity
import app.ext.BaseViewModel

internal inline fun <reified VM : BaseViewModel,
        A : BaseActivity<VM, ViewDataBinding>,
        F : ViewModelProvider.Factory>
        A.getViewModel(factory: F): VM {
    return ViewModelProviders.of(this, factory)[VM::class.java]
}