package app.views.registerfragment

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import app.ext.BasePresenter
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber

internal class RegisterFragmentPresenter(private val registerFragment: RegisterFragment) : BasePresenter() {
    private lateinit var disposables: CompositeDisposable

    private val dataBinding by lazy { registerFragment.dataBinding }
    private val viewModel by lazy { registerFragment.registerFragmentViewModel }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun createDisposables() {
        disposables = CompositeDisposable()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun initDisposables() {
        disposables += observeEtEmailChanges()
        disposables += observeEtPasswordChanges()
        disposables += observeRegisterButtonClick()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun clearDisposables() {
        disposables.clear()
    }

    private fun observeEtEmailChanges(): Disposable {
        return RxTextView.textChanges(dataBinding.etEmail)
                .filter(CharSequence::isNotEmpty)
                .map(CharSequence::toString)
                .doOnNext(viewModel.email::set)
                .window(1)
                .flatMap(viewModel::isValidEmail)
                .doOnNext(viewModel.validEmail::set)
                .subscribeBy(
                        onNext = registerFragment::handleEtEmailChanges,
                        onError = Timber::e
                )
    }

    private fun observeEtPasswordChanges(): Disposable {
        return RxTextView.textChanges(dataBinding.etPassword)
                .filter(CharSequence::isNotEmpty)
                .map(CharSequence::toString)
                .doOnNext(viewModel.password::set)
                .window(1)
                .flatMap(viewModel::isValidPassword)
                .doOnNext(viewModel.validPassword::set)
                .subscribeBy(
                        onNext = registerFragment::handleEtPasswordChanges,
                        onError = Timber::e
                )
    }

    private fun observeRegisterButtonClick(): Disposable {
        return RxView.clicks(dataBinding.btnRegister)
                .firstElement()
                .subscribeBy(
                        onSuccess = { disposables += observeRegistrationOfUser() },
                        onError = Timber::e
                )
    }

    private fun observeRegistrationOfUser(): Disposable {
        return viewModel.registerUser()
                .subscribeBy(
                        onComplete = registerFragment::handleUserRegistration,
                        onError = Timber::e
                )
    }
}