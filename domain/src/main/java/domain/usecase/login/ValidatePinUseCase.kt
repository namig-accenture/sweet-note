package domain.usecase.login

import domain.exceptions.CurrentUserNotFoundException
import domain.extensions.fromOptional
import domain.repositories.UserRepository
import domain.usecase.SingleParametrisedUseCase
import io.reactivex.Single

class ValidatePinUseCase(private val getCurrentLoggedInUserUseCase: GetCurrentLoggedInUserUseCase) : SingleParametrisedUseCase<String, Boolean>() {
    override fun build(param: String): Single<Boolean> {
        return getCurrentLoggedInUserUseCase.chain()
                .map { optionalUser ->
                    optionalUser.fromOptional { user ->
                        user.pin == param
                    } ?: throw CurrentUserNotFoundException("No current user found")
                }
    }
}