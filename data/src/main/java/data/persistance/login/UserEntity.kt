package data.persistance.login

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import domain.model.UserModel

@Entity(tableName = "User",
        indices = [Index(value = ["email"], unique = true), (Index(value = ["email", "password"]))])
data class UserEntity(@PrimaryKey var id: Long? = null,
                      var email: String,
                      var password: String) {
    val userModel: UserModel get() = UserModel(email, password)
}