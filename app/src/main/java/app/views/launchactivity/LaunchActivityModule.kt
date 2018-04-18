package app.views.launchactivity

import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.applicationContext

val launchActivityModule = applicationContext {
    viewModel { LaunchActivityViewModel() }
}