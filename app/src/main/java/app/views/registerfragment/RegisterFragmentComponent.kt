package app.views.registerfragment

import dagger.Subcomponent
import dagger.android.AndroidInjector

@RegisterFragmentScope
@Subcomponent(modules = [RegisterFragmentModule::class])
internal interface RegisterFragmentComponent : AndroidInjector<RegisterFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<RegisterFragment>()
}