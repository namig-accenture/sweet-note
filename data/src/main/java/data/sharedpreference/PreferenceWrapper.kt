package data.sharedpreference

import android.content.SharedPreferences
import domain.services.PreferenceService

class PreferenceWrapper(private val sharedPreferences: SharedPreferences) : PreferenceService {
    companion object {
        const val LOGGED_IN_USER_ID = "LOGGED_IN_USER_ID"
    }

    override val userId: Long
        get() = sharedPreferences.getLong(LOGGED_IN_USER_ID, 0)

    override fun saveUserId(id: Long) {
        sharedPreferences
                .edit()
                .putLong(LOGGED_IN_USER_ID, id)
                .apply()
    }
}