package app.dagger

import app.App
import dagger.Component
import dagger.android.AndroidInjector

@AppScope
@Component(modules = [AppModule::class])
internal interface AppComponent : AndroidInjector<App> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<App>()
}