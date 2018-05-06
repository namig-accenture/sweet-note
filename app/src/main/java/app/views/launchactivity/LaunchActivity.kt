package app.views.launchactivity

import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.IdRes
import app.ext.BaseActivity
import app.ext.log
import app.extensions.plusAssign
import app.parcelables.PinActivityIntentModel
import app.views.loginfragment.LoginFragment
import app.views.pinactivity.PinActivity
import app.views.registerfragment.RegisterFragment
import com.example.namigtahmazli.sweetnote.R
import com.example.namigtahmazli.sweetnote.databinding.ActivityLauncherBinding
import com.fernandocejas.arrow.optional.Optional
import domain.model.EnterPinType
import domain.model.UserModel
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.setProperty

internal class LaunchActivity : BaseActivity<ActivityLauncherBinding>() {
    val launchActivityViewModel by viewModel<LaunchActivityViewModel>()
    private val launchActivityPresenter by inject<LaunchActivityPresenter> { mapOf(ACTIVITY to this) }
    private val loginFragment by inject<LoginFragment>()
    private val registerFragment by inject<RegisterFragment>()

    override val dataBinding: ActivityLauncherBinding
        get() = provideDataBinding(R.layout.activity_launcher).apply {
            viewModel = launchActivityViewModel
            presenter = launchActivityPresenter
        }

    override fun addLifecycleObservers(lifecycle: Lifecycle) {
        lifecycle += launchActivityPresenter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setProperty(ACTIVITY, this)
        super.onCreate(savedInstanceState)
    }

    fun handleSwitchChanges(@IdRes id: Int) {
        when (id) {
            R.id.group_login -> addFragment(R.id.container_layout) { loginFragment }
            R.id.group_register -> addFragment(R.id.container_layout) { registerFragment }
        }
    }

    fun handleCurrentUserAvailibility(optionalUser: Optional<UserModel>) {
        if (optionalUser.isPresent) {
            val pinType = if (optionalUser.get().pin != null) EnterPinType.Login else EnterPinType.Register
            startActivity(PinActivity.provideIntent(this, PinActivityIntentModel(pinType)))
            finish()
        }
    }

    fun handleCurrentUserAvailibilityError(throwable: Throwable) {
        throwable.log<LaunchActivityPresenter>("While observing current logged in user.")
    }

    companion object {
        const val ACTIVITY = "LaunchActivity"
        fun getIntent(context: Context): Intent {
            return Intent(context, LaunchActivity::class.java)
        }
    }
}