package data.persistance.note

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import data.persistance.login.UserEntity
import domain.model.NoteModel

@Entity(
        tableName = "Notes",
        foreignKeys = [
            ForeignKey(
                    entity = UserEntity::class,
                    parentColumns = ["id"],
                    childColumns = ["userId"]
            )
        ]
)
data class NoteEntity(@PrimaryKey var id: Long? = null,
                      var userId: Long? = null,
                      var userName: String,
                      var password: String,
                      var title: String) {
    val noteModel
        get() = NoteModel(
                id = id,
                userId = userId,
                userName = userName,
                password = password,
                title = title
        )
}