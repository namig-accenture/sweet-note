package data.sharedpreference

import android.content.SharedPreferences
import com.fernandocejas.arrow.optional.Optional
import com.squareup.moshi.Moshi
import data.extensions.fromJsonAsObject
import data.extensions.toJson
import domain.model.UserModel
import domain.services.PreferenceService

class PreferenceWrapper(private val sharedPreferences: SharedPreferences,
                        private val moshi: Moshi) : PreferenceService {
    override fun saveUser(user: UserModel) {
        sharedPreferences
                .edit()
                .putString(CURRENT_USER, user.toJson(moshi))
                .apply()
    }

    override val currentUser: Optional<UserModel>
        get() = sharedPreferences.getString(CURRENT_USER, null).fromJsonAsObject(moshi)

    companion object {
        const val CURRENT_USER = "CURRENT_USER"
    }
}