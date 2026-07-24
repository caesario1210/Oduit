package com.oduit.app.ui.screens.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oduit.app.OduitApp
import com.oduit.app.data.repository.TransactionRepository
import com.oduit.app.ui.components.EmptyState
import com.oduit.app.ui.components.TransactionItem
import com.oduit.app.ui.theme.ExpenseRed
import com.oduit.app.ui.theme.IncomeGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen() {
    val repository = TransactionRepository(
        transactionDao = OduitApp.instance.database.transactionDao(),
        categoryDao = OduitApp.instance.database.categoryDao(),
        accountDao = OduitApp.instance.database.accountDao(),
    )
    val viewModel = viewModel<TransactionViewModel>(
        factory = TransactionViewModel.Factory(repository),
    )
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = "Transaksi",
                    style = MaterialTheme.typography.headlineSmall,
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground,
            ),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = state.selectedFilter == "all",
                onClick = { viewModel.setFilter("all") },
                label = { Text("Semua") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                ),
            )
            FilterChip(
                selected = state.selectedFilter == "expense",
                onClick = { viewModel.setFilter("expense") },
                label = { Text("Pengeluaran") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ExpenseRed.copy(alpha = 0.15f),
                ),
            )
            FilterChip(
                selected = state.selectedFilter == "income",
                onClick = { viewModel.setFilter("income") },
                label = { Text("Pemasukan") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = IncomeGreen.copy(alpha = 0.15f),
                ),
            )
            FilterChip(
                selected = state.selectedFilter == "transfer",
                onClick = { viewModel.setFilter("transfer") },
                label = { Text("Transfer") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                ),
            )
        }

        if (state.isLoading) {
            EmptyState(
                icon = Icons.Outlined.Receipt,
                title = "Memuat...",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            )
        } else if (state.groupedTransactions.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.Receipt,
                title = "Belum ada transaksi",
                description = "Tap + untuk mencatat transaksi pertama",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                state.groupedTransactions.entries.forEach { (date, txns) ->
                    item {
                        Text(
                            text = date,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(
                                start = 16.dp, top = 16.dp, bottom = 4.dp, end = 16.dp,
                            ),
                        )
                    }
                    items(txns, key = { it.transaction.id }) { txn ->
                        TransactionItem(
                            transactionWithCategory = txn,
                            onClick = { viewModel.showEditForm(txn.transaction) },
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
