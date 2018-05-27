package domain.usecase.note

import domain.exceptions.UserNotFoundException
import domain.extensions.fromOptional
import domain.repositories.UserRepository
import domain.usecase.SingleUseCase
import domain.usecase.login.GetCurrentLoggedInUserUseCase
import io.reactivex.Single

class GetNotesCountUseCase(private val getCurrentLoggedInUserUseCase: GetCurrentLoggedInUserUseCase,
                           private val userRepository: UserRepository)
    : SingleUseCase<Int>() {
    override fun build(): Single<Int> {
        return getCurrentLoggedInUserUseCase.chain()
                .flatMap {
                    it.fromOptional {
                        userRepository.findNotesCountByUserId(it)
                    } ?: throw UserNotFoundException("No currently logged in user found.")
                }
    }
}