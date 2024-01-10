package com.nickpatrick.swissmoneysaver.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nickpatrick.swissmoneysaver.R
import com.nickpatrick.swissmoneysaver.ui.list.ListViewModel

@Composable
fun BudgetInfoScreen(
    viewModel: ListViewModel,
    context: Context,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.primary_background))
            .padding(top = 64.dp)
    ) {
        // First Text
        Text(
            text = stringResource(id = R.string.budgetClose),
            color = Color.White,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        // Second Text below the Image
        Text(
            text = stringResource(id = R.string.genBudgetInfo),
            color = Color.White,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        // Image between the Text elements
        Image(
            painterResource(id = R.drawable.budgetimage), "null",
            modifier = Modifier
                .fillMaxSize(),
            alignment = Alignment.TopCenter
        )
    }
}
