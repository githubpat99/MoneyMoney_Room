package com.example.moneymoney_room.ui.list

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymoney_room.MoneyMoneyApplication.Constants.startSaldo
import com.example.moneymoney_room.MoneyMoneyTopAppBar
import com.example.moneymoney_room.R
import com.example.moneymoney_room.data.Item
import com.example.moneymoney_room.ui.AppViewModelProvider
import com.example.moneymoney_room.ui.navigation.NavigationDestination
import com.example.moneymoney_room.util.Utilities
import com.example.moneymoney_room.util.Utilities.Companion.formatDoubleToString
import kotlinx.coroutines.launch

object ListDestination : NavigationDestination {
    override val route = "list"
    override val titleRes = R.string.app_name
}

/**
 * Entry route for Home screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ListScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    navigateToDetail: (Int) -> Unit,
    navigateToEntry: () -> Unit,
    canNavigateBack: Boolean = true,
    modifier: Modifier = Modifier,
    viewModel: ListViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {

    val listUiState by viewModel.listUiState.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            MoneyMoneyTopAppBar(
                title = stringResource(id = R.string.list),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->

        ListScreenBody(
            listUiState,
            onItemClick = navigateToDetail,
            navigateToEntry,

            )
    }

}

@Composable
fun ListScreenBody(
    listUiState: ListUiState,
    onItemClick: (Int) -> Unit,
    navigateToEntry: () -> Unit,

    ) {

    var saldoState = remember { mutableStateOf(startSaldo) }
    val customTextStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        color = Color.Black,
        textAlign = TextAlign.Center
        // Add any other desired style properties here
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Header
        val saldoTxt = formatDoubleToString(saldoState.value)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(top = 64.dp),

        ) {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                CustomStyledText(
                    text = "1.1.2023: CHF 6'500.00",
                    textAlign = TextAlign.Left,
                    fontWeight = FontWeight.Bold
                )

                CustomStyledText(
                    text = saldoTxt,
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }

    // Body (Main content)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(top = 120.dp, bottom = 24.dp)
    ) {
        ShowOnlyRelevantElements(
            listUiState.list,
            onItemClick = { onItemClick(it.id) },
            navigateToEntry,
            onSaldoChange = { newSaldo ->
                saldoState.value = newSaldo
            }

        )
    }
}


@Composable
fun ShowOnlyRelevantElements(
    itemList: List<Item>,
    onItemClick: (Item) -> Unit,
    navigateToEntry: () -> Unit,
    onSaldoChange: (Double) -> Unit,

    ) {

    var totalAmountOnScreen: Double = 0.00
    val lazyListState = rememberLazyListState()
    var scrollToIndex = 50 // Change this to the desired index

    // Create a coroutine scope
    val coroutineScope = rememberCoroutineScope()

    val today = Utilities.getNowAsLong()


//    val todayItemIndex = itemList.indexOfFirst { it.timestamp == today.toEpochDay() }
//    if (todayItemIndex != -1) {
//
//    }

    println("ListScreen - itemList = ${itemList.isEmpty()}")

    if (itemList.isEmpty()) {
        Button(
            onClick = navigateToEntry
        ) {
            Text(text = "Add")
        }
    } else {
        var todayItemIndex = itemList.indexOfFirst { it.timestamp > today }

        if (todayItemIndex < 0)
            todayItemIndex = 0

        println("ListScreen - todayItemIndex = $todayItemIndex")

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

        LaunchedEffect(todayItemIndex) {
            // Scroll to the desired index when the effect is launched
            coroutineScope.launch {
                lazyListState.scrollToItem(todayItemIndex)
            }
        }

        println("ListScreen - LazyColumn - firstVisItemIndex = ${lazyListState.firstVisibleItemIndex}")
        val idx = lazyListState.firstVisibleItemIndex
        var i = 0
        var newSaldo = startSaldo
        while (i < idx) {
            println("ListScreen - LazyColumn - Items to firstVisItemIndex - Amount = ${itemList[i].amount}")
            newSaldo = newSaldo + itemList[i].amount
            i++
        }

        println("ListScreen - LazyColumn - new Saldo = $newSaldo")
        onSaldoChange(newSaldo)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemCard(
    item: Item, modifier: Modifier = Modifier,
) {
    val myCardModifier = modifier
        .padding(4.dp)
        .background(color = colorResource(id = R.color.white))

    val now = Utilities.getNowAsLong()
    val visualDate = Utilities.getTimestampAsDate(now)

    var itemColor = colorResource(id = R.color.dark_blue)
    if (item.amount < 0)
        itemColor = colorResource(id = R.color.dark_red)

    val fontFamily = FontFamily.Default

    val textStyle = TextStyle(
        color = itemColor,
        fontFamily = fontFamily, // Set the appropriate font family here
        textAlign = TextAlign.End
    )

    Row(
        modifier = myCardModifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = myCardModifier
                .size(8.dp),
            painter = painterResource(id = R.drawable.baseline_24),
            contentDescription = "Money-Sign"
        )
        Column {
            Text(
                modifier = myCardModifier,
                color = colorResource(id = R.color.gray),
                text = Utilities.getTimestampAsDate(item.timestamp)
            )
            Text(
                modifier = myCardModifier,
                color = colorResource(id = R.color.gray),
                text = item.name
            )
        }

        BasicText(
            text = item.description,
            style = textStyle
        )

        Spacer(modifier = Modifier.weight(0.5f))
        BasicText(
            text = item.amount.toString(),
            style = textStyle
        )

    }
}


@Composable
fun CustomStyledText(
    text: String,
    textAlign: TextAlign,
    fontWeight: FontWeight,
    modifier: Modifier = Modifier,
) {
    // Define a custom TextStyle with the provided textAlign
    val customTextStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        fontWeight = fontWeight,
        color = Color.Black,
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