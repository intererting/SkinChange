package com.yuliyang.skinchange

import android.app.Application
import kotlin.properties.Delegates

class MyApplication : Application() {

    companion object {
        private var instance: MyApplication by Delegates.notNull()

        fun provideInstance(): MyApplication {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}