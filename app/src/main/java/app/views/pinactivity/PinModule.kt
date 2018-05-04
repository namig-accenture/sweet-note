package app.views.pinactivity

import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.applicationContext

val pinActivityModule = applicationContext {
    viewModel { param -> PinViewModel(get(), get(), param[PinActivity.EXTRA]) }
    factory { param -> PinPresenter(param[PinActivity.ACTIVITY]) }
}