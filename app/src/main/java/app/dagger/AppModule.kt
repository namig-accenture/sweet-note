package app.dagger

import android.app.Application
import app.App
import dagger.Binds
import dagger.Module
import dagger.android.support.AndroidSupportInjectionModule

@Module(includes = [AndroidSupportInjectionModule::class, ActivityBindingModule::class, ViewModelBindingModule::class])
internal abstract class AppModule {

    @Binds
    @AppScope
    abstract fun getApplication(app: App): Application

    @Binds
    @AppScope
    abstract fun getApp(app: App): App
}