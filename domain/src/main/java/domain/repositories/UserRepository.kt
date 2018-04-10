package domain.repositories

import domain.model.UserModel
import io.reactivex.Completable

interface UserRepository {
    fun registerUser(userModel: UserModel): Long
    fun logUserIn(email: String, password: String): UserModel?
    fun logUserOut(userModel: UserModel): Completable
}