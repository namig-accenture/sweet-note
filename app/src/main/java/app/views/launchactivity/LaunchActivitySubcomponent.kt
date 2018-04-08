package app.views.launchactivity

import dagger.Subcomponent
import dagger.android.AndroidInjector

@LaunchActivityScope
@Subcomponent(modules = [LaunchActivityModule::class])
internal interface LaunchActivitySubcomponent : AndroidInjector<LaunchActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<LaunchActivity>()
}