package app.views.launchactivity

import android.arch.lifecycle.ViewModel
import android.support.v4.app.Fragment
import app.dagger.ViewModelKey
import app.views.loginfragment.LoginFragment
import app.views.loginfragment.LoginFragmentSubcomponent
import app.views.registerfragment.RegisterFragment
import app.views.registerfragment.RegisterFragmentComponent
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap

@Module(subcomponents = [LoginFragmentSubcomponent::class, RegisterFragmentComponent::class])
internal abstract class LaunchActivityModule {

    @Binds
    @IntoMap
    @LaunchActivityScope
    @ViewModelKey(LaunchActivityViewModel::class)
    abstract fun provideLaunchActivityViewModel(launchActivityViewModel: LaunchActivityViewModel): ViewModel

    @Binds
    @IntoMap
    @FragmentKey(LoginFragment::class)
    abstract fun provideLoginFragment(builder: LoginFragmentSubcomponent.Builder): AndroidInjector.Factory<out Fragment>

    @Binds
    @IntoMap
    @FragmentKey(RegisterFragment::class)
    abstract fun provideRegisterFragment(builder: RegisterFragmentComponent.Builder): AndroidInjector.Factory<out Fragment>
}