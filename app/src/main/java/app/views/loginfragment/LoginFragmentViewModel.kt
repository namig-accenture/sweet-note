package app.views.loginfragment

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import app.ext.BaseViewModel
import domain.model.UserModel
import domain.usecase.login.LogUserInUseCase
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject

internal class LoginFragmentViewModel(private val logUserInUseCase: LogUserInUseCase) : BaseViewModel() {
    sealed class ExceptionCase : IllegalAccessException() {
        class EmailNotDefined : ExceptionCase()
        class PasswordNotDefined : ExceptionCase()
    }

    var email = ObservableField<String?>()
    var password = ObservableField<String?>()
    val validEmail = ObservableBoolean(false)
    val validPassword = ObservableBoolean(false)
    val emailObserver: PublishSubject<CharSequence> = PublishSubject.create()
    val passwordObserver: PublishSubject<CharSequence> = PublishSubject.create()

    fun login(): Completable {
        return when {
            email.get() == null -> Completable.error(ExceptionCase.EmailNotDefined())
            password.get() == null -> Completable.error(ExceptionCase.PasswordNotDefined())
            else -> logUserInUseCase.get(UserModel(email = email.get()!!, password = password.get()!!))
        }
    }

    inline fun observeEtEmailChanges(crossinline onNext: (Boolean) -> Unit,
                                     crossinline onError: (Throwable) -> Unit): Disposable {
        return emailObserver
                .skip(1)
                .map(CharSequence::toString)
                .doOnNext(email::set)
                .map { it.isNotEmpty() && it.isNotBlank() }
                .doOnNext(validEmail::set)
                .subscribeBy(
                        onNext = { onNext(it) },
                        onError = { onError(it) }
                )
    }

    inline fun observeEtPasswordChanges(crossinline onNext: (Boolean) -> Unit,
                                        crossinline onError: (Throwable) -> Unit): Disposable {
        return passwordObserver
                .skip(1)
                .map(CharSequence::toString)
                .doOnNext(password::set)
                .map { it.isNotEmpty() && it.isNotBlank() }
                .doOnNext(validPassword::set)
                .subscribeBy(
                        onNext = { onNext(it) },
                        onError = { onError(it) }
                )
    }
}