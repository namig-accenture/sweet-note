package domain.usecase.login

import domain.exceptions.UserNotFoundException
import domain.extensions.fromOptional
import domain.model.UserModel
import domain.repositories.UserRepository
import domain.usecase.CompletableParametrizedUseCase
import io.reactivex.Completable

class LogUserInUseCase(private val userRepository: UserRepository) : CompletableParametrizedUseCase<UserModel>() {
    override fun build(param: UserModel): Completable {
        return userRepository.logUserIn(email = param.email, password = param.password)
                .flatMapCompletable { optionalUser ->
                    optionalUser.fromOptional { userModel ->
                        userRepository.saveCurrentUser(userModel)
                    } ?: throw UserNotFoundException("User not found")
                }
    }
}