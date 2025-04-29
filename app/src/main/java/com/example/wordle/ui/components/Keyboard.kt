package com.example.wordle.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wordle.viewmodel.GameState
import com.example.wordle.viewmodel.LetterState
import com.example.wordle.viewmodel.WordleViewModel

@Composable
fun Keyboard(
    modifier: Modifier = Modifier,
    onKeyClick: (String) -> Unit
) {
    val viewModel: WordleViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    val letterStatesMap = rememberLetterStates(state)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        listOf(
            listOf("Ą", "Ć", "Ę", "Ł", "Ó", "Ś", "Ń", "Ż", "Ź"),
            listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"),
            listOf("A", "S", "D", "F", "G", "H", "J", "K", "L"),
            listOf("ENTER", "Z", "X", "C", "V", "B", "N", "M", "⌫")
        ).forEach { rowKeys ->
            KeyboardRow(
                keys = rowKeys,
                letterStates = letterStatesMap,
                onKeyClick = onKeyClick
            )
        }
    }
}

@Composable
private fun rememberLetterStates(state: GameState): SnapshotStateMap<Char, LetterState> {
    val letterStatesMap = remember { mutableStateMapOf<Char, LetterState>() }

    LaunchedEffect(state.secretWord) {
        letterStatesMap.clear()
    }

    state.guesses.forEachIndexed { rowIndex, guessRow ->
        state.letterStates.getOrNull(rowIndex)?.forEachIndexed { colIndex, letterState ->
            guessRow.getOrNull(colIndex)?.let { letter ->
                val currentState = letterStatesMap[letter] ?: LetterState.UNCHECKED
                val newState = when (letterState) {
                    LetterState.CORRECT -> LetterState.CORRECT
                    LetterState.PRESENT -> if (currentState != LetterState.CORRECT) LetterState.PRESENT else currentState
                    LetterState.ABSENT -> if (currentState == LetterState.UNCHECKED) LetterState.ABSENT else currentState
                    else -> currentState
                }
                letterStatesMap[letter] = newState
            }
        }
    }

    return letterStatesMap
}

@Composable
private fun KeyboardRow(
    keys: List<String>,
    letterStates: Map<Char, LetterState>,
    onKeyClick: (String) -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val baseKeyWidth = (screenWidth - 16.dp - (4.dp * (keys.size - 1))) / (keys.size + keys.count { it.length > 1 } * 0.7f)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
    ) {
        keys.forEach { key ->
            val width = if (key.length > 1) baseKeyWidth * 1.7f else baseKeyWidth
            val state = if (key.length == 1) letterStates[key[0]] ?: LetterState.UNCHECKED else null

            KeyboardKey(
                text = key,
                onClick = { onKeyClick(key) },
                modifier = Modifier
                    .height(48.dp)
                    .width(width),
                backgroundColor = state?.let { getKeyColor(it) } ?: MaterialTheme.colorScheme.surface,
                contentColor = if (state != null && state != LetterState.UNCHECKED) Color.White
                else MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun getKeyColor(state: LetterState): Color {
    return when (state) {
        LetterState.CORRECT -> MaterialTheme.colorScheme.primary
        LetterState.PRESENT -> MaterialTheme.colorScheme.secondary
        LetterState.ABSENT -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.surface
    }
}

@Composable
fun KeyboardKey(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onBackground
) {
    Box(
        modifier = modifier
            .padding(vertical = 2.dp)
            .clickable(onClick = onClick)
            .background(backgroundColor, RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = contentColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}