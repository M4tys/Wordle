package com.example.wordle.utils

import android.content.Context
import androidx.core.content.edit

class PreferencesManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("wordle_prefs", Context.MODE_PRIVATE)

    var isVibrationEnabled: Boolean
        get() = sharedPreferences.getBoolean("vibration_enabled", true) // Domyślnie włączone
        set(value) = sharedPreferences.edit { putBoolean("vibration_enabled", value) }
}