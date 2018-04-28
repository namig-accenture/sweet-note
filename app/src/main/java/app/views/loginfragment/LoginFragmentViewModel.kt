package app.views.loginfragment

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import app.ext.BaseViewModel
import domain.model.UserModel
import domain.usecase.login.LogUserInUseCase
import io.reactivex.Completable

internal class LoginFragmentViewModel(private val logUserInUseCase: LogUserInUseCase) : BaseViewModel() {
    sealed class ExceptionCase : IllegalAccessException() {
        class EmailNotDefined : ExceptionCase()
        class PasswordNotDefined : ExceptionCase()
    }

    var email = ObservableField<String?>()
    var password = ObservableField<String?>()
    val validEmail = ObservableBoolean(false)
    val validPassword = ObservableBoolean(false)

    fun login(): Completable {
        return when {
            email.get() == null -> Completable.error(ExceptionCase.EmailNotDefined())
            password.get() == null -> Completable.error(ExceptionCase.PasswordNotDefined())
            else -> logUserInUseCase.get(UserModel(email = email.get()!!, password = password.get()!!))
        }
    }
}