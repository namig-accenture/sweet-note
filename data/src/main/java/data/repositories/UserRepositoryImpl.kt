package data.repositories

import com.fernandocejas.arrow.optional.Optional
import data.extensions.asEntity
import domain.extensions.asOptional
import data.persistance.login.UserDao
import domain.model.UserModel
import domain.repositories.UserRepository
import domain.services.PreferenceService
import io.reactivex.Completable
import io.reactivex.Single

class UserRepositoryImpl(private val userDao: UserDao,
                         private val preferenceService: PreferenceService) : UserRepository {
    override fun saveLoggedInUserId(id: Long): Completable {
        return Completable.fromAction { preferenceService.saveUserId(id) }
    }

    override val loggedInUserId: Single<Long>
        get() = Single.fromCallable { preferenceService.userId }

    override fun registerUser(userModel: UserModel): Single<Long> {
        return Single.fromCallable { userDao.insert(userModel.asEntity) }
    }

    override fun logUserIn(email: String, password: String): Single<Optional<UserModel>> {
        return Single.fromCallable { userDao.findUserByEmailAndPassword(email, password)?.userModel.asOptional }
    }

    override fun logUserOut(userModel: UserModel): Completable = Completable.complete()
}