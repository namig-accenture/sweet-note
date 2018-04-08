package app.dagger

import android.app.Activity
import app.views.launchactivity.LaunchActivity
import app.views.launchactivity.LaunchActivitySubcomponent
import dagger.Binds
import dagger.Module
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap

@Module(subcomponents = [LaunchActivitySubcomponent::class])
internal abstract class ActivityBindingModule {

    @Binds
    @IntoMap
    @ActivityKey(LaunchActivity::class)
    abstract fun provideActivity(builder: LaunchActivitySubcomponent.Builder): AndroidInjector.Factory<out Activity>
}