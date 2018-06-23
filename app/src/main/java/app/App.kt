package app

import android.app.Application
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.ProcessLifecycleOwner
import app.koin.AppModule
import com.example.namigtahmazli.sweetnote.BuildConfig
import data.dataModule
import domain.lifecycle.ApplicationLifecycyle
import domain.domainModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.setProperty
import org.koin.android.ext.android.startKoin
import timber.log.Timber

open class App : Application() {
    private val applicationLifecycyle by inject<ApplicationLifecycyle>()
    override fun onCreate() {
        super.onCreate()
        initializeTimber()
        startKoin()
        ProcessLifecycleOwner.get().lifecycle.addObserver(applicationLifecycyle as LifecycleObserver)
    }

    open fun startKoin() {
        startKoin(modules = (AppModule().appModule + dataModule + domainModule))
        setProperty("Debug", BuildConfig.DEBUG)
        setProperty("dbName", BuildConfig.DB_NAME)
        setProperty("prefName", BuildConfig.PREF_NAME)
        setProperty("inMemory", false)
    }

    private fun initializeTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}