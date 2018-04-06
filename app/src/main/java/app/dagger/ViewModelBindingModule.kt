package app.dagger

import android.arch.lifecycle.ViewModel
import app.views.mvvm.LaunchActivityModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal abstract class ViewModelBindingModule {

    @Binds
    @IntoMap
    @ViewModelKey(LaunchActivityModel::class)
    abstract fun provideLaunchActivityModel(launchActivityModel: LaunchActivityModel): ViewModel
}