package domain.model

import java.io.Serializable

data class NoteModel(val id: Long? = null,
                     val userId: Long? = null,
                     val userName: String,
                     val password: String,
                     val title: String) : Serializable