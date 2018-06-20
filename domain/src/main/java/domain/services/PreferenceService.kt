package domain.services

import com.fernandocejas.arrow.optional.Optional
import domain.model.UserModel
import io.reactivex.Completable

interface PreferenceService {
    fun saveUser(user: UserModel)
    fun removeUser(): Completable
    val currentUser: Optional<UserModel>
}