package app.views.registerfragment

import android.arch.lifecycle.Lifecycle
import app.ext.BaseFragment
import app.extensions.plusAssign
import app.extensions.set
import com.example.namigtahmazli.sweetnote.BR
import com.example.namigtahmazli.sweetnote.R
import com.example.namigtahmazli.sweetnote.databinding.FragmentRegisterBinding
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.android.inject

internal class RegisterFragment : BaseFragment<FragmentRegisterBinding>() {
    val registerFragmentViewModel by viewModel<RegisterFragmentViewModel>()
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
        dataBinding.passwordLayout.error = if (isValid) null else "Invalid"
    }

    fun handleEtEmailChanges(isValid: Boolean) {
        dataBinding.emailLayout.error = if (isValid) null else "Invalid"
    }

    fun handleUserRegistration() {
        showMessage(message = "Registered Successfully")
    }

    companion object {
        const val FRAGMENT = "RegisterFragment"
    }
}