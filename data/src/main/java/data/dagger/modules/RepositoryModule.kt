package data.dagger.modules

import dagger.Module
import dagger.Provides
import data.dagger.scopes.AppScope
import data.repositories.UserRepositoryImpl
import domain.repositories.UserRepository

@Module
class RepositoryModule {

    @AppScope
    @Provides
    fun provideUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository = userRepositoryImpl
}