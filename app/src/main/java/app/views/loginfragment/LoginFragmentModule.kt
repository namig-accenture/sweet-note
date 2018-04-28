package app.views.loginfragment

import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.applicationContext

val loginFragmentModule = applicationContext {
    viewModel { LoginFragmentViewModel(get()) }
    bean {params -> LoginFragmentPresenter(params[LoginFragment.FRAGMENT]) }
}