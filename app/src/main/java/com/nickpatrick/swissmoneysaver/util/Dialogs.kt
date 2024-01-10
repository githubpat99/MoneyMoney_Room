package com.nickpatrick.swissmoneysaver.util

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nickpatrick.swissmoneysaver.R


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
            title = { Text(text = stringResource(id = R.string.warning)) },
            text = { Text(text = stringResource(id = R.string.genLive)) },
            confirmButton = {
                Button(onClick = onYesClick) {
                    Text(stringResource(id = R.string.yes))
                }
            },
            dismissButton = {
                Button(onClick = onNoClick) {
                    Text(stringResource(id = R.string.no))
                }
            }
        )
    }
}

