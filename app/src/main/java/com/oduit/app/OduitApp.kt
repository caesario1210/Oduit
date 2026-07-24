package com.oduit.app

import android.app.Application
import com.oduit.app.data.local.AppDatabase
class OduitApp : Application() {

    val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: OduitApp
            private set
    }
}
