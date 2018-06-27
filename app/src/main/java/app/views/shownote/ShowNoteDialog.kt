package app.views.shownote

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import app.customview.DrawableClickableTextView
import app.ext.BaseActivity
import app.ext.BaseBottomSheetDialogFragment
import app.ext.log
import app.extensions.systemService
import app.parcelables.AddEditDialogArgumentModel
import app.parcelables.ShowDialogArgumentModel
import app.views.addeditnotedialog.AddEditNoteDialog
import com.example.namigtahmazli.sweetnote.databinding.DialogShowNoteBinding
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.android.inject

internal class ShowNoteDialog : BaseBottomSheetDialogFragment() {
    val showNoteViewModel by viewModel<ShowNoteViewModel>()
    private val showNoteDialogPresenter by inject<ShowNoteDialogPresenter> { mapOf(DIALOG to this) }
    private lateinit var dataBinding: DialogShowNoteBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dataBinding = DialogShowNoteBinding.inflate(layoutInflater, container, false).apply {
            viewModel = showNoteViewModel
            presenter = showNoteDialogPresenter
        }
        return dataBinding.root
    }

    override fun onStateChanged(state: Int) {
        when (state) {
            BottomSheetBehavior.STATE_COLLAPSED, BottomSheetBehavior.STATE_HIDDEN -> {
                this.dismiss()
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        showNoteViewModel.note.value = arguments?.getParcelable<ShowDialogArgumentModel>(ARGUMENT)?.noteModel
    }

    fun handleUsernameDrawableClick(drawablePosition: DrawableClickableTextView.DrawablePosition) {
        if (drawablePosition == DrawableClickableTextView.DrawablePosition.Right) {
            context?.systemService<ClipboardManager>(Context.CLIPBOARD_SERVICE)?.run {
                primaryClip = ClipData.newPlainText("Copied to Clipboard", showNoteViewModel.note.value?.userName)
                Toast.makeText(context, "Copied to Clipboard", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun handlePasswordDrawableClick(drawablePosition: DrawableClickableTextView.DrawablePosition) {
        if (drawablePosition == DrawableClickableTextView.DrawablePosition.Right) {
            context?.systemService<ClipboardManager>(Context.CLIPBOARD_SERVICE)?.run {
                primaryClip = ClipData.newPlainText("Copied to Clipboard", showNoteViewModel.note.value?.password)
                Toast.makeText(context, "Copied to Clipboard", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun handleEdit() {
        (activity as BaseActivity<*>).addDialogFragment(block = {
            AddEditNoteDialog.instance.apply {
                arguments = Bundle().apply {
                    putParcelable(AddEditNoteDialog.NOTE, AddEditDialogArgumentModel(showNoteViewModel.note.value))
                }
            }
        })
        dismiss()
    }

    fun handleDeletingNote(removedNoteCount: Int) {
        if (removedNoteCount == 1) {
            context?.let {
                Toast.makeText(it, "Removed Successfully", Toast.LENGTH_LONG).show()
            }
            dialog.dismiss()
        } else {
            context?.let {
                Toast.makeText(it, "Could not delete note.", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun handleDeletingNoteError(throwable: Throwable) {
        throwable.log<ShowNoteDialogPresenter>("while deleting note.")
        (activity as BaseActivity<*>).showMessage(message = "Could not delete. Please try again.")
    }

    companion object {
        const val ARGUMENT = "NoteArgument"
        const val DIALOG = "ShowNoteDialog"
    }
}