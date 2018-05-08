package app

import android.app.Application
import app.koin.AppModule
import com.example.namigtahmazli.sweetnote.BuildConfig
import data.dataModule
import domain.domainModule
import org.koin.android.ext.android.setProperty
import org.koin.android.ext.android.startKoin

open class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin()
    }

    open fun startKoin() {
        startKoin(modules = (AppModule().appModule + dataModule + domainModule))
        setProperty("Debug", BuildConfig.DEBUG)
        setProperty("dbName", BuildConfig.DB_NAME)
        setProperty("prefName", BuildConfig.PREF_NAME)
    }
}