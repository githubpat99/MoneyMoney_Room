package com.example.moneymoney_room.ui.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneymoney_room.R
import com.example.moneymoney_room.util.Utilities
import java.text.DecimalFormat


@Composable
fun LiveDataBox(
    navigateToMonthly: (String) -> Unit,
    budgetYear: String,
    start: Double,
    end: Double,
    nowString: String,
) {

    val (startDate, endDate) = Utilities.getFormattedStartAndEndDatesForYear(budgetYear)
    val decimalFormat = DecimalFormat("#,##0.00")
    val startText = decimalFormat.format(start)
    val endText = decimalFormat.format(end)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
            .background(colorResource(id = R.color.semi_gray))
            .clickable { navigateToMonthly(budgetYear) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter =
                    if (budgetYear < Utilities.getActualYear()) {
                        painterResource(id = R.drawable.baseline_archive_24)
                    } else {
                        painterResource(id = R.drawable.baseline_nightlife_24)
                    },
                    contentDescription = "Live",
                    tint = colorResource(id = R.color.white)
                )
                Text(
                    text = " Live",
                    style = TextStyle(
                        color = colorResource(id = R.color.white),
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Right
                    ),
                    fontSize = 16.sp
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left-aligned text
                Text(
                    text = startDate,
                    style = TextStyle(
                        color = colorResource(id = R.color.white),
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Left
                    ),
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f) // Adjust weight as needed
                )

                // Right-aligned text
                Text(
                    text = startText,
                    style = TextStyle(
                        color = colorResource(id = R.color.white),
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Right
                    ),
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f) // Adjust weight as needed
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left-aligned text
                Text(
                    text = endDate,
                    style = TextStyle(
                        color = colorResource(id = R.color.white),
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Left
                    ),
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f) // Adjust weight as needed
                )

                // Right-aligned text
                Text(
                    text = endText,
                    style = TextStyle(
                        color = colorResource(id = R.color.white),
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Right
                    ),
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f) // Adjust weight as needed
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text(
                    text = "Datum $nowString",
                    style = TextStyle(
                        color = colorResource(id = R.color.white),
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Right
                    ),
                    fontSize = 14.sp
                )
            }
        }
    }
}