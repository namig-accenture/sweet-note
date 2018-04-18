package domain.usecase.login

import domain.usecase.FlowableUseCase
import io.reactivex.Observable

class ValidatePasswordUseCase : FlowableUseCase<String, Boolean>() {
    override fun process(upstream: Observable<String>): Observable<Boolean> {
        return upstream.map(::isValidPassword)
    }

    private fun isValidPassword(string: String) = string.matches(REGEX.toRegex())

    companion object {
        const val REGEX = "^(?=[a-zA-Z0-9@#\$%^&*()_+=]{8,})(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@#\$%^&*()_+=]).*\$"
    }
}