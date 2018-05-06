package app.views.launchactivity

import android.databinding.ObservableBoolean
import android.databinding.ObservableInt
import app.ext.BaseViewModel
import com.example.namigtahmazli.sweetnote.R
import com.fernandocejas.arrow.optional.Optional
import domain.model.UserModel
import domain.usecase.login.GetCurrentLoggedInUserUseCase
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.BehaviorSubject

internal class LaunchActivityViewModel(getCurrentLoggedInUserUseCase: GetCurrentLoggedInUserUseCase) : BaseViewModel() {
    val showProgress = ObservableBoolean(true)
    val currentStateId = ObservableInt(R.id.group_login)
    val currentUser = getCurrentLoggedInUserUseCase.get()
    val switchObserver: BehaviorSubject<Int> = BehaviorSubject.createDefault(currentStateId.get())

    inline fun observeSwitchChanges(crossinline onNext: (Int) -> Unit,
                                    crossinline onError: (Throwable) -> Unit): Disposable {
        return switchObserver
                .doOnNext(currentStateId::set)
                .subscribeBy(
                        onNext = { onNext(it) },
                        onError = { onError(it) }
                )
    }

    inline fun observeCurrentUser(crossinline onSuccess: (Optional<UserModel>) -> Unit,
                                  crossinline onError: (Throwable) -> Unit): Disposable {
        return currentUser
                .doOnSuccess { showProgress.set(it.isPresent) }
                .subscribeBy(
                        onSuccess = { onSuccess(it) },
                        onError = { onError(it) }
                )
    }
}