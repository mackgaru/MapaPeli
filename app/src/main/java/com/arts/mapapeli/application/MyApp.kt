package com.arts.mapapeli.application

import android.app.Application
import android.content.Context

class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {

        lateinit var instance:MyApp

        val context: Context get() {
                return instance.applicationContext
            }
    }
}