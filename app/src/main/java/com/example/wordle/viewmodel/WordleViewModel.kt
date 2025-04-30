package com.example.wordle.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wordle.data.WordsRepository
import com.example.wordle.utils.VibrationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WordleViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state
    private var cachedWords: List<String> = emptyList()

    private val vibrationService = VibrationService(application.applicationContext)

    fun toggleVibration() {
        vibrationService.toggleVibration()
    }

    fun isVibrationEnabled(): Boolean = vibrationService.isVibrationEnabled

    fun loadAndSetRandomWord(context: Context, fileName: String) {
        viewModelScope.launch {
            if(cachedWords.isEmpty()) {
                var wordsRepository = WordsRepository(context)
                cachedWords = wordsRepository.loadWords(fileName)
            }
            val randomWord = cachedWords.random()

            _state.update {
                it.copy(
                    secretWord = randomWord,
                    isWordLoading = false,
                    isGameOver = false,
                    hasWon = false,
                    guesses = emptyList(),
                    currentGuess = emptyList(),
                    currentRow = 0,
                    letterStates = emptyList()
                )
            }
            Log.d("WordleViewModel", "Nowe słowo: ${_state.value.secretWord}")
        }
    }

    fun handleKeyClick(key: String) {
        if (_state.value.isGameOver || _state.value.isWordLoading) return

        when (key) {
            "ENTER" -> submitGuess()
            "⌫" -> deleteLastChar()
            else -> addCharToCurrentGuess(key.first().uppercaseChar())
        }
    }

    private fun addCharToCurrentGuess(char: Char) {
        if (_state.value.currentGuess.size < _state.value.wordLength) {
            _state.update { it.copy(currentGuess = it.currentGuess + char) }
        }
    }

    private fun deleteLastChar() {
        _state.update { it.copy(currentGuess = it.currentGuess.dropLast(1)) }
    }

    private fun submitGuess() {
        val currentState = _state.value
        val secretWord = currentState.secretWord ?: return

        if (currentState.currentGuess.size != currentState.wordLength) return

        val guessedWord = currentState.currentGuess.mapNotNull { it }.joinToString("")

        Log.d("WordleViewModel", "Sprawdzam słowo: $guessedWord, Sekretne słowo: $secretWord")

        val isCorrect = guessedWord == secretWord
        val newGuesses = currentState.guesses + listOf(currentState.currentGuess)
        val letterStates = evaluateGuess(guessedWord, secretWord)
        val newLetterStates = currentState.letterStates + listOf(letterStates)

        vibrationService.triggerVibration(letterStates)

        _state.update {
            it.copy(
                guesses = newGuesses,
                letterStates = newLetterStates,
                currentGuess = emptyList(),
                currentRow = it.currentRow + 1,
                hasWon = isCorrect,
                isGameOver = isCorrect || it.currentRow + 1 >= it.maxGuesses
            )
        }
    }

    private fun evaluateGuess(guess: String, secret: String): List<LetterState> {
        val result = mutableListOf<LetterState>()
        val secretLetters = secret.toMutableList()
        val guessLetters = guess.toMutableList()

        for (i in 0 until guess.length) {
            if (guessLetters.getOrNull(i) == secretLetters.getOrNull(i)) {
                result.add(LetterState.CORRECT)
                secretLetters[i] = '*'
                guessLetters[i] = '#'
            } else {
                result.add(LetterState.UNCHECKED)
            }
        }

        for (i in 0 until guess.length) {
            if (result[i] == LetterState.UNCHECKED && guessLetters.getOrNull(i) != null) {
                val indexInSecret = secretLetters.indexOf(guessLetters[i])
                if (indexInSecret != -1) {
                    result[i] = LetterState.PRESENT
                    secretLetters[indexInSecret] = '*'
                } else {
                    result[i] = LetterState.ABSENT
                }
            }
        }
        return result
    }
}