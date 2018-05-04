package app.views.pinactivity

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import app.ext.BaseViewModel
import app.parcelables.PinActivityIntentModel
import domain.exceptions.PinNotDefinedException
import domain.model.EnterPinType
import domain.usecase.login.EnterPinUseCase
import domain.usecase.login.ValidatePinUseCase
import io.reactivex.Completable
import io.reactivex.Single

internal class PinViewModel(private val enterPinUseCase: EnterPinUseCase,
                            private val validatePinUseCase: ValidatePinUseCase,
                            intentModel: PinActivityIntentModel) : BaseViewModel() {
    val pin = ObservableField<String>()
    val pinLength = 4
    val isContinueButtonEnabled = ObservableBoolean(false)
    val isContinueButtonVisible = ObservableBoolean(intentModel.enterPinType == EnterPinType.Register)

    fun registerPin(): Completable {
        return pin.get()?.let {
            enterPinUseCase.get(it)
        } ?: Completable.error(PinNotDefinedException("Pin not defined."))
    }

    fun validatePin(): Single<Boolean> {
        return pin.get()?.let {
            validatePinUseCase.get(it)
        } ?: Single.error(PinNotDefinedException("Pin not defined."))
    }
}