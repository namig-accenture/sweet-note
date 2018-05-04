package domain.usecase.login

import domain.exceptions.CurrentUserNotFoundException
import domain.extensions.fromOptional
import domain.repositories.UserRepository
import domain.usecase.SingleParametrisedUseCase
import io.reactivex.Single

class ValidatePinUseCase(private val userRepository: UserRepository) : SingleParametrisedUseCase<String, Boolean>() {
    override fun build(param: String): Single<Boolean> {
        return userRepository.currentUser
                .map { optionalUser ->
                    optionalUser.fromOptional { user ->
                        user.pin == param
                    } ?: throw CurrentUserNotFoundException("No current user found")
                }
    }
}