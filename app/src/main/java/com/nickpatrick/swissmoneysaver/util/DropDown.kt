package com.nickpatrick.swissmoneysaver.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nickpatrick.swissmoneysaver.R
import com.nickpatrick.swissmoneysaver.ui.budgetDetails.BudgetItemDetails
import com.nickpatrick.swissmoneysaver.ui.entry.ItemDetails

@Composable
fun DropDownWithArrowAndEntryField4BudgetItems(
    dropDownlistItems: List<String>,
    onValueChange: (BudgetItemDetails) -> Unit,
    budgetItemDetails: BudgetItemDetails,
) {
    var expanded by remember { mutableStateOf(false) }

    Row {
        Text(
            text = stringResource(id = R.string.name) + " ▼ ", // Your label text here
            color = colorResource(id = R.color.gray), // Color for the label (optional)
            fontSize = 14.sp, // Font size for the label (optional)
            modifier = Modifier
                .clickable {
                    expanded = true
                }
                .padding(start = 16.dp)
        )
    }

    if (expanded) {
        DropdownMenu(
            expanded = true,
            onDismissRequest = { expanded = false },
        ) {
            dropDownlistItems.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        onValueChange(budgetItemDetails.copy(name = item))
                        expanded = false
                    },
                    text = {
                        Text(text = item)
                    }
                )
            }
        }
    }
    Row {
        OutlinedTextField(
            value = budgetItemDetails.name,
            onValueChange = {
                onValueChange(budgetItemDetails.copy(name = it))
            },
            textStyle = TextStyle(
                color = colorResource(id = R.color.white),
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
        )
    }
}

@Composable
fun DropDownWithArrowAndEntryField4Items(
    dropDownlistItems: List<String>,
    onValueChange: (ItemDetails) -> Unit,
    itemDetails: ItemDetails,
) {
    var expanded by remember { mutableStateOf(false) }

    Row {
        Text(
            text = stringResource(id = R.string.name) + " ▼ ", // Your label text here
            color = colorResource(id = R.color.gray), // Color for the label (optional)
            fontSize = 14.sp, // Font size for the label (optional)
            modifier = Modifier
                .clickable {
                    expanded = true
                }
                .padding(start = 16.dp)
        )
    }

    if (expanded) {
        DropdownMenu(
            expanded = true,
            onDismissRequest = { expanded = false },
        ) {
            dropDownlistItems.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        onValueChange(itemDetails.copy(description = item))
                        expanded = false
                    },
                    text = {
                        Text(text = item)
                    }
                )
            }
        }
    }
    Row {
        OutlinedTextField(
            value = itemDetails.description,
            onValueChange = {
                onValueChange(itemDetails.copy(description = it))
            },
            textStyle = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
        )
    }
}


