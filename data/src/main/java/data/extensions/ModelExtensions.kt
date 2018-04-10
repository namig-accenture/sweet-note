package data.extensions

import data.persistance.login.UserEntity
import domain.model.UserModel

val UserModel.asEntity get() = UserEntity(email = this.email, password = this.password)