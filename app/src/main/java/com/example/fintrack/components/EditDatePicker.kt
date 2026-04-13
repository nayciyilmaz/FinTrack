package com.example.fintrack.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.fintrack.R
import com.example.fintrack.util.monthFormatter
import java.time.LocalDate
import java.time.YearMonth

@SuppressLint("LocalContextResourcesRead")
@Composable
fun EditDatePicker(
    currentMonth: YearMonth,
    tempSelectedDate: LocalDate?,
    onMonthChange: (YearMonth) -> Unit,
    onDateSelect: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    val weekDays = stringArrayResource(id = R.array.week_days_short)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = modifier.fillMaxWidth()
        ) {
            Column(
                modifier = modifier.padding(16.dp)
            ) {
                Box(
                    modifier = modifier
                        .fillMaxWidth()
                        .background(
                            color = colorResource(id = R.color.quick_action_background),
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        EditTextButton(
                            text = "<",
                            onClick = { onMonthChange(currentMonth.minusMonths(1)) }
                        )
                        Text(
                            text = currentMonth.format(monthFormatter),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black
                        )
                        EditTextButton(
                            text = ">",
                            onClick = { onMonthChange(currentMonth.plusMonths(1)) }
                        )
                    }
                }

                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    weekDays.forEach { day ->
                        Text(
                            text = day,
                            modifier = modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelMedium,
                            color = colorResource(id = R.color.bottom_bar_fab)
                        )
                    }
                }

                CalendarDaysGrid(
                    currentMonth = currentMonth,
                    tempSelectedDate = tempSelectedDate,
                    onDateSelect = onDateSelect
                )

                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    EditTextButton(
                        text = stringResource(id = R.string.label_cancel),
                        onClick = onDismiss
                    )
                    EditTextButton(
                        text = stringResource(id = R.string.label_confirm),
                        onClick = onConfirm
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarDaysGrid(
    currentMonth: YearMonth,
    tempSelectedDate: LocalDate?,
    onDateSelect: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Column {
        val firstDayOfMonth = currentMonth.atDay(1)
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value
        val daysInMonth = currentMonth.lengthOfMonth()
        var dayCounter = 1

        for (week in 0..5) {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (dayOfWeek in 1..7) {
                    val dayToShow = if (week == 0 && dayOfWeek < firstDayOfWeek) {
                        null
                    } else if (dayCounter > daysInMonth) {
                        null
                    } else {
                        dayCounter++
                    }

                    val isSelected = dayToShow != null &&
                            tempSelectedDate?.dayOfMonth == dayToShow &&
                            tempSelectedDate?.month == currentMonth.month

                    Box(
                        modifier = modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .background(
                                color = if (isSelected) colorResource(id = R.color.bottom_bar_fab) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable(enabled = dayToShow != null) {
                                dayToShow?.let { onDateSelect(currentMonth.atDay(it)) }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        dayToShow?.let {
                            Text(
                                text = it.toString(),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}