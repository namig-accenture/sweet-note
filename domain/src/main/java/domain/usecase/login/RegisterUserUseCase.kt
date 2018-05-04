package domain.usecase.login

import domain.model.UserModel
import domain.repositories.UserRepository
import domain.usecase.CompletableParametrizedUseCase
import io.reactivex.Completable

class RegisterUserUseCase(private val userRepository: UserRepository) : CompletableParametrizedUseCase<UserModel>() {
    override fun build(param: UserModel): Completable {
        return userRepository.registerUser(param)
                .flatMapCompletable { userRepository.saveCurrentUser(it) }
    }
}