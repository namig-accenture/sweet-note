package app.views.loginfragment

import android.databinding.ObservableBoolean
import app.ext.BaseViewModel
import domain.model.UserModel
import domain.usecase.login.LogUserInUseCase
import io.reactivex.Completable

internal class LoginFragmentViewModel(private val logUserInUseCase: LogUserInUseCase) : BaseViewModel() {
    sealed class ExceptionCase : IllegalAccessException() {
        class EmailNotDefined : ExceptionCase()
        class PasswordNotDefined : ExceptionCase()
    }

    var email: String? = null
        set(value) {
            field = value
            updateButtonState()
        }
    var password: String? = null
        set(value) {
            field = value
            updateButtonState()
        }

    val isLoginButtonEnabled = ObservableBoolean(false)

    private fun updateButtonState() {
        isLoginButtonEnabled.set(email != null && password != null)
    }

    fun login(): Completable {
        return email?.let { email ->
            password?.let { password ->
                logUserInUseCase.get(UserModel(email = email, password = password))
            } ?: Completable.error(ExceptionCase.PasswordNotDefined())
        } ?: Completable.error(ExceptionCase.EmailNotDefined())
    }
}