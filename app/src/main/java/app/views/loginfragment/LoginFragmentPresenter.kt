package app.views.loginfragment

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import android.view.View
import app.ext.BasePresenter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

@Suppress("UNUSED_PARAMETER")
internal class LoginFragmentPresenter(private val loginFragment: LoginFragment) : BasePresenter() {

    val viewModel by lazy { loginFragment.loginFragmentViewModel }

    private lateinit var disposables: CompositeDisposable
    private lateinit var emailObserver: PublishSubject<CharSequence>
    private lateinit var passwordObserver: PublishSubject<CharSequence>

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun createDisposables() {
        emailObserver = PublishSubject.create()
        passwordObserver = PublishSubject.create()
        disposables = CompositeDisposable()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun initDisposables() {
        disposables += observeEtEmailChanges()
        disposables += observeEtPasswordChanges()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun clearDisposables() {
        disposables.clear()
    }

    fun onEtEmailChanged(email: CharSequence?, start: Int, before: Int, count: Int) {
        email?.let { emailObserver.onNext(it) }
    }

    fun onEtPasswordChanged(password: CharSequence?, start: Int, before: Int, count: Int) {
        password?.let { passwordObserver.onNext(it) }
    }

    private fun observeEtEmailChanges(): Disposable {
        return emailObserver
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
        return passwordObserver
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

    fun onloginButtonClicked(view: View) {
        viewModel.login()
                .subscribeBy(
                        onComplete = loginFragment::handleLogin,
                        onError = loginFragment::handleLoginError
                )
    }
}