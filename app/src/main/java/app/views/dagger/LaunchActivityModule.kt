package app.views.dagger

import android.support.v7.app.AppCompatActivity
import app.ext.ViewModelFactory
import app.extensions.getDataBinding
import app.extensions.getViewModel
import app.views.LaunchActivity
import app.views.mvvm.LaunchActivityModel
import com.example.namigtahmazli.sweetnote.R
import com.example.namigtahmazli.sweetnote.databinding.ActivityLauncherBinding
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
internal abstract class LaunchActivityModule {

    @LaunchActivityScope
    @Binds
    abstract fun provideAppCompatActivity(launchActivity: LaunchActivity): AppCompatActivity

    @Module companion object {
        @LaunchActivityScope
        @Provides
        @JvmStatic
        fun provideActivityBinding(launchActivity: LaunchActivity): ActivityLauncherBinding {
            return launchActivity.getDataBinding(R.layout.activity_launcher)
        }

        @LaunchActivityScope
        @Provides
        @JvmStatic
        fun provideViewModel(launchActivity: LaunchActivity, viewModelFactory: ViewModelFactory): LaunchActivityModel {
            return launchActivity.getViewModel(viewModelFactory)
        }
    }
}