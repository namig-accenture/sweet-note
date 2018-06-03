package app.views.shownote

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.Toast
import app.customview.DrawableClickableTextView
import app.extensions.systemService
import app.parcelables.ShowDialogArgumentModel
import com.example.namigtahmazli.sweetnote.databinding.DialogShowNoteBinding
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.android.inject

internal class ShowNoteDialog : BottomSheetDialogFragment() {
    private val showNoteViewModel by viewModel<ShowNoteViewModel>()
    private val showNoteDialogPresenter by inject<ShowNoteDialogPresenter> { mapOf(DIALOG to this) }
    private lateinit var dataBinding: DialogShowNoteBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dataBinding = DialogShowNoteBinding.inflate(layoutInflater, container, false).apply {
            viewModel = showNoteViewModel
            presenter = showNoteDialogPresenter
        }
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val dialog = dialog as BottomSheetDialog
                val bottomSheet = dialog.findViewById<FrameLayout>(android.support.design.R.id.design_bottom_sheet)
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight = 0
            }
        })
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

    companion object {
        const val ARGUMENT = "NoteArgument"
        const val DIALOG = "ShowNoteDialog"
    }
}