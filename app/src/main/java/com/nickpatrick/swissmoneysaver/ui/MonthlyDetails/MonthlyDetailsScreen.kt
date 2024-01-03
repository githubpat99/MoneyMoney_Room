package com.nickpatrick.swissmoneysaver.ui.MonthlyDetails

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nickpatrick.swissmoneysaver.MoneyMoneyTopAppBar
import com.nickpatrick.swissmoneysaver.R
import com.nickpatrick.swissmoneysaver.data.Item
import com.nickpatrick.swissmoneysaver.ui.AppViewModelProvider
import com.nickpatrick.swissmoneysaver.ui.list.ItemCard
import com.nickpatrick.swissmoneysaver.ui.navigation.NavigationDestination
import com.nickpatrick.swissmoneysaver.util.Utilities
import kotlinx.coroutines.launch
import java.text.DecimalFormat

object MonthlyDetailDestination : NavigationDestination {
    const val Month = "month"
    const val Year = "year"
    const val EndSaldo = "endSaldo"
    const val MonthlyTotal = "monthlyTotal"
    override val route = "monthlyDetail"
    override val titleRes = R.string.app_name

    val routeWithArgs =
        "$route/{$Month}/{$Year}/{$EndSaldo}/{$MonthlyTotal}"  //todo PIN: itemIdArg always in {}
}

/**
 * Entry route for Home screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MonthlyDetailsScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    month: String,
    year: String,
    endSaldo: Double,
    monthlyTotal: Double,
    navigateToDetail: (Int) -> Unit,
    navigateToEntry: () -> Unit,
    canNavigateBack: Boolean = true,
    modifier: Modifier = Modifier,
    viewModel: MonthlyDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {

    val coroutineScope = rememberCoroutineScope()
    val configurationState = viewModel.configuration.collectAsState(initial = null)
    var startSaldo = 0.0
    val monthTxt = Utilities.MonthUtils.getMonthName(viewModel.month.toString())

    if (configurationState.value != null) {
        startSaldo = configurationState.value!!.startSaldo
    }

    val monthlyDetailsUiState = viewModel.monthlyDetailsUiState.collectAsState().value
    var monthlyTotal = viewModel.monthlyTotal
    var monthlyCalc = 0.0
    var endSaldo = 0.0

    // only Calculate endSaldo when UiState Information is ready
    if (monthlyDetailsUiState.list.isEmpty() == false) {
        monthlyCalc = monthlyDetailsUiState.list.sumOf { it.amount }    // actual Month Total
        endSaldo = viewModel.endSaldo - monthlyTotal + monthlyCalc
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            MoneyMoneyTopAppBar(
                title = "$monthTxt",
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        ListScreenBody(
            monthlyUiState = monthlyDetailsUiState,
            onItemClick = navigateToDetail,
            navigateToEntry,
            startSaldo,
            endSaldo,
            monthlyCalc,
            viewModel
        )
    }
}

@Composable
fun ListScreenBody(
    monthlyUiState: MonthlyDetailsUiState,
    onItemClick: (Int) -> Unit,
    navigateToEntry: () -> Unit,
    startSaldo: Double,
    endSaldo: Double,
    monthlyTotal: Double,
    viewModel: MonthlyDetailsViewModel,

    ) {
    val coroutineScope = rememberCoroutineScope()
    var saldoState = remember { mutableStateOf(startSaldo) }
    val decimalFormat = DecimalFormat("#,##0.00")

    val yearAndMonthString = viewModel.yearAndMonth.toString()
    val yearString = yearAndMonthString.substring(0,4)
    val yearInt = yearString.toInt()

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

                com.nickpatrick.swissmoneysaver.ui.monthly.CustomStyledText(
                    text = "",
                    textAlign = TextAlign.Left,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .background(color = colorResource(id = R.color.white)),
                )
                com.nickpatrick.swissmoneysaver.ui.monthly.CustomStyledText(
                    text = "",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .background(color = colorResource(id = R.color.white)),
                )

                com.nickpatrick.swissmoneysaver.ui.monthly.CustomStyledText(
                    text = "Live",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    color = colorResource(id = R.color.white)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                val formattedSaldo: String = decimalFormat.format(startSaldo)


                CustomStyledText(
                    text = "01.01.$yearString",
                    textAlign = TextAlign.Left,
                    fontWeight = FontWeight.Normal,
                    color = colorResource(id = R.color.white)
                )

                CustomStyledText(
                    text = "$formattedSaldo",
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.Normal,
                    color = colorResource(id = R.color.white)
                )
            }

            var background = colorResource(id = R.color.green)
            if (endSaldo < startSaldo) {
                background = colorResource(id = R.color.dark_red)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(background),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val formattedSaldo: String = decimalFormat.format(endSaldo)


                CustomStyledText(
                    text = "31.12.$yearString",
                    textAlign = TextAlign.Left,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.white)
                )

                CustomStyledText(
                    text = "$formattedSaldo",
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.white)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val formattedSaldo: String = decimalFormat.format(monthlyTotal)

                CustomStyledText(
                    text = "Saldo",
                    textAlign = TextAlign.Left,
                    fontWeight = FontWeight.Normal,
                    color = colorResource(id = R.color.white)
                )

                CustomStyledText(
                    text = "$formattedSaldo",
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.Normal,
                    color = colorResource(id = R.color.white)
                )
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
                startSaldo,
                onItemClick = { onItemClick(it.id) },
                navigateToEntry,
                onSaldoChange = { newSaldo ->
                    saldoState.value = newSaldo
                }
            )
        }
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
        ){


            var yearInt = 0
            var monthInt = 0

            Button(
                modifier = Modifier
                    .padding(4.dp),
                onClick = {
                    val ts = viewModel.firstTimestamp
                    val item: Item =
                        Item(0, ts, "Neu", "", 12, 0.0, 0.0, false)

                    coroutineScope.launch {
                        viewModel.saveItem(item)
                    }
                },
                enabled = true,
                colors = ButtonDefaults.buttonColors(
                    contentColor = colorResource(id = R.color.white),
                    containerColor = colorResource(id = R.color.light_blue)
                ),
                border = BorderStroke(1.dp, colorResource(id = R.color.white))
            ) {

                Icon(
                    painterResource(id = R.drawable.add_24), // Use your custom "+" icon
                    contentDescription = null, // Provide a content description if needed
                    modifier = Modifier.size(24.dp)
                )
            }
        }
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
    onItemClick: (Item) -> Unit,
    navigateToEntry: () -> Unit,
    onSaldoChange: (Double) -> Unit,

    ) {

    val lazyListState = rememberLazyListState()

    LazyColumn(
        state = lazyListState
    ) {
        items(items = itemList) {
            ItemCard(
                item = it,
                modifier = Modifier
                    .clickable { onItemClick(it) }
            )
        }

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
        modifier = modifier.padding(4.dp),
    )
}