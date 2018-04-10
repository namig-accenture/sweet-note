package app.dagger

import dagger.Module
import data.dagger.modules.DatabaseModule
import data.dagger.modules.RepositoryModule

@Module(includes = [
    DatabaseModule::class,
    RepositoryModule::class
])
internal abstract class DataModules