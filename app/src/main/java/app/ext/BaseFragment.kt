package app.ext

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

internal abstract class BaseFragment<out VM : BaseViewModel, DB : ViewDataBinding> : Fragment() {
    lateinit var dataBinding: DB
    @get:LayoutRes
    abstract val layoutRes: Int
    abstract val viewModel: VM
    abstract val viewModelId: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dataBinding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        dataBinding.setVariable(viewModelId, viewModel)
        return dataBinding.root
    }
}