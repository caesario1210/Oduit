package com.oduit.app.ui.screens.savings

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.oduit.app.data.repository.SavingsRepository
import com.oduit.app.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingsScreen() {
    val repository = SavingsRepository(
        savingsGoalDao = OduitApp.instance.database.savingsGoalDao(),
    )
    val viewModel = viewModel<SavingsViewModel>(
        factory = SavingsViewModel.Factory(repository),
    )
    val state by viewModel.uiState.collectAsState()
    val showForm by viewModel.showForm.collectAsState()
    val editingGoal by viewModel.editingGoal.collectAsState()
    val contributionGoal by viewModel.showContribution.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = "Tabungan",
                    style = MaterialTheme.typography.headlineSmall,
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground,
            ),
            actions = {
                IconButton(onClick = { viewModel.showAddForm() }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Buat target baru",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            },
        )

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.goals.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.Savings,
                title = "Belum ada target tabungan",
                description = "Buat target tabungan untuk mulai menabung",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                }
                items(state.goals, key = { it.goal.id }) { goal ->
                    SavingsGoalCard(
                        goal = goal,
                        onClick = { viewModel.showEditForm(goal.goal) },
                        onAddContribution = { viewModel.showContributionFor(goal.goal) },
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // Add button - FAB position at bottom of list
    // For now, use top-app bar action or just rely on tap existing card → edit

    // Form sheet
    if (showForm) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.hideForm() },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        ) {
            SavingsFormSheet(
                editingGoal = editingGoal,
                onSave = { id, name, amount, date ->
                    viewModel.saveGoal(id, name, amount, date)
                },
                onDelete = editingGoal?.let { { viewModel.deleteGoal(it) } },
                onDismiss = { viewModel.hideForm() },
            )
        }
    }

    // Contribution dialog
    contributionGoal?.let { goal ->
        ContributionDialog(
            goal = goal,
            onAdd = { amount ->
                viewModel.addContribution(goal.id, amount)
            },
            onDismiss = { viewModel.hideContribution() },
        )
    }
}
