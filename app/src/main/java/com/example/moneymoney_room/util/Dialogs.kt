package com.example.moneymoney_room.util

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


@Composable
fun YesNoDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onYesClick: () -> Unit,
    onNoClick: () -> Unit,
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Warnung") },
            text = { Text(text = "Daten werden neu erstellt. Einverstanden?") },
            confirmButton = {
                Button(onClick = onYesClick) {
                    Text(text = "Ja")
                }
            },
            dismissButton = {
                Button(onClick = onNoClick) {
                    Text(text = "Nein")
                }
            }
        )
    }
}

