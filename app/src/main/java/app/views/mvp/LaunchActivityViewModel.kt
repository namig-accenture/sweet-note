package app.views.mvp

import app.ext.BaseViewModel
import app.views.dagger.LaunchActivityScope
import domain.usecase.login.ValidateEmailUseCase
import domain.usecase.login.ValidatePasswordUseCase
import io.reactivex.Observable
import javax.inject.Inject

@LaunchActivityScope
internal class LaunchActivityViewModel @Inject constructor(private val validateEmailUseCase: ValidateEmailUseCase,
                                                           private val validatePasswordUseCase: ValidatePasswordUseCase) : BaseViewModel() {
    fun isValidEmail(changes: Observable<String>): Observable<Boolean> {
        return validateEmailUseCase.process(changes)
    }

    fun isValidPassword(changes: Observable<String>): Observable<Boolean> {
        return validatePasswordUseCase.process(changes)
    }
}