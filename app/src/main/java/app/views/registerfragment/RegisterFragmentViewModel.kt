package app.views.registerfragment

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import app.ext.BaseViewModel
import domain.exceptions.InvalidEmailException
import domain.exceptions.InvalidPasswordException
import domain.model.UserModel
import domain.usecase.login.RegisterUserUseCase
import domain.usecase.login.ValidateEmailUseCase
import domain.usecase.login.ValidatePasswordUseCase
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject

internal class RegisterFragmentViewModel(private val validateEmailUseCase: ValidateEmailUseCase,
                                         private val validatePasswordUseCase: ValidatePasswordUseCase,
                                         private val registerUserUseCase: RegisterUserUseCase) : BaseViewModel() {
    val email = ObservableField<String?>()
    val password = ObservableField<String?>()
    val validEmail = ObservableBoolean(false)
    val validPassword = ObservableBoolean(false)
    val passwordError = ObservableField<String?>()
    val emailError = ObservableField<String?>()

    val emailObserver: PublishSubject<CharSequence> = PublishSubject.create()
    val passwordObserver: PublishSubject<CharSequence> = PublishSubject.create()

    private fun isValidEmail(stream: Observable<String>): Observable<Boolean> {
        return validateEmailUseCase.withUpstream(stream)
    }

    private fun isValidPassword(stream: Observable<String>): Observable<Boolean> {
        return validatePasswordUseCase.withUpstream(stream)
    }

    fun registerUser(): Completable {
        return when {
            email.get() == null -> Completable.error(InvalidEmailException())
            password.get() == null -> Completable.error(InvalidPasswordException())
            else -> registerUserUseCase.get(UserModel(email = email.get()!!, password = password.get()!!))
        }
    }

    inline fun observeEtEmailChanges(crossinline onNext: (Boolean) -> Unit,
                                     crossinline onError: (Throwable) -> Unit): Disposable {
        return emailObserver
                .map(CharSequence::toString)
                .doOnNext(email::set)
                .window(1)
                .flatMap { isValidEmail(it) }
                .doOnNext(validEmail::set)
                .subscribeBy(
                        onNext = { onNext(it) },
                        onError = { onError(it) }
                )
    }

    inline fun observeEtPasswordChanges(crossinline onNext: (Boolean) -> Unit,
                                        crossinline onError: (Throwable) -> Unit): Disposable {
        return passwordObserver
                .map(CharSequence::toString)
                .doOnNext(password::set)
                .window(1)
                .flatMap { isValidPassword(it) }
                .doOnNext(validPassword::set)
                .subscribeBy(
                        onNext = { onNext(it) },
                        onError = { onError(it) }
                )
    }
}