package app.views.registerfragment

import android.arch.lifecycle.Lifecycle
import app.ext.BaseFragment
import app.extensions.plusAssign
import app.parcelables.PinActivityIntentModel
import app.views.launchactivity.LaunchActivity
import app.views.pinactivity.PinActivity
import com.example.namigtahmazli.sweetnote.R
import com.example.namigtahmazli.sweetnote.databinding.FragmentRegisterBinding
import domain.exceptions.InvalidEmailException
import domain.exceptions.InvalidPasswordException
import domain.model.EnterPinType
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.property

internal class RegisterFragment : BaseFragment<FragmentRegisterBinding>() {
    val registerFragmentViewModel by viewModel<RegisterFragmentViewModel>()
    private val launchActivity by property<LaunchActivity>(LaunchActivity.ACTIVITY)
    private val registerFragmentPresenter by inject<RegisterFragmentPresenter> { mapOf(FRAGMENT to this) }

    override val layoutRes: Int
        get() = R.layout.fragment_register

    override fun bindVariables(dataBinding: FragmentRegisterBinding) {
        dataBinding.viewModel = registerFragmentViewModel
        dataBinding.presenter = registerFragmentPresenter
    }

    override fun addLifecycleObservers(lifecycle: Lifecycle) {
        lifecycle += registerFragmentPresenter
    }

    fun handleEtPasswordChanges(isValid: Boolean) {
        val message = if (isValid) null else getString(R.string.register_fragment_invalid_password_message)
        registerFragmentViewModel.passwordError.set(message)
    }

    fun handleEtEmailChanges(isValid: Boolean) {
        val message = if (isValid) null else getString(R.string.register_fragment_invalid_email_message)
        registerFragmentViewModel.emailError.set(message)
    }

    fun handleUserRegistration() {
        startActivity(PinActivity.provideIntent(launchActivity, PinActivityIntentModel(EnterPinType.Register)))
    }

    fun handleUserRegistrationError(throwable: Throwable) {
        val message = when (throwable) {
            is InvalidEmailException -> R.string.register_fragment_invalid_email_message
            is InvalidPasswordException -> R.string.register_fragment_invalid_password_message
            else -> R.string.register_fragment_registration_error
        }
        showMessage(messageId = message)
    }

    companion object {
        const val FRAGMENT = "RegisterFragment"
    }
}