package app.views.pinactivity

import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.content.Intent
import app.ext.BaseActivity
import app.ext.log
import app.extensions.plusAssign
import app.parcelables.PinActivityIntentModel
import com.example.namigtahmazli.sweetnote.R
import com.example.namigtahmazli.sweetnote.databinding.ActivityPinBinding
import domain.exceptions.CurrentUserNotFoundException
import domain.exceptions.PinNotDefinedException
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.android.inject

internal class PinActivity : BaseActivity<ActivityPinBinding>() {

    val pinViewModel by viewModel<PinViewModel> { mapOf(EXTRA to intent.getParcelableExtra(EXTRA)) }
    private val pinPresenter by inject<PinPresenter> { mapOf(ACTIVITY to this) }

    override val dataBinding: ActivityPinBinding
        get() = provideDataBinding(R.layout.activity_pin).apply {
            viewModel = pinViewModel
            presenter = pinPresenter
        }

    override fun addLifecycleObservers(lifecycle: Lifecycle) {
        lifecycle += pinPresenter
    }

    fun handleRegisteringPin() {
        showMessage(message = "Registered pin.")
    }

    fun handleRegisteringPinError(throwable: Throwable) {
        val message = when (throwable) {
            is CurrentUserNotFoundException -> R.string.pin_activity_current_user_not_found_error_message
            is PinNotDefinedException -> R.string.pin_activity_pin_not_defined_message
            else -> R.string.pin_activity_register_pin_error_message
        }
        showMessage(messageId = message)
        throwable.log<PinPresenter>("While registering pin.")
    }

    fun handleValidatingPin(isValid: Boolean) {
        val message = if (isValid) {
            "Valid pin"
        } else {
            "Invalid pin"
        }
        showMessage(message = message)
    }

    fun handleValidatingPinError(throwable: Throwable) {
        val message = when (throwable) {
            is CurrentUserNotFoundException -> R.string.pin_activity_current_user_not_found_error_message
            is PinNotDefinedException -> R.string.pin_activity_pin_not_defined_message
            else -> R.string.pin_activity_login_pin_error_message
        }
        showMessage(messageId = message)
    }

    companion object {
        const val ACTIVITY = "PinActivity"
        const val EXTRA = "PinActivityExtra"
        fun provideIntent(context: Context, model: PinActivityIntentModel): Intent {
            return Intent(context, PinActivity::class.java).apply {
                putExtra(EXTRA, model)
            }
        }
    }
}