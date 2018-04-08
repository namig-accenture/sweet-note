package app.views.registerfragment

import android.arch.lifecycle.ViewModel
import app.dagger.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal abstract class RegisterFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(RegisterFragmentViewModel::class)
    abstract fun provideRegisterFragmentViewModel(registerFragmentViewModel: RegisterFragmentViewModel): ViewModel
}