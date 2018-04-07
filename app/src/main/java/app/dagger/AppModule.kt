package app.dagger

import android.app.Application
import app.App
import app.executor.JobExecutor
import app.executor.UIThread
import dagger.Binds
import dagger.Module
import dagger.android.support.AndroidSupportInjectionModule
import domain.executor.PostExecutionThread
import domain.executor.ThreadExecutor
import domain.transformers.AndroidSchedulerTransformer
import domain.transformers.SchedulerTransformer

@Module(includes = [AndroidSupportInjectionModule::class, ActivityBindingModule::class, ViewModelBindingModule::class])
internal abstract class AppModule {

    @Binds
    @AppScope
    abstract fun getApplication(app: App): Application

    @Binds
    @AppScope
    abstract fun getApp(app: App): App

    @Binds
    @AppScope
    abstract fun provideSchedulerTransformer(androidSchedulerTransformer: AndroidSchedulerTransformer): SchedulerTransformer

    @Binds
    @AppScope
    abstract fun provideThreadExecutor(jobExecutor: JobExecutor): ThreadExecutor

    @Binds
    @AppScope
    abstract fun providePostExecutionThread(uiThread: UIThread): PostExecutionThread
}