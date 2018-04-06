package app.dagger

import android.app.Activity
import app.views.LaunchActivity
import app.views.dagger.LaunchActivityComponent
import dagger.Binds
import dagger.Module
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap

@Module(subcomponents = [LaunchActivityComponent::class])
internal abstract class ActivityBindingModule {

    @Binds
    @IntoMap
    @ActivityKey(LaunchActivity::class)
    abstract fun provideLauncherActivity(builder: LaunchActivityComponent.Builder): AndroidInjector.Factory<out Activity>
}