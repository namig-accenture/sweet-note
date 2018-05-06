package app.views.registerfragment

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import android.view.View
import app.ext.BasePresenter
import app.ext.log
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy

@Suppress("UNUSED_PARAMETER")
internal class RegisterFragmentPresenter(private val registerFragment: RegisterFragment) : BasePresenter() {
    private lateinit var disposables: CompositeDisposable


    private val viewModel by lazy { registerFragment.registerFragmentViewModel }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun createDisposables() {
        disposables = CompositeDisposable()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun clearDisposables() {
        disposables.clear()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun initObservers() {
        disposables += viewModel.observeEtEmailChanges(
                onNext = registerFragment::handleEtEmailChanges,
                onError = { it.log<RegisterFragmentPresenter>("while validating email.") }
        )
        disposables += viewModel.observeEtPasswordChanges(
                onNext = registerFragment::handleEtPasswordChanges,
                onError = { it.log<RegisterFragmentPresenter>("While validating password.") }
        )
    }

    fun onEtEmailChanged(email: CharSequence?, start: Int, before: Int, count: Int) {
        email?.let { viewModel.emailObserver.onNext(it) }
    }

    fun onEtPasswordChanged(password: CharSequence?, start: Int, before: Int, count: Int) {
        password?.let { viewModel.passwordObserver.onNext(it) }
    }

    fun onRegisterButtonClicked(view: View) {
        disposables += viewModel.registerUser()
                .subscribeBy(
                        onComplete = registerFragment::handleUserRegistration,
                        onError = registerFragment::handleUserRegistrationError
                )
    }
}