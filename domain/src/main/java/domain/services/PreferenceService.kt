package domain.services

import com.fernandocejas.arrow.optional.Optional
import domain.model.UserModel

interface PreferenceService {
    fun saveUser(user: UserModel)
    val currentUser: Optional<UserModel>
}