package pe.edu.upeu.acopioleche

import android.app.Application
import pe.edu.upeu.acopioleche.data.sqldelight.AndroidDatabaseDriverFactory
import pe.edu.upeu.acopioleche.data.sync.AndroidConnectivityObserver
import pe.edu.upeu.acopioleche.di.ServiceLocator

class AcopioLecheApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(
            driverFactory = AndroidDatabaseDriverFactory(context = this),
            connectivityObserver = AndroidConnectivityObserver(context = this),
        )
    }
}
