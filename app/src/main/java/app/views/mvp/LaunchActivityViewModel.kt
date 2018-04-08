package app.views.mvp

import android.databinding.ObservableBoolean
import app.ext.BaseViewModel
import app.views.dagger.LaunchActivityScope
import domain.usecase.login.ValidateEmailUseCase
import domain.usecase.login.ValidatePasswordUseCase
import io.reactivex.Observable
import javax.inject.Inject

@LaunchActivityScope
internal class LaunchActivityViewModel @Inject constructor(private val validateEmailUseCase: ValidateEmailUseCase,
                                                           private val validatePasswordUseCase: ValidatePasswordUseCase) : BaseViewModel() {

    var email: String? = null
    var password: String? = null
    var isValidEmail: Boolean = false
        set(value) {
            field = value
            updateLoginButtonState()
        }
    var isValidPassword: Boolean = false
        set(value) {
            field = value
            updateLoginButtonState()
        }
    val isLoginEnabled: ObservableBoolean = ObservableBoolean(false)

    private fun updateLoginButtonState() {
        isLoginEnabled.set(isValidEmail && isValidPassword)
    }

    fun isValidEmail(changes: Observable<String>): Observable<Boolean> {
        return validateEmailUseCase.process(changes)
    }

    fun isValidPassword(changes: Observable<String>): Observable<Boolean> {
        return validatePasswordUseCase.process(changes)
    }
}