package app.views.loginfragment

import android.arch.lifecycle.Lifecycle
import app.ext.BaseFragment
import app.extensions.plusAssign
import app.extensions.set
import com.example.namigtahmazli.sweetnote.BR
import com.example.namigtahmazli.sweetnote.R
import com.example.namigtahmazli.sweetnote.databinding.FragmentLoginBinding
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.android.inject

internal class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    val loginFragmentViewModel by viewModel<LoginFragmentViewModel>()
    private val loginFragmentPresenter by inject<LoginFragmentPresenter> { mapOf(FRAGMENT to this) }

    override val layoutRes: Int
        get() = R.layout.fragment_login

    override fun bindVariables(dataBinding: FragmentLoginBinding) {
        dataBinding[BR.viewModel] = loginFragmentViewModel
    }

    override fun addLifecycleObservers(lifecycle: Lifecycle) {
        lifecycle += loginFragmentPresenter
    }

    fun handleLogin() {
        showMessage(message = "Logged in")
    }

    fun handleLoginError(throwable: Throwable) {
        when (throwable) {
            is LoginFragmentViewModel.ExceptionCase.EmailNotDefined -> handleEmailChanges(false)
            is LoginFragmentViewModel.ExceptionCase.PasswordNotDefined -> handlePasswordChanges(false)
            else -> showMessage(message = "User not found")
        }
    }

    fun handlePasswordChanges(isValid: Boolean) {
        dataBinding.passwordLayout.error = if (!isValid) "Password not defined" else null
    }

    fun handleEmailChanges(isValid: Boolean) {
        dataBinding.emailLayout.error = if (!isValid) "Email not defined" else null
    }

    companion object {
        const val FRAGMENT = "LoginFragment"
    }
}