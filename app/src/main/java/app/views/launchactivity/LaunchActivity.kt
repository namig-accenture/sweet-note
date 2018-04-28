package app.views.launchactivity

import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.content.Intent
import android.support.annotation.IdRes
import app.ext.BaseActivity
import app.extensions.plusAssign
import app.extensions.set
import app.views.loginfragment.LoginFragment
import app.views.registerfragment.RegisterFragment
import com.example.namigtahmazli.sweetnote.BR
import com.example.namigtahmazli.sweetnote.R
import com.example.namigtahmazli.sweetnote.databinding.ActivityLauncherBinding
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.android.inject

internal class LaunchActivity : BaseActivity<ActivityLauncherBinding>() {
    private val launchActivityViewModel by viewModel<LaunchActivityViewModel>()
    private val launchActivityPresenter by inject<LaunchActivityPresenter> { mapOf(ACTIVITY to this) }
    private val loginFragment by inject<LoginFragment>()
    private val registerFragment by inject<RegisterFragment>()

    override val dataBinding: ActivityLauncherBinding
        get() = provideDataBinding(R.layout.activity_launcher)

    override fun bindVariables(dataBinding: ActivityLauncherBinding) {
        dataBinding[BR.viewModel] = launchActivityViewModel
    }

    override fun addLifecycleObservers(lifecycle: Lifecycle) {
        lifecycle += launchActivityPresenter
    }

    fun handleSwitchChanges(@IdRes id: Int) {
        when (id) {
            R.id.group_login -> addFragment(R.id.container_layout) { loginFragment }
            R.id.group_register -> addFragment(R.id.container_layout) { registerFragment }
        }
    }

    companion object {
        const val ACTIVITY = "LaunchActivity"
        fun getIntent(context: Context): Intent {
            return Intent(context, LaunchActivity::class.java)
        }
    }
}