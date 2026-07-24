package com.oduit.app.ui.screens.budget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oduit.app.OduitApp
import com.oduit.app.data.repository.BudgetRepository
import com.oduit.app.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen() {
    val repository = BudgetRepository(
        budgetDao = OduitApp.instance.database.budgetDao(),
        transactionDao = OduitApp.instance.database.transactionDao(),
        categoryDao = OduitApp.instance.database.categoryDao(),
    )
    val viewModel = viewModel<BudgetViewModel>(
        factory = BudgetViewModel.Factory(repository),
    )
    val state by viewModel.uiState.collectAsState()
    val formBudget by viewModel.showForm.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = "Budget",
                    style = MaterialTheme.typography.headlineSmall,
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground,
            ),
        )

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.budgets.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.AccountBalanceWallet,
                title = "Belum ada budget",
                description = "Atur budget bulanan untuk setiap kategori pengeluaran",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                }
                items(state.budgets, key = { it.categoryId }) { budget ->
                    BudgetProgressCard(
                        budget = budget,
                        onClick = { viewModel.showFormFor(budget) },
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    // Budget form sheet
    formBudget?.let { budget ->
        ModalBottomSheet(
            onDismissRequest = { viewModel.hideForm() },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        ) {
            BudgetFormSheet(
                budget = budget,
                onSave = { categoryId, limit ->
                    viewModel.saveBudget(categoryId, limit)
                },
                onDelete = if (budget.budget != null) {
                    { budget.budget?.let { viewModel.deleteBudget(it) } }
                } else null,
                onDismiss = { viewModel.hideForm() },
            )
        }
    }
}
