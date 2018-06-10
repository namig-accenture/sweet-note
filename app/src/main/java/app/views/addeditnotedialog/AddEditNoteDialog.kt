package app.views.addeditnotedialog

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.arch.lifecycle.Lifecycle
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.support.annotation.MainThread
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.ColorUtils
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import app.ext.BaseDialogFragment
import app.ext.log
import app.extensions.afterLayoutChanged
import app.extensions.onAnimationEnd
import app.extensions.plusAssign
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

    override fun onBackPressed() {
        this.dismiss()
    }

    override fun dismiss() {
        animate(false) {
            super.dismiss()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState).apply {
            this?.afterLayoutChanged {
                animate(true)
            }
        }
    }

    @MainThread
    fun handleBackButtonClick(@Suppress("UNUSED_PARAMETER") view: View?) {
        dismiss()
    }

    private fun animate(isEntering: Boolean, onAnimationEnd: () -> Unit = {}) {
        animateBackButtonIcon(isOpening = isEntering) {
            if (!isEntering) {
                title = getString(R.string.register)
            }
        }
        dataBinding.apply {
            addNoteMainContainer.apply view@{
                val width = measuredWidth
                val height = measuredHeight
                val diamether = arguments?.getInt(FAB_DIAMETHER) ?: return
                val rightPadding = arguments?.getInt(PADDING_RIGHT) ?: return
                val bottomPadding = arguments?.getInt(PADDING_BOTTOM) ?: return
                val centerX = width - rightPadding - diamether / 2
                val centerY = height - bottomPadding - diamether / 2
                val startAlpha = if (isEntering) 255 else 0
                val endAlpha = if (isEntering) 0 else 255
                val startRadius = if (isEntering) diamether / 2.0 else Math.hypot(width.toDouble(), height.toDouble())
                val endRadius = if (isEntering) Math.hypot(width.toDouble(), height.toDouble()) else diamether * .5
                AnimatorSet().apply {
                    this += ViewAnimationUtils.createCircularReveal(this@view,
                            centerX, centerY, startRadius.toFloat(), endRadius.toFloat())
                    this += ValueAnimator.ofInt(startAlpha, endAlpha).apply {
                        val color = ContextCompat.getColor(context, R.color.colorAccent)
                        addUpdateListener {
                            setBackgroundColor(ColorUtils.setAlphaComponent(color, it.animatedValue as Int))
                        }
                    }
                    duration = ANIMATION_DURATION
                    interpolator = FastOutSlowInInterpolator()
                    onAnimationEnd { onAnimationEnd() }
                    start()
                }
            }
        }
    }

    private inline fun animateBackButtonIcon(isOpening: Boolean, doOnToolbar: Toolbar.() -> Unit = {}) {
        val icon = if (isOpening) {
            R.drawable.animated_menu_to_back_icon
        } else {
            R.drawable.animated_back_to_menu_icon
        }

        dataBinding.toolbar.apply {
            navigationIcon = (context.getDrawable(icon) as AnimatedVectorDrawable).apply {
                start()
            }
            doOnToolbar()
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
        const val FAB_DIAMETHER = "width"
        const val PADDING_BOTTOM = "paddingBottom"
        const val PADDING_RIGHT = "paddingRight"
        const val ANIMATION_DURATION = 500L
    }
}