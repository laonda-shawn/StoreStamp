package io.laonda.storestamp.application

import android.app.Application
import io.laonda.storestamp.utils.YsSharedPreferences

class YsApplication  : Application() {
    companion object {
        lateinit var sharedPref: YsSharedPreferences

        @Volatile
        private lateinit var instance: YsApplication

        fun getApplicationContext() = instance.applicationContext
    }

    override fun onCreate() {
        instance = this
        sharedPref = YsSharedPreferences(applicationContext)

        super.onCreate()
    }
}