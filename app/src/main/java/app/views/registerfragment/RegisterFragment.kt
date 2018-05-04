package app.views.registerfragment

import android.arch.lifecycle.Lifecycle
import app.ext.BaseFragment
import app.extensions.plusAssign
import app.extensions.set
import app.parcelables.PinActivityIntentModel
import app.views.launchactivity.LaunchActivity
import app.views.pinactivity.PinActivity
import com.example.namigtahmazli.sweetnote.BR
import com.example.namigtahmazli.sweetnote.R
import com.example.namigtahmazli.sweetnote.databinding.FragmentRegisterBinding
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
        dataBinding[BR.viewModel] = registerFragmentViewModel
    }

    override fun addLifecycleObservers(lifecycle: Lifecycle) {
        lifecycle += registerFragmentPresenter
    }

    fun handleEtPasswordChanges(isValid: Boolean) {
        dataBinding.passwordLayout.error = if (isValid) null
        else getString(R.string.register_fragment_invalid_password_message)
    }

    fun handleEtEmailChanges(isValid: Boolean) {
        dataBinding.emailLayout.error = if (isValid) null
        else getString(R.string.register_fragment_invalid_email_message)
    }

    fun handleUserRegistration() {
        startActivity(PinActivity.provideIntent(launchActivity, PinActivityIntentModel(EnterPinType.Register)))
    }

    companion object {
        const val FRAGMENT = "RegisterFragment"
    }
}