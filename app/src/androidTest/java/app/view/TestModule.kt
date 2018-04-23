package app.view

import app.koin.AppModule
import domain.model.UserModel
import domain.repositories.UserRepository
import io.reactivex.Completable
import io.reactivex.Single
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext
import org.mockito.ArgumentMatchers
import org.mockito.Mockito

class TestModule : AppModule() {
    companion object {
        const val VALID_EMAIL = "a@b.co"
        const val VALID_PASSWORD = "abcAbc123@"
    }

    override val repository: Module
        get() = applicationContext {
            bean {
                Mockito.mock(UserRepository::class.java).apply {
                    Mockito.`when`(registerUser(safeEq(UserModel(email = VALID_EMAIL, password = VALID_PASSWORD)))).thenReturn(Single.just(1))
                    Mockito.`when`(saveLoggedInUserId(ArgumentMatchers.anyLong())).thenReturn(Completable.complete())
                } as UserRepository
            }
        }
}