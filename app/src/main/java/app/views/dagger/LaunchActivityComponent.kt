package app.views.dagger

import app.views.LaunchActivity
import dagger.Subcomponent
import dagger.android.AndroidInjector

@LaunchActivityScope
@Subcomponent(modules = [LaunchActivityModule::class])
internal interface LaunchActivityComponent : AndroidInjector<LaunchActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<LaunchActivity>()
}