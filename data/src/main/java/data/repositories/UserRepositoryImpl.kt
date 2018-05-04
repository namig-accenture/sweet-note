package data.repositories

import com.fernandocejas.arrow.optional.Optional
import data.extensions.asEntity
import data.persistance.login.UserDao
import domain.extensions.asOptional
import domain.model.UserModel
import domain.repositories.UserRepository
import domain.services.PreferenceService
import io.reactivex.Completable
import io.reactivex.Single

class UserRepositoryImpl(private val userDao: UserDao,
                         private val preferenceService: PreferenceService) : UserRepository {

    override fun saveCurrentUser(user: UserModel): Completable {
        return Completable.fromAction { preferenceService.saveUser(user) }
    }

    override val currentUser: Single<Optional<UserModel>>
        get() = Single.fromCallable { preferenceService.currentUser }

    override fun registerUser(userModel: UserModel): Single<UserModel> {
        return Single.fromCallable { userModel.copy(id = userDao.insert(userModel.asEntity)) }
    }

    override fun logUserIn(email: String, password: String): Single<Optional<UserModel>> {
        return Single.fromCallable { userDao.findUserByEmailAndPassword(email, password)?.userModel.asOptional }
    }

    override fun logUserOut(userModel: UserModel): Completable = Completable.complete()
}