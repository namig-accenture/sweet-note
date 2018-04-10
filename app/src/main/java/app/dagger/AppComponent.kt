package app.dagger

import app.App
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import data.dagger.modules.DatabaseModule
import data.dagger.modules.RepositoryModule
import data.dagger.scopes.AppScope

@AppScope
@Component(modules = [AppModule::class])
internal interface AppComponent : AndroidInjector<App> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<App>() {
        @BindsInstance
        abstract fun bindDatabaseModule(databaseModule: DatabaseModule): Builder

        @BindsInstance
        abstract fun bindRepositoryModule(repositoryModule: RepositoryModule): Builder
    }
}