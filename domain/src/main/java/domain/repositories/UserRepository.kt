package domain.repositories

import com.fernandocejas.arrow.optional.Optional
import domain.model.UserModel
import io.reactivex.Completable
import io.reactivex.Single

interface UserRepository {
    fun registerUser(userModel: UserModel): Single<UserModel>
    fun logUserIn(email: String, password: String): Single<Optional<UserModel>>
    fun logUserOut(userModel: UserModel): Completable
    fun saveCurrentUser(user: UserModel): Completable
    val currentUser: Single<Optional<UserModel>>
}