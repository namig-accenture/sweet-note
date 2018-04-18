package app.views.launchactivity

import android.os.Bundle
import android.support.annotation.IdRes
import android.util.Log
import app.ext.BaseActivity
import app.views.loginfragment.LoginFragment
import app.views.registerfragment.RegisterFragment
import com.example.namigtahmazli.sweetnote.R
import com.example.namigtahmazli.sweetnote.databinding.ActivityLauncherBinding
import com.jakewharton.rxbinding2.widget.RxRadioGroup
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import org.koin.android.architecture.ext.viewModel

internal class LaunchActivity : BaseActivity<ActivityLauncherBinding>() {
    val viewModel by viewModel<LaunchActivityViewModel>()

    override val dataBinding: ActivityLauncherBinding
        get() = getDataBinding(R.layout.activity_launcher)

    private lateinit var disposables: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        disposables = CompositeDisposable()
    }

    override fun onStart() {
        super.onStart()
        disposables += observeSwitchChanges()
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    private fun observeSwitchChanges(): Disposable {
        return RxRadioGroup.checkedChanges(dataBinding.viewSwitcher)
                .subscribeBy(
                        onNext = (::handleSwitchChanges),
                        onError = { Log.e("Error", it.toString()) }
                )
    }

    private fun handleSwitchChanges(@IdRes id: Int) {
        Log.e("Radio Button Id", "$id")
        when (id) {
            R.id.group_login -> addFragment(R.id.container_layout) { LoginFragment() }
            R.id.group_register -> addFragment(R.id.container_layout) { RegisterFragment() }
        }
    }
}