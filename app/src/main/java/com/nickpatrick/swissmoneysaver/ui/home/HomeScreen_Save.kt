package com.nickpatrick.swissmoneysaver.ui.home

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.nickpatrick.swissmoneysaver.R
import com.nickpatrick.swissmoneysaver.ui.AppViewModelProvider
import com.nickpatrick.swissmoneysaver.ui.navigation.NavigationDestination
import com.nickpatrick.swissmoneysaver.util.Utilities
import com.nickpatrick.swissmoneysaver.util.VideoPlayerComponent


object HomeDestinationSave : NavigationDestination {
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
fun HomeScreenSave(
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

                            }
                        }

                    }
                }

            }
        }
    }
}
