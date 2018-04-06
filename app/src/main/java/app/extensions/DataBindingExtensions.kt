package app.extensions

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.annotation.LayoutRes
import app.ext.BaseActivity
import app.ext.BaseViewModel

internal inline fun <reified DB : ViewDataBinding,
        A : BaseActivity<BaseViewModel, DB>>
        A.getDataBinding(@LayoutRes res: Int): DB {
    return DataBindingUtil.setContentView(this, res)
}