package app.views.launchactivity

import app.views.loginfragment.LoginFragment
import app.views.registerfragment.RegisterFragment
import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.applicationContext

val launchActivityModule = applicationContext {
    viewModel { LaunchActivityViewModel() }
    bean { LoginFragment() }
    bean { RegisterFragment() }
}