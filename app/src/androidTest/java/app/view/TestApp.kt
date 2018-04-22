package app.view

import app.App
import data.dataModule
import domain.domainModule
import org.koin.android.ext.android.startKoin

class TestApp : App() {
    override fun startKoin() {
        startKoin(application = this, modules = TestModule().appModule + dataModule + domainModule)
    }
}