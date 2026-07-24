package com.oduit.app.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oduit.app.ui.components.toRupiah
import com.oduit.app.ui.theme.ExpenseRed
import com.oduit.app.ui.theme.IncomeGreen
import com.oduit.app.ui.theme.WarningOrange

@Composable
fun IncomeExpenseSummary(
    monthlyIncome: Double,
    monthlyExpense: Double,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                text = "Ringkasan Bulan Ini",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                SummaryItem(
                    label = "Pemasukan",
                    amount = monthlyIncome,
                    color = IncomeGreen,
                )
                SummaryItem(
                    label = "Pengeluaran",
                    amount = monthlyExpense,
                    color = ExpenseRed,
                )
                val remaining = monthlyIncome - monthlyExpense
                val remainingColor = when {
                    remaining >= 0 -> IncomeGreen
                    remaining < 0 -> ExpenseRed
                    else -> WarningOrange
                }
                SummaryItem(
                    label = "Sisa",
                    amount = remaining,
                    color = remainingColor,
                )
            }
        }
    }
}

@Composable
private fun SummaryItem(
    label: String,
    amount: Double,
    color: androidx.compose.ui.graphics.Color,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = amount.toRupiah(),
            style = MaterialTheme.typography.titleSmall,
            color = color,
        )
    }
}
