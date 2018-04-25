package app.views.loginfragment

import android.os.Bundle
import app.ext.BaseFragment
import com.example.namigtahmazli.sweetnote.BR
import com.example.namigtahmazli.sweetnote.R
import com.example.namigtahmazli.sweetnote.databinding.FragmentLoginBinding
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import org.koin.android.architecture.ext.viewModel
import timber.log.Timber

internal class LoginFragment : BaseFragment<LoginFragmentViewModel, FragmentLoginBinding>() {
    private val loginViewModel by viewModel<LoginFragmentViewModel>()
    override val layoutRes: Int
        get() = R.layout.fragment_login
    override val viewModel: LoginFragmentViewModel
        get() = loginViewModel
    override val viewModelId: Int
        get() = BR.viewModel

    private lateinit var disposables: CompositeDisposable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        disposables = CompositeDisposable()
    }

    override fun onStart() {
        super.onStart()
        disposables += observeEtEmailChanges()
        disposables += observeEtPasswordChanges()
        disposables += observeLoginButtonClick()
    }

    private fun observeLoginButtonClick(): Disposable {
        return RxView.clicks(dataBinding.btnLogin)
                .firstElement()
                .subscribeBy(
                        onSuccess = { disposables += handleLoginButtonClick() },
                        onError = Timber::e
                )
    }

    private fun handleLoginButtonClick(): Disposable {
        return viewModel.login()
                .subscribeBy(
                        onComplete = { showMessage(message = "Logged in") },
                        onError = ::handleLoginError
                )
    }

    private fun handleLoginError(throwable: Throwable) {
        when (throwable) {
            is LoginFragmentViewModel.ExceptionCase.EmailNotDefined -> dataBinding.etEmail.error = "Email not defined"
            is LoginFragmentViewModel.ExceptionCase.PasswordNotDefined -> dataBinding.etPassword.error = "Password not defined"
            else -> showMessage(message = "User not found")
        }
    }

    private fun observeEtPasswordChanges(): Disposable {
        return RxTextView.textChanges(dataBinding.etPassword)
                .filter { it.isNotEmpty() }
                .map { it.toString() }
                .subscribeBy(
                        onNext = ::handlePasswordChanges,
                        onError = Timber::e
                )
    }

    private fun handlePasswordChanges(password: String) {
        viewModel.password = password
    }

    private fun observeEtEmailChanges(): Disposable {
        return RxTextView.textChanges(dataBinding.etEmail)
                .filter { it.isNotEmpty() }
                .map { it.toString() }
                .subscribeBy(
                        onNext = ::handleEmailChanges,
                        onError = Timber::e
                )
    }

    private fun handleEmailChanges(email: String) {
        viewModel.email = email
    }


    override fun onStop() {
        super.onStop()
        disposables.clear()
    }


}