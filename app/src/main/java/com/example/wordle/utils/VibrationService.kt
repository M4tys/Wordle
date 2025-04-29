package com.example.wordle.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.wordle.viewmodel.LetterState

class VibrationService(context: Context) {
    private var vibrator: Vibrator? = null
    private val prefs = PreferencesManager(context)

    var isVibrationEnabled: Boolean by mutableStateOf(prefs.isVibrationEnabled)
        private set

    init {
        initializeVibrator(context)
    }

    private fun initializeVibrator(context: Context) {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    fun toggleVibration() {
        isVibrationEnabled = !isVibrationEnabled
        prefs.isVibrationEnabled = isVibrationEnabled
    }

    fun triggerVibration(letterStates: List<LetterState>) {
        if (!isVibrationEnabled || vibrator == null || vibrator?.hasVibrator() != true) return

        val hasCorrect = letterStates.any { it == LetterState.CORRECT }
        val hasPresent = letterStates.any { it == LetterState.PRESENT }

        val vibrationPattern = when {
            hasCorrect -> longArrayOf(0, 200)
            hasPresent -> longArrayOf(0, 500)
            else -> longArrayOf(0, 1000)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(
                VibrationEffect.createWaveform(
                    vibrationPattern,
                    -1
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(vibrationPattern, -1)
        }
    }
}