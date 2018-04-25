package app.view

import app.koin.AppModule
import domain.extensions.asOptional
import domain.model.UserModel
import domain.repositories.UserRepository
import io.reactivex.Completable
import io.reactivex.Single
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import tests.domain.safeEq

class TestModule : AppModule() {
    companion object {
        const val VALID_EMAIL = "a@b.co"
        const val VALID_PASSWORD = "abcAbc123@"
    }

    override val repository: Module
        get() = applicationContext {
            bean {
                Mockito.mock(UserRepository::class.java).apply {
                    `when`(registerUser(safeEq(UserModel(email = VALID_EMAIL, password = VALID_PASSWORD)))).thenReturn(Single.just(1))
                    `when`(saveLoggedInUserId(ArgumentMatchers.anyLong())).thenReturn(Completable.complete())
                    `when`(logUserIn(anyString(), anyString())).thenReturn(Single.just(UserModel(id = 1, email = VALID_EMAIL, password = VALID_PASSWORD).asOptional))
                } as UserRepository
            }
        }
}