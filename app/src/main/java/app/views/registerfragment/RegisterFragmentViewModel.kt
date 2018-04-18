package app.views.registerfragment

import android.databinding.ObservableBoolean
import app.ext.BaseViewModel
import domain.usecase.login.ValidateEmailUseCase
import domain.usecase.login.ValidatePasswordUseCase
import io.reactivex.Observable

internal class RegisterFragmentViewModel(private val validateEmailUseCase: ValidateEmailUseCase,
                                         private val validatePasswordUseCase: ValidatePasswordUseCase) : BaseViewModel() {
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
}