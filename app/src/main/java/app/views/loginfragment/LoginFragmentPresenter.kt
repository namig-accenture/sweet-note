package app.views.loginfragment

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

internal class LoginFragmentPresenter(private val loginFragment: LoginFragment) : BasePresenter() {

    val viewModel by lazy { loginFragment.loginFragmentViewModel }
    val dataBinding by lazy { loginFragment.dataBinding }

    private lateinit var disposables: CompositeDisposable

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun createDisposables() {
        disposables = CompositeDisposable()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun initDisposables() {
        disposables += observeEtEmailChanges()
        disposables += observeEtPasswordChanges()
        disposables += observeLoginButtonClick()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun clearDisposables() {
        disposables.clear()
    }

    private fun observeEtEmailChanges(): Disposable {
        return RxTextView.textChanges(dataBinding.etEmail)
                .skip(1)
                .map(CharSequence::toString)
                .doOnNext(viewModel.email::set)
                .map { it.isNotEmpty() && it.isNotBlank() }
                .doOnNext(viewModel.validEmail::set)
                .subscribeBy(
                        onNext = loginFragment::handleEmailChanges,
                        onError = Timber::e
                )
    }

    private fun observeEtPasswordChanges(): Disposable {
        return RxTextView.textChanges(dataBinding.etPassword)
                .skip(1)
                .map(CharSequence::toString)
                .doOnNext(viewModel.password::set)
                .map { it.isNotEmpty() && it.isNotBlank() }
                .doOnNext(viewModel.validPassword::set)
                .subscribeBy(
                        onNext = loginFragment::handlePasswordChanges,
                        onError = Timber::e
                )
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
                        onComplete = loginFragment::handleLogin,
                        onError = loginFragment::handleLoginError
                )
    }
}