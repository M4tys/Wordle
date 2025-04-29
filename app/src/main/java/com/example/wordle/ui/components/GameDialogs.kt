package com.example.wordle.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.wordle.viewmodel.GameState

@Composable
fun GameDialogs(
    state: GameState,
    onRestart: () -> Unit,
    showInfoDialog: Boolean,
    onDismissInfo: () -> Unit
) {
    // Dialog końca gry
    if (state.isGameOver) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Koniec Gry") },
            text = {
                Text(
                    if (state.hasWon) "Gratulacje! Zgadłeś słowo: ${state.secretWord}"
                    else "Przegrałeś! Słowo to: ${state.secretWord}"
                )
            },
            confirmButton = {
                Button(onClick = onRestart) {
                    Text("Zagraj Ponownie")
                }
            }
        )
    }

    // Dialog informacyjny
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = onDismissInfo,
            title = { Text("Zasady gry Wordle", style = MaterialTheme.typography.headlineSmall) },
            text = {
                Column {
                    Text("1. Twoim celem jest odgadnięcie ukrytego 5-literowego słowa.")
                    Text("2. Masz 6 prób na odgadnięcie słowa.")
                    Text("3. Po każdej próbie otrzymasz podpowiedzi:")
                    Text("   - Zielony: litera jest na właściwym miejscu")
                    Text("   - Żółty: litera jest w słowie, ale na złym miejscu")
                    Text("   - Szary: litera nie występuje w słowie")
                }
            },
            confirmButton = {
                Button(onClick = onDismissInfo) {
                    Text("Rozumiem")
                }
            }
        )
    }
}