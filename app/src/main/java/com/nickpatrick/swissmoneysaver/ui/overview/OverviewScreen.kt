package com.nickpatrick.swissmoneysaver.ui.overview

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nickpatrick.swissmoneysaver.MoneyMoneyTopAppBar
import com.nickpatrick.swissmoneysaver.R
import com.nickpatrick.swissmoneysaver.ui.AppViewModelProvider
import com.nickpatrick.swissmoneysaver.ui.navigation.NavigationDestination
import com.nickpatrick.swissmoneysaver.util.Utilities
import java.time.LocalDateTime
import java.time.ZoneOffset

object OverviewDestination : NavigationDestination {
    override val route = "overview"
    override val titleRes = R.string.app_name
}

/**
 * Entry route for Home screen
 */

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    modifier: Modifier = Modifier,
    viewModel: OverviewViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier,
        topBar = {
            MoneyMoneyTopAppBar(
                title = stringResource(id = R.string.overview),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(id = R.color.light_gray))
        ) {
            HorizontalScrollableScreenBody(viewModel)
            Spacer(
                modifier = Modifier
                    .height(8.dp)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun HorizontalScrollableScreenBody(
    viewModel: OverviewViewModel,
) {
    // Replace this list with your actual content
    val myBudgets = viewModel.overviewUiState.value

    val itemList = listOf("2023", "2024", "2025")

    val budgetYear = viewModel.overviewUiState.value.budgetYear
    val approxStart = viewModel.overviewUiState.value.approxStartSaldo
    val approxEnd = viewModel.overviewUiState.value.approxEndSaldo
    val budgetTs = viewModel.overviewUiState.value.ts
    val budgetDatum = Utilities.getStringDateFromTimestamp(budgetTs)
    val nowTs = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
    val nowString = Utilities.getStringDateFromTimestamp(nowTs)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 72.dp)
            .horizontalScroll(rememberScrollState())
            .background(colorResource(id = R.color.light_gray))
    ) {
        itemList.forEach { item ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .width(280.dp)
                    .height(480.dp)
                    .background(color = colorResource(id = R.color.primary_background))
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp) // Add padding for better spacing
                ) {
                    Text(
                        text = budgetYear.toString(),
                        style = TextStyle(
                            color = colorResource(id = R.color.white),
                            fontWeight = FontWeight.Bold,
                        ),
                        fontSize = 24.sp
                    )
                    // BudgetBox(approxStart, approxEnd, budgetDatum)
                    // LiveDataBox(approxStart, approxEnd, nowString)
                }
            }
        }
    }
}



