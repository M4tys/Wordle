package com.example.wordle.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wordle.viewmodel.LetterState

@Composable
fun WordleGrid(
    modifier: Modifier = Modifier,
    guesses: List<List<Char?>>,
    currentGuess: List<Char?>,
    currentRow: Int,
    wordLength: Int,
    maxGuesses: Int,
    letterStates: List<List<LetterState>>,
) {
    val cellSize = calculateCellSize(wordLength)

    LazyVerticalGrid(
        columns = GridCells.Fixed(wordLength),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(maxGuesses * wordLength) { index ->
            val row = index / wordLength
            val col = index % wordLength
            val letter = guesses.getOrNull(row)?.getOrNull(col)
                ?: if (row == currentRow) currentGuess.getOrNull(col) else null

            val state = letterStates.getOrNull(row)?.getOrNull(col) ?: LetterState.UNCHECKED
            val colors = getColorsForState(state)

            Box(
                modifier = Modifier
                    .size(cellSize)
                    .background(colors.backgroundColor, RoundedCornerShape(4.dp))
                    .border(2.dp, colors.borderColor, RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                letter?.let {
                    Text(
                        text = it.toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textColor
                    )
                }
            }
        }
    }
}

@Composable
private fun calculateCellSize(wordLength: Int): Dp {
    return (LocalConfiguration.current.screenWidthDp.dp - 32.dp - (4.dp * 4)) / wordLength
}

private data class BoxColors(
    val backgroundColor: Color,
    val borderColor: Color,
    val textColor: Color
)

@Composable
private fun getColorsForState(state: LetterState): BoxColors {
    return when (state) {
        LetterState.CORRECT -> BoxColors(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primary,
            Color.White
        )
        LetterState.PRESENT -> BoxColors(
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.secondary,
            Color.White
        )
        LetterState.ABSENT -> BoxColors(
            MaterialTheme.colorScheme.tertiary,
            MaterialTheme.colorScheme.tertiary,
            Color.White
        )
        else -> BoxColors(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surface,
            if (isSystemInDarkTheme()) Color.White else Color.Black
        )
    }
}