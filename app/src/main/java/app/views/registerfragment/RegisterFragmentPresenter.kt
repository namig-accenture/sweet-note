package app.views.registerfragment

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import android.view.View
import app.ext.BasePresenter
import app.ext.log
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
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

    fun onEtEmailChanged(email: CharSequence?, start: Int, before: Int, count: Int) {
        email?.toString()?.let {
            viewModel.run {
                this.email.set(it)
                disposables += observeEmailValidation(Observable.just(it))
            }
        }
    }

    private fun observeEmailValidation(stream: Observable<String>): Disposable {
        return viewModel.run {
            isValidEmail(stream)
                    .doOnNext(validEmail::set)
                    .subscribeBy(
                            onNext = registerFragment::handleEtEmailChanges,
                            onError = { it.log<RegisterFragmentPresenter>("While validating email.") }
                    )
        }
    }

    fun onEtPasswordChanged(password: CharSequence?, start: Int, before: Int, count: Int) {
        password?.toString()?.let {
            viewModel.apply {
                this.password.set(it)
                disposables += observePasswordValidation(Observable.just(it))
            }
        }
    }

    private fun observePasswordValidation(stream: Observable<String>): Disposable {
        return viewModel.run {
            isValidPassword(stream)
                    .doOnNext(validPassword::set)
                    .subscribeBy(
                            onNext = registerFragment::handleEtPasswordChanges,
                            onError = { it.log<RegisterFragmentPresenter>("While validating password.") }
                    )
        }
    }

    fun onRegisterButtonClicked(view: View) {
        disposables += viewModel.registerUser()
                .subscribeBy(
                        onComplete = registerFragment::handleUserRegistration,
                        onError = registerFragment::handleUserRegistrationError
                )
    }
}