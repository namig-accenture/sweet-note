package app

import com.example.namigtahmazli.sweetnote.BuildConfig
import data.dataModule
import domain.domainModule
import org.koin.android.ext.android.setProperty
import org.koin.android.ext.android.startKoin

class TestApp : App() {
    override fun startKoin() {
        startKoin(application = this, modules = TestModule().appModule + dataModule + domainModule)
        setProperty("Debug", BuildConfig.DEBUG)
        setProperty("dbName", BuildConfig.DB_NAME)
        setProperty("prefName", "SweetPrefName")
        setProperty("inMemory", true)
    }
}