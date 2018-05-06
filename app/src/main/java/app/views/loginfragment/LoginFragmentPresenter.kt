package app.views.loginfragment

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import android.view.View
import app.ext.BasePresenter
import app.ext.log
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy

@Suppress("UNUSED_PARAMETER")
internal class LoginFragmentPresenter(private val loginFragment: LoginFragment) : BasePresenter() {

    val viewModel by lazy { loginFragment.loginFragmentViewModel }

    private lateinit var disposables: CompositeDisposable

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun createDisposables() {
        disposables = CompositeDisposable()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun initDisposables() {
        disposables += viewModel.observeEtEmailChanges(
                onNext = loginFragment::handleEmailChanges,
                onError = { it.log<LoginFragmentPresenter>("While observing email changes") }
        )
        disposables += viewModel.observeEtPasswordChanges(
                onNext = loginFragment::handlePasswordChanges,
                onError = { it.log<LoginFragmentPresenter>("While observing password changes") }
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun clearDisposables() {
        disposables.clear()
    }

    fun onEtEmailChanged(email: CharSequence?, start: Int, before: Int, count: Int) {
        email?.let { viewModel.emailObserver.onNext(it) }
    }

    fun onEtPasswordChanged(password: CharSequence?, start: Int, before: Int, count: Int) {
        password?.let { viewModel.passwordObserver.onNext(it) }
    }

    fun onloginButtonClicked(view: View) {
        viewModel.login()
                .subscribeBy(
                        onComplete = loginFragment::handleLogin,
                        onError = loginFragment::handleLoginError
                )
    }
}