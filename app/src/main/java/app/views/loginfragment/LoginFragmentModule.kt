package app.views.loginfragment

import android.arch.lifecycle.ViewModel
import app.dagger.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal abstract class LoginFragmentModule {

    @Binds
    @IntoMap
    @LoginFragmentScope
    @ViewModelKey(LoginFragmentViewModel::class)
    abstract fun provideLoginFragmentViewModel(loginFragmentViewModel: LoginFragmentViewModel): ViewModel
}