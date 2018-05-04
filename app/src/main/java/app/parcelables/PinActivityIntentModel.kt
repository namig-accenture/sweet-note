package app.parcelables

import android.os.Parcelable
import domain.model.EnterPinType
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PinActivityIntentModel(val enterPinType: EnterPinType) : Parcelable