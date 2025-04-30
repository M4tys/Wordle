package com.example.wordle.viewmodel

data class GameState(
    val secretWord: String? = null,
    val currentGuess: List<Char?> = emptyList(),
    val guesses: List<List<Char?>> = emptyList(),
    val letterStates: List<List<LetterState>> = emptyList(),
    val currentRow: Int = 0,
    val wordLength: Int = 5,
    val maxGuesses: Int = 6,
    val isGameOver: Boolean = false,
    val hasWon: Boolean = false,
    val isWordLoading: Boolean = false
)

enum class LetterState {
    CORRECT, PRESENT, ABSENT, UNCHECKED
}