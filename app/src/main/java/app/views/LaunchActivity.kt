package app.views

import app.ext.BaseActivity
import app.ext.ViewModelFactory
import app.views.dagger.LaunchActivityScope
import app.views.mvvm.LaunchActivityModel
import com.example.namigtahmazli.sweetnote.R
import com.example.namigtahmazli.sweetnote.databinding.ActivityLauncherBinding
import javax.inject.Inject

@LaunchActivityScope
internal class LaunchActivity : BaseActivity<LaunchActivityModel, ActivityLauncherBinding>() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override val dataBinding: ActivityLauncherBinding
        get() = getDataBinding(R.layout.activity_launcher)

    override val viewModel: LaunchActivityModel
        get() = getViewModel(viewModelFactory)
}