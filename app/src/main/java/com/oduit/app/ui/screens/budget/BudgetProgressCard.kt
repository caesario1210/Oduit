package com.oduit.app.ui.screens.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.oduit.app.ui.components.toRupiah
import com.oduit.app.ui.theme.DangerRed
import com.oduit.app.ui.theme.IncomeGreen
import com.oduit.app.ui.theme.WarningOrange

@Composable
fun BudgetProgressCard(
    budget: BudgetWithSpent,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Category color dot
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(budget.categoryColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(budget.categoryColor),
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = budget.categoryName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                    )
                    if (budget.status == BudgetStatus.NOT_SET) {
                        Text(
                            text = "Belum ada budget",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    } else {
                        Row {
                            Text(
                                text = "${budget.spent.toRupiah()} / ${budget.limit.toRupiah()}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    if (budget.status == BudgetStatus.OVER) {
                        val overAmount = budget.spent - budget.limit
                        Text(
                            text = "Anda over budget Rp ${overAmount.toRupiah()}, kurangi pengeluaran lain.",
                            style = MaterialTheme.typography.labelSmall,
                            color = DangerRed,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }

                // Status badge
                val statusColor = when (budget.status) {
                    BudgetStatus.SAFE -> IncomeGreen
                    BudgetStatus.WARNING -> WarningOrange
                    BudgetStatus.OVER -> DangerRed
                    BudgetStatus.NOT_SET -> MaterialTheme.colorScheme.onSurfaceVariant
                }
                val statusText = when (budget.status) {
                    BudgetStatus.SAFE -> "Aman"
                    BudgetStatus.WARNING -> "Hati-hati"
                    BudgetStatus.OVER -> "Over!"
                    BudgetStatus.NOT_SET -> "Set"
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusColor.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            if (budget.status != BudgetStatus.NOT_SET) {
                Spacer(modifier = Modifier.height(12.dp))

                // Progress bar
                val barColor = when (budget.status) {
                    BudgetStatus.SAFE -> IncomeGreen
                    BudgetStatus.WARNING -> WarningOrange
                    BudgetStatus.OVER -> DangerRed
                    else -> MaterialTheme.colorScheme.outline
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(budget.progress.coerceIn(0f, 1f))
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(barColor),
                    )
                }
            }
        }
    }
}
