package app.parcelables

import android.os.Parcelable
import domain.model.NoteModel
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class ShowDialogArgumentModel(val noteModel: NoteModel?) : Parcelable