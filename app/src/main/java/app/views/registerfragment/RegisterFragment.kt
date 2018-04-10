package app.views.registerfragment

import android.os.Bundle
import app.ext.BaseFragment
import com.example.namigtahmazli.sweetnote.BR
import com.example.namigtahmazli.sweetnote.R
import com.example.namigtahmazli.sweetnote.databinding.FragmentRegisterBinding
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy

@RegisterFragmentScope
internal class RegisterFragment : BaseFragment<RegisterFragmentViewModel, FragmentRegisterBinding>() {
    override val viewModel: RegisterFragmentViewModel
        get() = provideViewModel()
    override val layoutRes: Int
        get() = R.layout.fragment_register
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
        disposables += observeRegisterButtonClick()
    }

    override fun onStop() {
        disposables.clear()
        super.onStop()
    }

    private fun observeRegisterButtonClick(): Disposable {
        return RxView.clicks(dataBinding.btnRegister)
                .firstElement()
                .subscribeBy(
                        onSuccess = {}
                )
    }

    private fun observeEtPasswordChanges(): Disposable {
        return RxTextView.textChanges(dataBinding.etPassword)
                .filter { !it.isEmpty() }
                .map { it.toString() }
                .doOnNext { viewModel.password = it }
                .window(1)
                .flatMap(viewModel::isValidPassword)
                .doOnNext { viewModel.isValidPassword = it }
                .subscribeBy(
                        onNext = (::handleEtPasswordChanges)
                )
    }

    private fun handleEtPasswordChanges(isValid: Boolean) {
        dataBinding.passwordLayout.error = if (isValid) null else "Invalid"
    }

    private fun observeEtEmailChanges(): Disposable {
        return RxTextView.textChanges(dataBinding.etEmail)
                .filter { !it.isEmpty() }
                .map { it.toString() }
                .doOnNext { viewModel.email = it }
                .window(1)
                .flatMap(viewModel::isValidEmail)
                .doOnNext { viewModel.isValidEmail = it }
                .subscribeBy(
                        onNext = (::handleEtEmailChanges)
                )
    }

    private fun handleEtEmailChanges(isValid: Boolean) {
        dataBinding.emailLayout.error = if (isValid) null else "Invalid"
    }
}