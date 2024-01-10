package com.nickpatrick.swissmoneysaver.util

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nickpatrick.swissmoneysaver.R

@Composable
fun LanguageDropDown(
    supportedLanguages: List<Pair<String, Int>>, // Pair<String, Int> represents language name and flag resource ID
    onLanguageSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf(supportedLanguages[0].first) }

    Column(modifier = Modifier.padding(8.dp)) {
        Row(
            modifier = Modifier
                .padding(top = 4.dp)
                .background(color = colorResource(id = R.color.primary_background)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                modifier = Modifier
                    .background(color = colorResource(id = R.color.primary_background)),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(id = R.color.primary_background),
                    contentColor = colorResource(id = R.color.white)
                ),
                onClick = { expanded = true }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Display the selected language's flag here
                    Image(
                        painter = painterResource(id = supportedLanguages.find { it.first == selectedLanguage }?.second ?: 0),
                        contentDescription = null, // Provide a suitable content description
                        modifier = Modifier.size(24.dp) // Adjust the size as needed
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // Add spacing between flag and text
                    Text(text = selectedLanguage)
                }
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            supportedLanguages.forEach { (language, flagResId) ->
                DropdownMenuItem(
                    onClick = {
                        selectedLanguage = language
                        expanded = false
                        onLanguageSelected(language)
                    }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Display the flag alongside the language name in the dropdown
                        Image(
                            painter = painterResource(id = flagResId),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp)) // Add spacing between flag and text
                        Text(text = language)
                    }
                }
            }
        }
    }
}


@Composable
fun LanguageDropDown_Save(
    supportedLanguages: List<String>,
    onLanguageSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf(supportedLanguages[0]) }

    Column(modifier = Modifier.padding(8.dp)) {
        Row(
            modifier = Modifier
                .padding(top = 4.dp)
                .background(colorResource(id = R.color.black)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                modifier = Modifier
                    .background(colorResource(id = R.color.black)),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(id = R.color.black),
                    contentColor = colorResource(id = R.color.white)
                ),
                onClick = { expanded = true }) {
                Text(
                    text = selectedLanguage)
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            supportedLanguages.forEach { language ->
                DropdownMenuItem(
                    onClick = {
                        selectedLanguage = language
                        expanded = false
                        onLanguageSelected(language)
                    }
                ) {
                    BasicText(text = language)
                }
            }
        }
    }
}

@Composable
fun FlagForLocale(language: String, languageFlagList: List<Pair<String, Int>>) {
    val selectedLanguage = language
    val selectedFlag = languageFlagList.find { it.first == selectedLanguage }?.second ?: 0
    val painter: Painter = painterResource(id = selectedFlag)

    // Display the flag image based on the selected language
    Image(
        painter = painter,
        contentDescription = "Flag for $selectedLanguage", // Provide a suitable content description
        modifier = Modifier
            .size(24.dp)
    )
}
