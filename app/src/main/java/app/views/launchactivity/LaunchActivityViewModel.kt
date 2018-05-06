package app.views.launchactivity

import android.databinding.ObservableBoolean
import android.databinding.ObservableInt
import app.ext.BaseViewModel
import com.example.namigtahmazli.sweetnote.R
import domain.usecase.login.GetCurrentLoggedInUserUseCase

internal class LaunchActivityViewModel(getCurrentLoggedInUserUseCase: GetCurrentLoggedInUserUseCase) : BaseViewModel() {
    val showProgress = ObservableBoolean(true)
    val currentStateId = ObservableInt(R.id.group_login)
    val currentUser = getCurrentLoggedInUserUseCase.get()
}