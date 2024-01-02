package com.example.moneymoney_room.ui.home

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymoney_room.R
import com.example.moneymoney_room.ui.AppViewModelProvider
import com.example.moneymoney_room.ui.navigation.NavigationDestination
import com.example.moneymoney_room.ui.overview.BudgetBox
import com.example.moneymoney_room.ui.overview.LiveDataBox
import com.example.moneymoney_room.util.Utilities
import com.example.moneymoney_room.util.VideoPlayerComponent
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset


object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

/**
 * Entry route for Home screen
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navigateToBudgetForm: (String, String) -> Unit,
    navigateToRegistration: () -> Unit,
    navigateToMonthly: (String) -> Unit,
    navigateToBudget: () -> Unit,
    navigateToGooglePicker: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {

    val configItemsState = viewModel.configItems
        .collectAsState(initial = emptyList())
    var initSwitch = false
    if (configItemsState.value.isEmpty()) {
        initSwitch = true
    } else {
        // Set Configuration to archive when Year changed
        configItemsState.value.forEach {
            if (it != null) {
                if (it.status < 2 && it.budgetYear < Utilities.getActualYear().toInt()) {
                    // set Status to archived - Status 2
                    viewModel.updateStatusToArchived(it)
                }
            }
        }
    }

    val appContext = LocalContext.current.applicationContext
    var switch: Boolean = false
    var playVideo: Boolean by remember { mutableStateOf(false) }
    var stopPlayback: () -> Unit = {
        // Stop video playback
        playVideo = false
    }

    val player = remember {
        val exoPlayer = SimpleExoPlayer.Builder(appContext).build()
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    playVideo = false // Update playVideo state to hide the video
                }
            }

            // Implement other required methods here if needed
        })
        exoPlayer
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.primary_background)),
        contentAlignment = Alignment.TopCenter,
    )
    {
        Image(
            painterResource(id = R.drawable.budgetimage), "null",
            modifier = Modifier
                .fillMaxSize(),
            alignment = Alignment.TopCenter
        )

        Column(
            modifier = Modifier
                .padding(12.dp, 160.dp, 0.dp, 24.dp)
        ) {
            LoginCard(
                homeUiState = viewModel.homeUiState,
                onValueChange = viewModel::chgUserInfo,
                modifier = Modifier

            )

            if (viewModel.homeUiState.password.isNotBlank()) {
                if (viewModel.homeUiState.password == "Viel Spass...") {
                    switch = true
                }
            }

            if (playVideo) {

                VideoPlayerComponent(
                    player = player,
                    context = appContext,
                    playVideo = playVideo,
                    onTogglePlayback = { newPlayState ->
                        playVideo = newPlayState
                    },
                    onStopPlayback = stopPlayback
                )
            } else {

                Divider(
                    modifier = Modifier
                        .padding(top = 12.dp),
                    color = Color.Gray, thickness = 1.dp
                )

                HorizontalScrollableOverview(
                    {
                        navigateToBudgetForm(it, "0")
                    },
                    { navigateToMonthly(it) },
                    viewModel
                )

                Divider(
                    modifier = Modifier
                        .padding(top = 4.dp),
                    color = Color.Gray, thickness = 1.dp
                )

                if (switch == true) {
                    Column {

                        Row {
                            Button(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .weight(1f),
                                onClick = {
                                    playVideo = true
                                }
                            ) {
                                Text(
                                    text = "Why?",
                                    style = TextStyle(color = colorResource(id = R.color.white))
                                )
                            }

                            if (initSwitch == true) {
                                Button(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .weight(1f),
                                    onClick = {
                                        // create Base Config for actual year
                                        viewModel.initializeConfigForYear()
                                    }
                                ) {
                                    Text(
                                        text = "Start Budget",
                                        style = TextStyle(color = colorResource(id = R.color.white))
                                    )
                                }
                            } else {

                                ActionButton(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .weight(1f),
                                    active = switch,
                                    navigateToRegistration,
                                    text = "Budget-Mgmt"
                                )

                            }
                        }

                    }
                }

            }
        }
    }
}


@Composable
fun ActionButton(
    modifier: Modifier,
    active: Boolean,
    navigateToList: () -> Unit,
    text: String,
) {
    Button(
        modifier = modifier,
        onClick = navigateToList,
        enabled = active,
        colors = ButtonDefaults.buttonColors(
            contentColor = colorResource(id = R.color.white),
            disabledContentColor = colorResource(id = R.color.light_gray)
        )
    ) {
        Text(
            text = text,
            style = TextStyle(color = colorResource(id = R.color.white))
        )

    }
}

@Composable
fun LoginCard(
    homeUiState: HomeUiState,
    onValueChange: (HomeUiState) -> Unit,
    modifier: Modifier,
) {
    ElevatedCard(
        modifier = modifier
    ) {
        Column {
            // userId
            OutlinedTextField(
                value = homeUiState.userId,
                readOnly = true,
                onValueChange = { onValueChange(homeUiState.copy(userId = it)) },
                label = {
                    Text(
                        "User",
                        color = colorResource(id = R.color.light_gray)
                    )
                },
                visualTransformation = VisualTransformation.None,
                keyboardOptions = KeyboardOptions.Default,
                keyboardActions = KeyboardActions(onDone = {}),
                textStyle = TextStyle(
                    colorResource(id = R.color.white),
                    fontSize = 14.sp
                ),
                maxLines = 1,
                modifier = Modifier
                    .background(color = colorResource(id = R.color.black))
            )
            // userId
            OutlinedTextField(
                value = homeUiState.password,
                onValueChange = { onValueChange(homeUiState.copy(password = it)) },
                label = {
                    Text(
                        "Passwort",
                        color = colorResource(id = R.color.light_gray)
                    )
                },
                visualTransformation = VisualTransformation.None,
                keyboardOptions = KeyboardOptions.Default,
                keyboardActions = KeyboardActions(onDone = {}),
                textStyle = TextStyle(
                    colorResource(id = R.color.white),
                    fontSize = 18.sp
                ),
                maxLines = 1,
                modifier = Modifier
                    .background(color = colorResource(id = R.color.black))
            )
        }

    }
}

@Composable
fun HorizontalScrollableOverview(
    navigateToBudget: (String) -> Unit,
    navigateToMonthly: (String) -> Unit,
    viewModel: HomeViewModel,
) {

    val coroutineScope = rememberCoroutineScope()
    // Replace this list with your actual content
    viewModel.overviewUiState.value
    val configItemsState = viewModel.configItems.collectAsState(initial = emptyList())

    val nowTs = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
    val nowString = Utilities.getStringDateFromTimestamp(nowTs)

    val items = configItemsState.value.sortedBy { it?.budgetYear ?: 0 }

    val visIndex = viewModel.getVisibleIdx(items)
    var scrollingDone by remember {
        mutableStateOf(false)
    }

    val scrollState = rememberScrollState()
    DisposableEffect(visIndex) {
        coroutineScope.launch {
            scrollState.animateScrollTo((660 * visIndex).toFloat().toInt())
            scrollingDone = true
        }
        onDispose { }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .background(color = colorResource(id = R.color.primary_background))
    ) {

        items.forEachIndexed { index, item ->

            Box(
                modifier = Modifier
                    .width(220.dp)
                    .height(320.dp)
                    .background(color = colorResource(id = R.color.primary_background))
            ) {
                val budgetDate = Utilities.getStringDateFromTimestamp(item?.ts ?: 0)

                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = item?.budgetYear.toString(),
                        style = TextStyle(
                            color = colorResource(id = R.color.white),
                            fontWeight = FontWeight.Bold,
                        ),
                        fontSize = 24.sp
                    )
                    BudgetBox(
                        { navigateToBudget(it) },
                        item?.budgetYear.toString(),
                        item?.approxStartSaldo ?: 0.0,
                        item?.approxEndSaldo ?: 0.0,
                        budgetDate,
                        item?.status ?: 0
                    )
                    LiveDataBox(
                        { navigateToMonthly(it) },
                        item?.budgetYear.toString(),
                        item?.startSaldo ?: 0.0,
                        item?.endSaldo ?: 0.0,
                        nowString
                    )
                }
            }
        }
    }
}


