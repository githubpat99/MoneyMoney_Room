package com.example.moneymoney_room.ui.monthly

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymoney_room.MoneyMoneyTopAppBar
import com.example.moneymoney_room.R
import com.example.moneymoney_room.data.Item
import com.example.moneymoney_room.ui.AppViewModelProvider
import com.example.moneymoney_room.ui.CreateBudgetScreen
import com.example.moneymoney_room.ui.list.ListViewModel
import com.example.moneymoney_room.ui.navigation.NavigationDestination
import com.example.moneymoney_room.util.Utilities
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Locale

object MonthlyDestination : NavigationDestination {
    override val route = "monthly"
    override val titleRes = R.string.app_name
    const val year = "year"
    val routeWithArgs = "$route/{$year}"
}

/**
 * Entry route for Home screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MonthlyScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    year: String,
    navigateToMonthlyDetail: (String, String, Double, Double) -> Unit,        //todo PIN: Navigate to xxxList
    navigateToEntry: () -> Unit,
    canNavigateBack: Boolean = true,
    modifier: Modifier = Modifier,
    viewModel: MonthlyViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {

    val configurationState = viewModel.configuration.collectAsState(initial = null)
    var startSaldo = 0.0
    var title = "Budget / Live - Daten"
    var year = ""

    if (configurationState.value != null) {
        startSaldo = configurationState.value!!.startSaldo
        year = configurationState.value!!.budgetYear.toString()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            MoneyMoneyTopAppBar(
                title = "Budget / Live - $year",
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        ListScreenBody(
            viewModel,
            onItemClick = { month, year, endSaldo, totalAmount ->
                navigateToMonthlyDetail(
                    month,
                    year,
                    endSaldo,
                    totalAmount
                )
            }, // Pass year and month to navigateToMonthlyDetail
            navigateToEntry,
            startSaldo
        )
    }
}

@Composable
fun ListScreenBody(
    viewModel: MonthlyViewModel,
    onItemClick: (String, String, Double, Double) -> Unit,
    navigateToEntry: () -> Unit,
    startSaldo: Double,

    ) {

    val monthlyUiState: MonthlyUiState by viewModel.monthlyUiState.collectAsState()

    println("MonthlyScreen - monthlyUiState: ${monthlyUiState.list}")

    //todo PIN: Performance issue

    var background = Color.Green
    val endSaldo = calculateEndSaldo(monthlyUiState.list, startSaldo)
    if (endSaldo < startSaldo) {
        background = Color.Red
    }

    val decimalFormat = DecimalFormat("#,###.##")
    val formattedStartSaldo: String = decimalFormat.format(startSaldo)
    val formattedSaldo: String = decimalFormat.format(endSaldo - startSaldo)
    val formattedEndSaldo: String = decimalFormat.format(endSaldo)

    var saldoColor = colorResource(id = R.color.dark_blue)

    if (endSaldo - startSaldo < 0) {
        saldoColor = colorResource(id = R.color.dark_red)
    }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val listViewModel: ListViewModel = viewModel(factory = AppViewModelProvider.Factory)

    if (monthlyUiState.list.isEmpty()) {

        // Create Budget first
        CreateBudgetScreen(
            viewModel = listViewModel,
            context = context,
            coroutineScope = coroutineScope
        )
    } else {

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {

            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray)
                    .padding(top = 64.dp),

                ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorResource(id = R.color.gray))
                ) {

                    CustomStyledText(
                        text = "",
                        textAlign = TextAlign.Left,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    CustomStyledText(
                        text = "Budget",
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        color = colorResource(id = R.color.white)
                    )

                    CustomStyledText(
                        text = "Live  ",
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        color = colorResource(id = R.color.white)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CustomStyledText(
                        text = "01.01.2023",
                        textAlign = TextAlign.Left,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    CustomStyledText(
                        text = "5'500.00",
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.weight(1f)
                    )
                    CustomStyledText(
                        text = "$formattedStartSaldo",
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.weight(1f)
                    )
                }



                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(background)
                ) {

                    CustomStyledText(
                        text = "31.12.2023",
                        textAlign = TextAlign.Left,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    CustomStyledText(
                        text = "17'500.00",
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    CustomStyledText(
                        text = "$formattedEndSaldo",
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    CustomStyledText(
                        text = "Saldo",
                        textAlign = TextAlign.Left,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.weight(1f)
                    )
                    CustomStyledText(
                        text = "12'000.00",
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.weight(1f)
                    )
                    CustomStyledText(
                        text = "$formattedSaldo",
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.weight(1f),
                        color = saldoColor
                    )
                }
            }
        }
    }

    // Body (Main content)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(top = 210.dp, bottom = 24.dp)
    ) {

        println("MonthlyScreen - ListScreenBody - call ShowOnlyRelevantElements")

        ShowOnlyRelevantElements(
            monthlyUiState.list,
            startSaldo,
            endSaldo,
            onItemClick,
            navigateToEntry
        )
    }
}

fun calculateEndSaldo(list: List<Item>, startSaldo: Double): Double {
    var saldo = startSaldo
    for (item in list) {
        saldo += item.amount
    }
    return saldo
}


@Composable
fun ShowOnlyRelevantElements(
    itemList: List<Item>,
    startSaldo: Double,
    endSaldo: Double,
    onItemClick: (String, String, Double, Double) -> Unit,
    navigateToEntry: () -> Unit,

    ) {
    val monthlyTotals = calculateMonthlyTotals(itemList)
    val lazyListState = rememberLazyListState()

    println("MonthlyScreen - ShowOnlyRelevantElements - monthlyTotals = $monthlyTotals")

    LazyColumn(
        state = lazyListState
    ) {

        items(monthlyTotals) { monthlyTotal ->
            key(monthlyTotal) {
                val (year, month, totalAmount) = monthlyTotal

                val df = DecimalFormat("#,###.00", DecimalFormatSymbols(Locale("de", "CH")))
                val formattedTotalAmount = df.format(totalAmount)
                var itemColor = colorResource(id = R.color.dark_blue)
                if (totalAmount < 0) {
                    itemColor = colorResource(id = R.color.dark_red)
                }

                MonthlyCard(
                    month,
                    formattedTotalAmount,
                    itemColor,
                    modifier = Modifier.clickable {
                        onItemClick(
                            month,
                            year,
                            endSaldo,
                            totalAmount
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyCard(
    month: String,
    formattedTotalAmount: String,
    itemColor: Color,
    modifier: Modifier = Modifier,
) {
    val myCardModifier = modifier
        .padding(4.dp)
        .background(color = colorResource(id = R.color.white))

    Row(
        modifier = myCardModifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = myCardModifier
                .weight(1f),
            color = colorResource(id = R.color.gray),
            text = Utilities.MonthUtils.getMonthName(month)
        )
        Text(
            modifier = myCardModifier
                .weight(1f),
            color = colorResource(id = R.color.gray),
            text = Utilities.MonthUtils.getMonthName(month),
            textAlign = TextAlign.End
        )
        Text(
            modifier = myCardModifier
                .weight(1f),
            color = itemColor,
            text = formattedTotalAmount,
            textAlign = TextAlign.End
        )
    }
}


@Composable
fun CustomStyledText(
    text: String,
    textAlign: TextAlign,
    fontWeight: FontWeight,
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
) {
    // Define a custom TextStyle with the provided textAlign
    val customTextStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        fontWeight = fontWeight,
        color = color,
        textAlign = textAlign,
        // Add any other desired style properties here
    )

    // Apply the custom TextStyle to the BasicText
    BasicText(
        text = text,
        style = customTextStyle,
        modifier = modifier.padding(8.dp)
    )
}

data class MonthlyTotal(val year: String, val month: String, val totalAmount: Double)

fun calculateMonthlyTotals(itemList: List<Item>): List<MonthlyTotal> {
    val monthlyTotals = mutableMapOf<String, Double>()
    val dateFormat = SimpleDateFormat("dd.MM.yyyy")
    val monthFormat = SimpleDateFormat("MM")
    val yearFormat = SimpleDateFormat("yyyy")

    for (item in itemList) {
        val date = dateFormat.parse(Utilities.getTimestampAsDate(item.timestamp))
        val month = monthFormat.format(date)
        val year = yearFormat.format(date)
        val key = "$year-$month" // Combine year and month as a key
        monthlyTotals[key] = monthlyTotals.getOrDefault(key, 0.0) + item.amount
    }

    return monthlyTotals.map { (yearMonth, totalAmount) ->
        val (year, month) = yearMonth.split("-")
        MonthlyTotal(year, month, totalAmount)
    }
}