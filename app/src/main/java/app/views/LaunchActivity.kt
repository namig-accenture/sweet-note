package app.views

import app.ext.BaseActivity
import app.views.dagger.LaunchActivityScope
import app.views.mvvm.LaunchActivityModel
import com.example.namigtahmazli.sweetnote.databinding.ActivityLauncherBinding
import javax.inject.Inject

@LaunchActivityScope
internal class LaunchActivity : BaseActivity<LaunchActivityModel, ActivityLauncherBinding>() {

    @Inject
    lateinit var launchActivityModel: LaunchActivityModel

    @Inject
    lateinit var launchActivityBinding: ActivityLauncherBinding

    override val dataBinding: ActivityLauncherBinding
        get() = launchActivityBinding

    override val viewModel: LaunchActivityModel
        get() = launchActivityModel
}