package com.oduit.app.ui.screens.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.oduit.app.data.local.entity.Transaction
import com.oduit.app.ui.components.EmptyState
import com.oduit.app.ui.components.TransactionItem
import com.oduit.app.ui.screens.transactions.TransactionWithCategory

@Composable
fun RecentTransactions(
    transactions: List<Transaction>,
    onSeeAllClick: () -> Unit = {},
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
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "Transaksi Terbaru",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
            )

            if (transactions.isEmpty()) {
                EmptyState(
                    title = "Belum ada transaksi",
                    description = "Tap + untuk mencatat transaksi pertama",
                )
            } else {
                transactions.forEach { txn ->
                    TransactionItem(
                        transactionWithCategory = TransactionWithCategory(
                            transaction = txn,
                        ),
                    )
                }
            }

            if (transactions.isNotEmpty()) {
                Text(
                    text = "Lihat Semua",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable { onSeeAllClick() }
                        .padding(16.dp),
                )
            }
        }
    }
}
