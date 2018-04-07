package app.views

import android.os.Bundle
import app.ext.BaseActivity
import app.ext.ViewModelFactory
import app.views.dagger.LaunchActivityScope
import app.views.mvp.LaunchActivityViewModel
import com.example.namigtahmazli.sweetnote.R
import com.example.namigtahmazli.sweetnote.databinding.ActivityLauncherBinding
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

@LaunchActivityScope
internal class LaunchActivity : BaseActivity<LaunchActivityViewModel, ActivityLauncherBinding>() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var disposables: CompositeDisposable

    override val dataBinding: ActivityLauncherBinding by lazy {
        getDataBinding<ActivityLauncherBinding>(R.layout.activity_launcher)
    }

    override val viewModel: LaunchActivityViewModel
        get() = getViewModel(viewModelFactory)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        disposables = CompositeDisposable()
    }

    override fun onStart() {
        disposables += observeEtEmailChanges()
        disposables += observeEtPasswordChanges()
        super.onStart()
    }


    private fun observeEtEmailChanges(): Disposable {
        return RxTextView.textChanges(dataBinding.etEmail)
                .filter { !it.isEmpty() }
                .map { it.toString() }
                .window(1)
                .flatMap(viewModel::isValidEmail)
                .subscribeBy(
                        onNext = { dataBinding.emailLayout.error = if (it) null else "Invalid" }
                )
    }

    private fun observeEtPasswordChanges(): Disposable {
        return RxTextView.textChanges(dataBinding.etPassword)
                .filter { !it.isEmpty() }
                .map { it.toString() }
                .window(1)
                .flatMap(viewModel::isValidPassword)
                .subscribeBy(
                        onNext = { dataBinding.passwordLayout.error = if (it) null else "Invalid" }
                )
    }
}