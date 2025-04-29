package com.example.wordle.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.setValue
import com.example.wordle.viewmodel.WordleViewModel

@Composable
fun GameScreen(
    modifier: Modifier,
    viewModel: WordleViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val isVibrationEnabled = viewModel.isVibrationEnabled()
    val context = LocalContext.current
    var showInfoDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadAndSetRandomWord(context, "words.txt")
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header(
            onInfoClick = { showInfoDialog = true },
            onVibrationToggle = { viewModel.toggleVibration() },
            isVibrationEnabled = isVibrationEnabled
        )
        WordleGrid(
            guesses = state.guesses,
            currentGuess = state.currentGuess,
            currentRow = state.currentRow,
            wordLength = state.wordLength,
            maxGuesses = state.maxGuesses,
            letterStates = state.letterStates
        )
        Keyboard(onKeyClick = viewModel::handleKeyClick)

        GameDialogs(
            state = state,
            onRestart = { viewModel.loadAndSetRandomWord(context, "words.txt") },
            showInfoDialog = showInfoDialog,
            onDismissInfo = { showInfoDialog = false }
        )
    }
}