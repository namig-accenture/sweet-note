package data.repositories

import data.dagger.scopes.AppScope
import data.extensions.asEntity
import data.persistance.login.UserDao
import domain.model.UserModel
import domain.repositories.UserRepository
import io.reactivex.Completable
import javax.inject.Inject

@AppScope
class UserRepositoryImpl @Inject constructor(private val userDao: UserDao) : UserRepository {
    override fun registerUser(userModel: UserModel): Long {
        return userDao.insert(userModel.asEntity)
    }

    override fun logUserIn(email: String, password: String): UserModel? {
        return userDao.findUserByEmailAndPassword(email, password)?.userModel
    }

    override fun logUserOut(userModel: UserModel): Completable {
        return Completable.complete()
    }
}