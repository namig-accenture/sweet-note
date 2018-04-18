package app

import android.app.Application
import app.koin.appModule
import com.example.namigtahmazli.sweetnote.BuildConfig
import data.dataModule
import domain.domainModule
import org.koin.android.ext.android.setProperty
import org.koin.android.ext.android.startKoin

operator fun Array<Any>.plus(item: Array<Any>): Array<Any> {
    return this.plus(item)
}

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin(application = this, modules = (appModule + dataModule + domainModule))
        setProperty("dbName", BuildConfig.DB_NAME)
    }
}