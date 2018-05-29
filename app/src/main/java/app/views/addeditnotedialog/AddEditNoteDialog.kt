package app.views.addeditnotedialog

import android.animation.AnimatorSet
import android.arch.lifecycle.Lifecycle
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.support.annotation.MainThread
import android.support.graphics.drawable.AnimatedVectorDrawableCompat
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.ColorUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.ext.BaseDialogFragment
import app.ext.log
import app.extensions.*
import com.example.namigtahmazli.sweetnote.R
import com.example.namigtahmazli.sweetnote.databinding.DialogAddEditNoteBinding
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.android.inject


internal class AddEditNoteDialog : BaseDialogFragment<DialogAddEditNoteBinding>() {

    val dialogViewModel by viewModel<AddEditNoteViewModel>()
    private val addEditNoteDialogPresenter by inject<AddEditNoteDialogPresenter> { mapOf(DIALOG to this) }

    override val layoutRes: Int
        get() = R.layout.dialog_add_edit_note

    override fun bindVariables(dataBinding: DialogAddEditNoteBinding) {
        dataBinding.presenter = addEditNoteDialogPresenter
        dataBinding.viewModel = dialogViewModel
        dataBinding.setLifecycleOwner(this)
    }

    override fun addLifecycleObservers(lifecycle: Lifecycle) {
        lifecycle += addEditNoteDialogPresenter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState).apply {
            dataBinding.apply {
                val context = root.context
                addNoteMainContainer.apply {
                    animateOn {
                        AnimatorSet().apply {
                            this += circularRevealOpen(
                                    arguments?.getInt(REVEAL_START_X) ?: 0,
                                    arguments?.getInt(REVEAL_START_Y) ?: 0
                            )
                            this += valueAnimation(255, 0, onValueChanged = {
                                val color = ContextCompat.getColor(context, R.color.colorAccent)
                                setBackgroundColor(ColorUtils.setAlphaComponent(color, it))
                            })
                            duration = ANIMATION_DURATION
                            start()
                        }
                        (dataBinding.toolbar.navigationIcon as AnimatedVectorDrawableCompat).start()
                    }
                }
            }
        }
    }

    @MainThread
    fun handleBackButtonClick(@Suppress("UNUSED_PARAMETER") view: View?) {
        context?.let { context ->
            arguments?.let {
                val width = it.getInt(FAB_DIAMETHER)
                val x = it.getInt(REVEAL_START_X)
                val y = it.getInt(REVEAL_START_Y)
                dataBinding.apply {
                    addNoteMainContainer.apply {
                        AnimatorSet().apply {
                            this += circularRevealClose(x, y - width, width * .5f)
                            this += valueAnimation(0, 255, onValueChanged = {
                                val color = ContextCompat.getColor(context, R.color.colorAccent)
                                setBackgroundColor(ColorUtils.setAlphaComponent(color, it))
                            })
                            duration = ANIMATION_DURATION
                            onAnimationEnd { this@AddEditNoteDialog.dismiss() }
                            start()
                        }
                    }
                    toolbar.apply {
                        navigationIcon = (context.getDrawable(R.drawable.animated_back_to_menu_icon)
                                as AnimatedVectorDrawable).apply {
                            start()
                        }
                        title = getString(R.string.register)
                    }
                }
            }
        }
    }

    fun handleTitleTextChanges(isNotDefined: Boolean) {
        dialogViewModel.titleError.value = if (isNotDefined) {
            getString(R.string.add_edit_note_dialog_title_not_defined_error)
        } else {
            null
        }
    }

    fun handleUsernameTextChanges(isNotDefined: Boolean) {
        dialogViewModel.usernameError.value = if (isNotDefined) {
            getString(R.string.add_edit_note_dialog_username_not_defined_error)
        } else {
            null
        }
    }

    fun handlePasswordTextChanges(isNotDefined: Boolean) {
        dialogViewModel.passwordError.value = if (isNotDefined) {
            getString(R.string.add_edit_note_dialog_password_not_defined_error)
        } else {
            null
        }
    }

    fun handleAddingNoteResult() {
        this.dismiss()
    }

    fun handleAddingNoteError(throwable: Throwable) {
        throwable.log<AddEditNoteDialogPresenter>("While adding note.")
        showMessage(message = "Failed")
    }

    companion object {
        const val DIALOG = "AddEditNoteDialog"
        const val REVEAL_START_X = "x"
        const val REVEAL_START_Y = "y"
        const val FAB_DIAMETHER = "width"
        const val ANIMATION_DURATION = 500L
    }
}