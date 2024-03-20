package com.muen.minesweeper

import android.app.Application
import android.content.Context
import kotlin.properties.Delegates

class AppApplication: Application() {
    companion object {
        private val TAG = "App"

        lateinit var instance:AppApplication

        var context: Context by Delegates.notNull()
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        context = applicationContext
    }
}