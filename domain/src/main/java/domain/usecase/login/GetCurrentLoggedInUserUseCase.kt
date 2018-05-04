package domain.usecase.login

import com.fernandocejas.arrow.optional.Optional
import domain.model.UserModel
import domain.repositories.UserRepository
import domain.usecase.SingleUseCase
import io.reactivex.Single

class GetCurrentLoggedInUserUseCase(private val userRepository: UserRepository) : SingleUseCase<Optional<UserModel>>() {
    override fun build(): Single<Optional<UserModel>> {
        return userRepository.currentUser
    }
}