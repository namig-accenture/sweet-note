package app.views.registerfragment

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import app.ext.BaseViewModel
import domain.model.UserModel
import domain.usecase.login.RegisterUserUseCase
import domain.usecase.login.ValidateEmailUseCase
import domain.usecase.login.ValidatePasswordUseCase
import io.reactivex.Completable
import io.reactivex.Observable

internal class RegisterFragmentViewModel(private val validateEmailUseCase: ValidateEmailUseCase,
                                         private val validatePasswordUseCase: ValidatePasswordUseCase,
                                         private val registerUserUseCase: RegisterUserUseCase) : BaseViewModel() {
    val email = ObservableField<String?>()
    val password = ObservableField<String?>()
    val validEmail = ObservableBoolean(false)
    val validPassword = ObservableBoolean(false)

    fun isValidEmail(stream: Observable<String>): Observable<Boolean> {
        return validateEmailUseCase.withUpstream(stream)
    }

    fun isValidPassword(stream: Observable<String>): Observable<Boolean> {
        return validatePasswordUseCase.withUpstream(stream)
    }

    fun registerUser(): Completable {
        return when {
            email.get() == null -> Completable.error(ValidationError.InvalidEmail())
            password.get() == null -> Completable.error(ValidationError.InvalidPassword())
            else -> registerUserUseCase.get(UserModel(email = email.get()!!, password = password.get()!!))
        }
    }

    sealed class ValidationError : IllegalAccessException() {
        class InvalidEmail : ValidationError()
        class InvalidPassword : ValidationError()
    }
}