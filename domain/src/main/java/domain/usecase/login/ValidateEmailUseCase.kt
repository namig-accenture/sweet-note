package domain.usecase.login

import domain.usecase.FlowableUseCase
import io.reactivex.Observable

class ValidateEmailUseCase : FlowableUseCase<String, Boolean>() {
    override fun process(upstream: Observable<String>): Observable<Boolean> {
        return upstream.map(::isValidEmail).distinctUntilChanged().defaultIfEmpty(false)
    }

    private fun isValidEmail(email: String): Boolean {
        return email.matches(REGEX.toRegex())
    }

    companion object {
        const val REGEX = "(?:[a-z0-9._]+[@][a-z0-9]+)(?:\\.[a-z0-9]{2,3})+"
    }
}