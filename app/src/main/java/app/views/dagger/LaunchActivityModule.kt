package app.views.dagger

import android.support.v7.app.AppCompatActivity
import app.views.LaunchActivity
import dagger.Binds
import dagger.Module

@Module
internal abstract class LaunchActivityModule {

    @LaunchActivityScope
    @Binds
    abstract fun provideAppCompatActivity(launchActivity: LaunchActivity): AppCompatActivity
}