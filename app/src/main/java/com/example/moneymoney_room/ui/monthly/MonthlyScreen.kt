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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymoney_room.MoneyMoneyTopAppBar
import com.example.moneymoney_room.R
import com.example.moneymoney_room.data.BudgetItem
import com.example.moneymoney_room.data.Item
import com.example.moneymoney_room.ui.AppViewModelProvider
import com.example.moneymoney_room.ui.BudgetInfoScreen
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

                println("MonthlyScreen - ListScreenBody - endSaldo: $endSaldo")

                navigateToMonthlyDetail(
                    month,
                    year,
                    endSaldo,
                    totalAmount
                )
            }, // Pass year and month to navigateToMonthlyDetail
            navigateToEntry,
            startSaldo,
            year
        )
    }
}

@Composable
fun ListScreenBody(
    viewModel: MonthlyViewModel,
    onItemClick: (String, String, Double, Double) -> Unit,
    navigateToEntry: () -> Unit,
    startSaldo: Double,
    year: String,
) {

    val monthlyUiState: MonthlyUiState by viewModel.monthlyUiState.collectAsState()
    val budgetUiState by viewModel.budgetUiState.collectAsState()

    val decimalFormat = DecimalFormat("#,##0.00")

    //todo PIN: Performance issue

    val configState = viewModel.configuration.collectAsState(initial = null)
    val budgetStart = configState.value?.approxStartSaldo ?: 0.0
    val budgetEnd = configState.value?.approxEndSaldo ?: 0.0
    val budgetSaldo = budgetEnd - budgetStart
    val formattedBudgetStartSaldo = decimalFormat.format(budgetStart)
    val formattedBudgetEndSaldo = decimalFormat.format(budgetEnd)
    val formattedBudgetSaldo = decimalFormat.format(budgetSaldo)

    var background = colorResource(id = R.color.green)
    var endSaldo = 0.0

    // only Calculate endSaldo when UiState Information is ready
    if (monthlyUiState.list.isEmpty() == false) {
        endSaldo = calculateEndSaldo(monthlyUiState.list, startSaldo)
        viewModel.updateConfigEndSaldoForYear(endSaldo)
    }


    if (endSaldo < startSaldo) {
        background = colorResource(id = R.color.dark_red)
    }


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

    var dynamicFontWeight by remember {
        mutableStateOf(FontWeight.Normal)
    }

    if (monthlyUiState.list.isEmpty()) {

        BudgetInfoScreen(
            viewModel = listViewModel,
            context = context
        )
    } else {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(id = R.color.light_blue))
        ) {

            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 64.dp),

                ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
//                        .background(colorResource(id = R.color.semi_gray))
                ) {

                    CustomStyledText(
                        text = "",
                        textAlign = TextAlign.Left,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1f)
                            .background(color = colorResource(id = R.color.white)),
                    )

                    if (viewModel.budgetFont.value == FontWeight.Bold) {
                        // Budget is active
                        CustomStyledText(
                            text = "Budget",
                            textAlign = TextAlign.Center,
                            fontWeight = viewModel.budgetFont.value,
                            modifier = Modifier.weight(1f),
                            color = colorResource(id = R.color.white)
                        )
                        CustomStyledText(
                            text = "Live",
                            textAlign = TextAlign.Center,
                            fontWeight = viewModel.liveFont.value,
                            modifier = Modifier
                                .weight(1f)
                                .background(color = colorResource(id = R.color.white)),
                            color = colorResource(id = R.color.white)
                        )
                    } else if (viewModel.liveFont.value == FontWeight.Bold) {
                        // Live is active
                        CustomStyledText(
                            text = "Budget",
                            textAlign = TextAlign.Center,
                            fontWeight = viewModel.budgetFont.value,
                            modifier = Modifier
                                .weight(1f)
                                .background(color = colorResource(id = R.color.white)),
                            color = colorResource(id = R.color.white)
                        )
                        CustomStyledText(
                            text = "Live",
                            textAlign = TextAlign.Center,
                            fontWeight = viewModel.liveFont.value,
                            modifier = Modifier.weight(1f),
                            color = colorResource(id = R.color.white)
                        )
                    } else {
                        // List is active
                        CustomStyledText(
                            text = "Budget",
                            textAlign = TextAlign.Center,
                            fontWeight = viewModel.budgetFont.value,
                            modifier = Modifier.weight(1f),
                            color = colorResource(id = R.color.white)
                        )
                        CustomStyledText(
                            text = "Live",
                            textAlign = TextAlign.Center,
                            fontWeight = viewModel.liveFont.value,
                            modifier = Modifier.weight(1f),
                            color = colorResource(id = R.color.white)
                        )
                    }

                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CustomStyledText(
                        text = "01.01.$year",
                        textAlign = TextAlign.Left,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.weight(1f),
                        color = colorResource(id = R.color.white)
                    )
                    CustomStyledText(
                        text = formattedBudgetStartSaldo,
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.weight(1f),
                        color = colorResource(id = R.color.white)
                    )
                    CustomStyledText(
                        text = "$formattedStartSaldo",
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.weight(1f),
                        color = colorResource(id = R.color.white)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(background)
                ) {

                    CustomStyledText(
                        text = "31.12.$year",
                        textAlign = TextAlign.Left,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        color = colorResource(id = R.color.white)
                    )
                    CustomStyledText(
                        text = formattedBudgetEndSaldo,
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        color = colorResource(id = R.color.white)
                    )
                    CustomStyledText(
                        text = "$formattedEndSaldo",
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        color = colorResource(id = R.color.white)
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
                        modifier = Modifier.weight(1f),
                        color = colorResource(id = R.color.white)
                    )
                    CustomStyledText(
                        text = formattedBudgetSaldo,
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.weight(1f),
                        color = colorResource(id = R.color.white)
                    )
                    CustomStyledText(
                        text = "$formattedSaldo",
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.weight(1f),
                        color = colorResource(id = R.color.white)
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
            .padding(top = 190.dp, bottom = 24.dp)
    ) {

        ShowOnlyRelevantElements(
            monthlyUiState.list,
            budgetUiState.list,
            viewModel,
            startSaldo,
            endSaldo,
            onItemClick,
            navigateToEntry,
            year
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
    budgetItemList: List<BudgetItem>,
    viewModel: MonthlyViewModel,
    startSaldo: Double,
    endSaldo: Double,
    onItemClick: (String, String, Double, Double) -> Unit,
    navigateToEntry: () -> Unit,
    year: String,
) {
    var showChart by remember { mutableStateOf(2) }
    val monthlyTotals = calculateMonthlyTotals(itemList)
    val sumMT = monthlyTotals.sumOf { it.totalAmount }
    val highestMT = monthlyTotals.maxByOrNull { it.totalAmount }
    val highest = highestMT?.totalAmount ?: 0.0
    val lowestMT = monthlyTotals.minByOrNull { it.totalAmount }
    val lowest = lowestMT?.totalAmount ?: 0.0

    val lazyListState = rememberLazyListState()

    // calculate monthly Totals
    val myBudgetItems = budgetItemList

    val budgetTotalsList: List<Triple<String, String, Double>> = if (year.isNotBlank()) {
        val myCalcItems = viewModel.reCalculateBudgetForMonthlyView(myBudgetItems, year).toList()
        val budgetTotals = calculateMonthlyTotals(myCalcItems)

        budgetTotals.map { (_, month, total) ->
            Triple(year, month, total)
        }
    } else {
        emptyList()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (showChart == 1) {
            // Show Budget Chart
            viewModel.budgetFont.value = FontWeight.Bold
            viewModel.liveFont.value = FontWeight.Normal

            Column(modifier = Modifier.fillMaxWidth()) {
                var chartSaldo = 0.0

                // set the Calculation Factor for my Canvas
                val totalWidth = 100
                val startSaldoWidth = 60
                val restWidth = totalWidth - startSaldoWidth - 4
                var factor = 1

                while (restWidth < sumMT / factor) factor++
                while (restWidth < highest / factor) factor++
                while (restWidth < lowest * -1 / factor) factor++

                monthlyTotals.forEach { monthlyTotal ->
                    key(monthlyTotal) {
                        val (year, month, totalAmount) = monthlyTotal

                        val df = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale("de", "CH")))

                        val budgetTotalForMonth =
                            getBudgetTotalForMonth(month, year, budgetTotalsList)

                        chartSaldo += budgetTotalForMonth
                        val subTotDouble = chartSaldo + startSaldo
                        val subTotal = df.format(subTotDouble)

                        val formattedBudgetTotal = df.format(budgetTotalForMonth)
                        val monthText = Utilities.MonthUtils.getMonthName(month)

                        // todo PIN - correct values for Float using the Calculation Factor
                        val color = if (chartSaldo > 0)
                            colorResource(id = R.color.light_blue)
                        else colorResource(id = R.color.transparent)
                        val slices = listOf(
                            Slice(
                                saldo = subTotal,
                                value = startSaldoWidth.toFloat(),
                                color = colorResource(id = R.color.light_blue),
                                text = monthText
                            ),
                            Slice(
                                saldo = subTotal,
                                value = (chartSaldo / factor).toFloat(),
                                color = color,
                                text = formattedBudgetTotal
                            )
                        )

                        StackedBar(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(30.dp),
                            slices = slices
                        )
                    }
                }
            }
        }
        if (showChart == 2) {
            // Show Live Chart
            viewModel.budgetFont.value = FontWeight.Normal
            viewModel.liveFont.value = FontWeight.Bold

            Column(modifier = Modifier.fillMaxWidth()) {
                var chartSaldo = 0.0

                // set the Calculation Factor for my Canvas
                val totalWidth = 100
                val startSaldoWidth = 60
                val restWidth = totalWidth - startSaldoWidth - 4
                var factor = 1

                while (restWidth < sumMT / factor) factor++
                while (restWidth < highest / factor) factor++
                while (restWidth < lowest * -1 / factor) factor++

                monthlyTotals.forEach { monthlyTotal ->
                    key(monthlyTotal) {
                        val (year, month, totalAmount) = monthlyTotal
                        val df = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale("de", "CH")))

                        chartSaldo += totalAmount
                        val subTotDouble = chartSaldo + startSaldo
                        val subTotal = df.format(subTotDouble)


                        val formattedTotalAmount = df.format(totalAmount)

                        val monthText = Utilities.MonthUtils.getMonthName(month)

                        // todo PIN - correct values for Float using the Calculation Factor
                        val color = if (chartSaldo > 0)
                            colorResource(id = R.color.light_blue)
                        else colorResource(id = R.color.transparent)
                        val slices = listOf(
                            Slice(
                                saldo = subTotal,
                                value = startSaldoWidth.toFloat(),
                                color = colorResource(id = R.color.light_blue),
                                text = monthText
                            ),
                            Slice(
                                saldo = subTotal,
                                value = (chartSaldo / factor).toFloat(),
                                color = color,
                                text = formattedTotalAmount
                            )
                        )

                        StackedBar(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(30.dp),
                            slices = slices
                        )
                    }
                }
            }
        }
        if (showChart == 0) {
            viewModel.budgetFont.value = FontWeight.SemiBold
            viewModel.liveFont.value = FontWeight.SemiBold

            LazyColumn(
                state = lazyListState
            ) {

                items(monthlyTotals) { monthlyTotal ->
                    key(monthlyTotal) {
                        val (year, month, totalAmount) = monthlyTotal

                        val df = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale("de", "CH")))
                        val formattedTotalAmount = df.format(totalAmount)
                        var itemColor = colorResource(id = R.color.white)
                        if (totalAmount < 0) {
                            itemColor = colorResource(id = R.color.ausgabe_Vorlage)
                        }

                        val budgetTotalForMonth =
                            getBudgetTotalForMonth(month, year, budgetTotalsList)
                        val formattedBudgetTotal = df.format(budgetTotalForMonth)

                        MonthlyCard(
                            month,
                            formattedTotalAmount,
                            formattedBudgetTotal,
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
        ) {
            Row {
                Button(
                    modifier = Modifier.padding(start = 8.dp),
                    enabled = (showChart != 0),
                    onClick = { showChart = 0 }
                ) {
                    Text("List")
                }
                Button(
                    modifier = Modifier.padding(start = 8.dp),
                    enabled = (showChart != 1),
                    onClick = { showChart = 1 }
                ) {
                    Text("Budget Chart")
                }
                Button(
                    modifier = Modifier.padding(start = 8.dp),
                    enabled = (showChart != 2),
                    onClick = { showChart = 2 }
                ) {
                    Text("Live Chart")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyCard(
    month: String,
    formattedTotalAmount: String,
    formattedBudgetTotal: String,
    itemColor: Color,
    modifier: Modifier = Modifier,
) {
    val myCardModifier = modifier
        .padding(start = 4.dp, top = 3.dp, end = 4.dp, bottom = 2.dp)
        .background(color = colorResource(id = R.color.light_blue))

    Row(
        modifier = Modifier
            .padding(bottom = 3.dp)
            .background(colorResource(id = R.color.light_blue))
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(0.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = myCardModifier
                .weight(1f),
            text = Utilities.MonthUtils.getMonthName(month),
            style = TextStyle(
                color = colorResource(id = R.color.white),
                textAlign = TextAlign.Left,
                fontSize = 16.sp
            )
        )
        Text(
            modifier = myCardModifier
                .weight(1f),
            text = formattedBudgetTotal,
            style = TextStyle(
                color = colorResource(id = R.color.light_gray),
                textAlign = TextAlign.End,
                fontSize = 16.sp
            )
        )
        Text(
            modifier = myCardModifier
                .weight(1f),
            color = itemColor,
            text = formattedTotalAmount,
            style = TextStyle(
                color = colorResource(id = R.color.white),
                textAlign = TextAlign.End,
                fontSize = 16.sp
            )
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
        modifier = modifier
            .padding(4.dp)
//            .background(color = colorResource(id = R.color.semi_gray))
    )
}

fun getBudgetTotalForMonth(
    month: String,
    year: String,
    budgetTotals: List<Triple<String, String, Double>>,
): Double {
    return budgetTotals.firstOrNull { it.first == year && it.second == month }?.third ?: 0.0
}


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

data class MonthlyTotal(val year: String, val month: String, val totalAmount: Double)