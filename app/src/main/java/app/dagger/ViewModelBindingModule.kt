package app.dagger

import android.arch.lifecycle.ViewModel
import app.views.mvp.LaunchActivityViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal abstract class ViewModelBindingModule {

    @Binds
    @IntoMap
    @ViewModelKey(LaunchActivityViewModel::class)
    abstract fun provideLaunchActivityModel(launchActivityModel: LaunchActivityViewModel): ViewModel
}