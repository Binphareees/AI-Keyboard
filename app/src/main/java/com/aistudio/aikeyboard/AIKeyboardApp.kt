package com.aistudio.aikeyboard

import android.app.Application
import com.aistudio.aikeyboard.data.local.AppDatabase
import com.aistudio.aikeyboard.data.preferences.KeyboardPreferenceManager

class AIKeyboardApp : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var preferenceManager: KeyboardPreferenceManager
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = AppDatabase.getInstance(this)
        preferenceManager = KeyboardPreferenceManager(this)
    }

    companion object {
        lateinit var instance: AIKeyboardApp
            private set
    }
}
