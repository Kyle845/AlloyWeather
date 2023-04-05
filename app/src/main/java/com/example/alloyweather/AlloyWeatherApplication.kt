package com.example.alloyweather

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class AlloyWeatherApplication: Application() {
    companion object {
        const val TOKEN = "nqygJBOgFVVSVjLv"
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}