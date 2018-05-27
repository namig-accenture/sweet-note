package data.extensions

import data.persistance.login.UserEntity
import data.persistance.note.NoteEntity
import domain.model.NoteModel
import domain.model.UserModel

val UserModel.asEntity get() = UserEntity(email = this.email, password = this.password)
val NoteModel.asEntity
    get() = NoteEntity(
            userId = this.userId,
            userName = this.userName,
            password = this.password,
            title = this.title
    )