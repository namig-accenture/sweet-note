package domain.usecase.login

import domain.exceptions.CurrentUserNotFoundException
import domain.extensions.fromOptional
import domain.repositories.UserRepository
import domain.usecase.CompletableParametrizedUseCase
import io.reactivex.Completable

class EnterPinUseCase(private val userRepository: UserRepository):CompletableParametrizedUseCase<String>(){
    override fun build(param: String): Completable {
        return userRepository.currentUser
                .flatMapCompletable {optionalUser ->
                    optionalUser.fromOptional {user->
                        userRepository.saveCurrentUser(user.copy(pin = param))
                    }?:throw CurrentUserNotFoundException("No current user found")
                }
    }
}