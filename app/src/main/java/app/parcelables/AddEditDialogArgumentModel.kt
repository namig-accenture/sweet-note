package app.parcelables

import android.os.Parcelable
import domain.model.NoteModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddEditDialogArgumentModel(val noteModel: NoteModel? = null) : Parcelable