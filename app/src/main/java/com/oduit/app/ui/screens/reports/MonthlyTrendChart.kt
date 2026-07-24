package com.oduit.app.ui.screens.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.oduit.app.ui.components.toRupiah
import com.oduit.app.ui.theme.ExpenseRed
import com.oduit.app.ui.theme.IncomeGreen

@Composable
fun MonthlyTrendChart(
    data: List<MonthlyData>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Tren Bulanan",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (data.isEmpty()) {
            Text(
                text = "Belum ada data",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 24.dp),
            )
            return
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            LegendItem(color = IncomeGreen, label = "Pemasukan")
            LegendItem(color = ExpenseRed, label = "Pengeluaran")
        }

        val maxValue = data.maxOf { maxOf(it.income, it.expense) }
        val yLabels = buildYLabels(maxValue)
        var tappedIndex by remember { mutableStateOf(-1) }

        Row(modifier = Modifier.fillMaxWidth()) {
            // Y-axis labels
            Column(
                modifier = Modifier.width(48.dp).height(170.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                yLabels.forEach { label ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // Bars + X-axis
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    data.forEachIndexed { index, month ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f),
                        ) {
                            val incomeH = if (maxValue > 0)
                                (month.income / maxValue * 110).dp else 0.dp
                            val expenseH = if (maxValue > 0)
                                (month.expense / maxValue * 110).dp else 0.dp

                            if (incomeH > 0.dp) {
                                Box(
                                    modifier = Modifier
                                        .width(20.dp)
                                        .height(incomeH)
                                        .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                        .background(IncomeGreen.copy(alpha = 0.8f))
                                        .clickable { tappedIndex = if (tappedIndex == index) -1 else index },
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            if (expenseH > 0.dp) {
                                Box(
                                    modifier = Modifier
                                        .width(20.dp)
                                        .height(expenseH)
                                        .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                        .background(ExpenseRed.copy(alpha = 0.8f))
                                        .clickable { tappedIndex = if (tappedIndex == index) -1 else index },
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = month.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                // Tooltip
                if (tappedIndex in data.indices) {
                    val month = data[tappedIndex]
                    Card(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = month.label,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = "Pemasukan: ${month.income.toRupiah()}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = IncomeGreen,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Text(
                                    text = "Pengeluaran: ${month.expense.toRupiah()}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ExpenseRed,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            val diff = month.income - month.expense
                            val diffColor = if (diff >= 0) IncomeGreen else ExpenseRed
                            Text(
                                text = "Selisih: ${diff.toRupiah()}",
                                style = MaterialTheme.typography.labelSmall,
                                color = diffColor,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color),
        )
        Spacer(modifier = Modifier.padding(start = 4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun buildYLabels(maxValue: Double): List<String> {
    if (maxValue <= 0) return listOf("0", "", "", "")
    val step = when {
        maxValue > 10_000_000 -> 5_000_000
        maxValue > 5_000_000 -> 2_000_000
        maxValue > 1_000_000 -> 500_000
        maxValue > 500_000 -> 200_000
        maxValue > 100_000 -> 50_000
        else -> 10_000
    }
    val labels = mutableListOf<String>()
    var cur = 0.0
    while (cur <= maxValue) {
        labels.add(abbreviateRupiah(cur))
        cur += step
    }
    return labels
}

private fun abbreviateRupiah(value: Double): String {
    return when {
        value >= 1_000_000 -> "${(value / 1_000_000).toLong()}jt"
        value >= 1_000 -> "${(value / 1_000).toLong()}rb"
        value == 0.0 -> "0"
        else -> value.toLong().toString()
    }
}
