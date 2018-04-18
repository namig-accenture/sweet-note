package data.repositories

import data.extensions.asEntity
import data.persistance.login.UserDao
import domain.model.UserModel
import domain.repositories.UserRepository
import io.reactivex.Completable

class UserRepositoryImpl(private val userDao: UserDao) : UserRepository {
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