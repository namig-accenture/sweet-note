package app.views.loginfragment

import dagger.Subcomponent
import dagger.android.AndroidInjector

@LoginFragmentScope
@Subcomponent(modules = [LoginFragmentModule::class])
internal interface LoginFragmentSubcomponent : AndroidInjector<LoginFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<LoginFragment>()
}