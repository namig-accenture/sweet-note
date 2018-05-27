package app.views.loginfragment

import android.arch.lifecycle.Lifecycle
import app.ext.BaseFragment
import app.extensions.plusAssign
import app.parcelables.PinActivityIntentModel
import app.views.launchactivity.LaunchActivity
import app.views.pinactivity.PinActivity
import com.example.namigtahmazli.sweetnote.R
import com.example.namigtahmazli.sweetnote.databinding.FragmentLoginBinding
import domain.exceptions.UserNotFoundException
import domain.model.EnterPinType
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.property

internal class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    val loginFragmentViewModel by viewModel<LoginFragmentViewModel>()
    private val launchActivity by property<LaunchActivity>(LaunchActivity.ACTIVITY)
    private val loginFragmentPresenter by inject<LoginFragmentPresenter> { mapOf(FRAGMENT to this) }

    override val layoutRes: Int
        get() = R.layout.fragment_login

    override fun bindVariables(dataBinding: FragmentLoginBinding) {
        dataBinding.viewModel = loginFragmentViewModel
        dataBinding.presenter = loginFragmentPresenter
    }

    override fun addLifecycleObservers(lifecycle: Lifecycle) {
        lifecycle += loginFragmentPresenter
    }

    fun handleLogin() {
        startActivity(PinActivity.provideIntent(launchActivity, PinActivityIntentModel(EnterPinType.Register)))
        activity?.finish()
    }

    fun handleLoginError(throwable: Throwable) {
        when (throwable) {
            is LoginFragmentViewModel.ExceptionCase.EmailNotDefined -> handleEmailChanges(false)
            is LoginFragmentViewModel.ExceptionCase.PasswordNotDefined -> handlePasswordChanges(false)
            is UserNotFoundException -> showMessage(messageId = R.string.login_fragment_user_not_found_error_message)
            else -> showMessage(messageId = R.string.login_fragment_login_error_message)
        }
    }

    fun handlePasswordChanges(isValid: Boolean) {
        dataBinding.passwordLayout.error = if (!isValid)
            getString(R.string.login_fragment_password_not_defined_message)
        else null
    }

    fun handleEmailChanges(isValid: Boolean) {
        dataBinding.emailLayout.error = if (!isValid)
            getString(R.string.login_fragment_email_not_defined_message)
        else null
    }

    companion object {
        const val FRAGMENT = "LoginFragment"
    }
}