package app.views.launchactivity

import android.databinding.ObservableBoolean
import app.ext.BaseViewModel
import domain.usecase.login.GetCurrentLoggedInUserUseCase

internal class LaunchActivityViewModel(private val getCurrentLoggedInUserUseCase: GetCurrentLoggedInUserUseCase) : BaseViewModel() {
    val showProgress = ObservableBoolean(true)
    val currentUser = getCurrentLoggedInUserUseCase.get()
}