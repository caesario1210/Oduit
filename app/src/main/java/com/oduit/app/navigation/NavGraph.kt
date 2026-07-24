package com.oduit.app.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.oduit.app.OduitApp
import com.oduit.app.data.local.entity.Account
import com.oduit.app.data.local.entity.Category
import com.oduit.app.data.local.entity.Transaction
import com.oduit.app.data.repository.TransactionRepository
import com.oduit.app.ui.screens.budget.BudgetScreen
import com.oduit.app.ui.screens.home.HomeScreen
import com.oduit.app.ui.screens.reports.ReportsScreen
import com.oduit.app.ui.screens.savings.SavingsScreen
import com.oduit.app.ui.screens.settings.SettingsScreen
import com.oduit.app.ui.screens.transactions.TransactionFormSheet
import com.oduit.app.ui.screens.transactions.TransactionScreen
import com.oduit.app.ui.screens.transactions.TransferFormSheet
import com.oduit.app.ui.screens.transactions.TransferViewModel
import com.oduit.app.util.getWittyMessage
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class BottomNavItem(
    val screen: Screen,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem(Screen.Transactions, Icons.Filled.SwapHoriz, Icons.Outlined.SwapHoriz),
    BottomNavItem(Screen.Budget, Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet),
    BottomNavItem(Screen.Savings, Icons.Filled.Savings, Icons.Outlined.Savings),
    BottomNavItem(Screen.Reports, Icons.Filled.BarChart, Icons.Outlined.BarChart),
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun OduitNavGraph() {
    val pagerState = rememberPagerState(pageCount = { bottomNavItems.size })
    val scope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }
    var showTransactionForm by remember { mutableStateOf(false) }
    var showTransferForm by remember { mutableStateOf(false) }
    var showFabChoice by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    val txnRepo = remember {
        TransactionRepository(
            transactionDao = OduitApp.instance.database.transactionDao(),
            categoryDao = OduitApp.instance.database.categoryDao(),
            accountDao = OduitApp.instance.database.accountDao(),
        )
    }
    val transferViewModel = remember(txnRepo) {
        TransferViewModel(txnRepo)
    }

    if (showSettings) {
        SettingsScreen(onNavigateBack = { showSettings = false })
        return
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp,
            ) {
                bottomNavItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch { pagerState.animateScrollToPage(index) }
                        },
                        icon = {
                            Icon(
                                imageVector = if (pagerState.currentPage == index) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.screen.label,
                            )
                        },
                        label = {
                            Text(
                                text = item.screen.label,
                                style = MaterialTheme.typography.labelSmall,
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        ),
                    )
                }
            }
        },
        floatingActionButton = {
            if (pagerState.currentPage != 0 || !showSettings) {
                FloatingActionButton(
                    onClick = { showFabChoice = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Tambah",
                    )
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarState) { data ->
                Snackbar(
                    snackbarData = data,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                )
            }
        },
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(paddingValues),
        ) { page ->
            when (page) {
                0 -> HomeScreen(
                    onNavigateToSettings = { showSettings = true },
                    onSeeAllClick = {
                        scope.launch { pagerState.animateScrollToPage(1) }
                    },
                )
                1 -> TransactionScreen()
                2 -> BudgetScreen()
                3 -> SavingsScreen()
                4 -> ReportsScreen()
            }
        }
    }

    // Transaction Form Bottom Sheet
    if (showTransactionForm) {
        var categories by remember { mutableStateOf(emptyList<Category>()) }
        var accounts by remember { mutableStateOf(emptyList<Account>()) }

        LaunchedEffect(Unit) {
            categories = txnRepo.getAllCategories().firstOrNull() ?: emptyList()
            accounts = OduitApp.instance.database.accountDao().getAllAccounts().firstOrNull() ?: emptyList()
        }

        Dialog(
            onDismissRequest = { showTransactionForm = false },
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            androidx.compose.material3.Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                TransactionFormSheet(
                    editingTransaction = null,
                    categories = categories,
                    accounts = accounts,
                    onSave = { amount, type, accountId, categoryId, date, note ->
                        scope.launch {
                            try {
                                txnRepo.insert(
                                    Transaction(
                                        accountId = accountId,
                                        amount = amount,
                                        type = type,
                                        categoryId = categoryId,
                                        date = date,
                                        note = note,
                                    ),
                                )
                                showTransactionForm = false
                                val typeLabel = if (type == "income") "pemasukan" else "pengeluaran"
                                val catName = categories.find { it.id == categoryId }?.name ?: ""
                                snackbarState.showSnackbar(getWittyMessage(catName, typeLabel))
                            } catch (e: Exception) {
                                snackbarState.showSnackbar("Gagal simpan: ${e.message}")
                            }
                        }
                    },
                    onDismiss = {
                        showTransactionForm = false
                    },
                )
            }
        }
    }

    // FAB Choice Dialog
    if (showFabChoice) {
        AlertDialog(
            onDismissRequest = { showFabChoice = false },
            title = { Text("Pilih Tindakan", color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Column {
                    OutlinedButton(
                        onClick = { showFabChoice = false; showTransactionForm = true },
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text("+ Tambah Transaksi") }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { showFabChoice = false; showTransferForm = true },
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text("↔ Transfer antar Dompet") }
                }
            },
            confirmButton = {},
        )
    }

    // Observe transfer result — MUST be outside if block to survive dialog close
    LaunchedEffect(Unit) {
        transferViewModel.transferSuccessEvent.collectLatest { message ->
            snackbarState.showSnackbar(message)
        }
    }

    // Transfer Form Sheet
    if (showTransferForm) {
        var accounts by remember { mutableStateOf(emptyList<Account>()) }
        LaunchedEffect(Unit) {
            accounts = OduitApp.instance.database.accountDao().getAllAccounts().firstOrNull() ?: emptyList()
        }

        Dialog(
            onDismissRequest = { showTransferForm = false },
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                TransferFormSheet(
                accounts = accounts,
                onSave = { fromId, toId, amount, date, note ->
                    transferViewModel.saveTransfer(fromId, toId, amount, date, note)
                },
                onDismiss = { showTransferForm = false },
            )
        }
    }
}

} // close OduitNavGraph
