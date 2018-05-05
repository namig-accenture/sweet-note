package app

import app.koin.AppModule
import domain.extensions.asOptional
import domain.model.UserModel
import domain.repositories.UserRepository
import domain.safeEq
import io.reactivex.Completable
import io.reactivex.Single
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString

class TestModule : AppModule() {
    companion object {
        const val ID = 1L
        const val VALID_EMAIL = "a@b.co"
        const val VALID_PASSWORD = "abcAbc123@"
        const val PIN = "1234"
        val user = UserModel(email = VALID_EMAIL, password = VALID_PASSWORD)
    }

    override val repository: Module
        get() = applicationContext {
            bean {
                Mockito.mock(UserRepository::class.java).apply {
                    `when`(registerUser(safeEq(user))).thenReturn(Single.just(user.copy(id = ID)))
                    `when`(saveCurrentUser(safeEq(user.copy(id = ID)))).thenReturn(Completable.complete())
                    `when`(saveCurrentUser(safeEq(user.copy(id = ID, pin = PIN)))).thenReturn(Completable.complete())
                    `when`(logUserIn(anyString(), anyString())).thenReturn(Single.just(user.copy(id = ID).asOptional))
                    `when`(currentUser).thenReturn(Single.just(user.copy(id = ID).asOptional))
                } as UserRepository
            }
        }
}