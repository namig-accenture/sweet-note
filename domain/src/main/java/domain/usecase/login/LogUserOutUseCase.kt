package domain.usecase.login

import domain.repositories.UserRepository
import domain.usecase.CompletableUseCase
import io.reactivex.Completable

class LogUserOutUseCase(private val userRepository: UserRepository)
    : CompletableUseCase() {
    override fun build(): Completable {
        return userRepository.logUserOut()
    }
}