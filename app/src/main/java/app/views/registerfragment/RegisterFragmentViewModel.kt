package app.views.registerfragment

import android.databinding.ObservableBoolean
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
    var email: String? = null
    var password: String? = null
    var isValidEmail = false
        set(value) {
            field = value
            updateButtonState()
        }
    var isValidPassword = false
        set(value) {
            field = value
            updateButtonState()
        }
    val isButtonEnabled: ObservableBoolean = ObservableBoolean(false)

    private fun updateButtonState() {
        isButtonEnabled.set(isValidEmail && isValidPassword)
    }

    fun isValidEmail(stream: Observable<String>): Observable<Boolean> {
        return validateEmailUseCase.withUpstream(stream)
    }

    fun isValidPassword(stream: Observable<String>): Observable<Boolean> {
        return validatePasswordUseCase.withUpstream(stream)
    }

    fun registerUser(): Completable {
        return email?.let email@{ email ->
            password?.let password@{ password ->
                registerUserUseCase.get(UserModel(email = email, password = password))
            } ?: return Completable.error(IllegalAccessError("Password is null"))
        } ?: Completable.error(IllegalAccessError("Email is null"))
    }
}