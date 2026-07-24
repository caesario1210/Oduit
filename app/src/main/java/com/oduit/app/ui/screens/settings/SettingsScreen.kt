package com.oduit.app.ui.screens.settings

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oduit.app.OduitApp
import com.oduit.app.data.local.entity.Account
import com.oduit.app.data.local.entity.Category
import com.oduit.app.ui.components.toRupiah
import com.oduit.app.ui.theme.ExpenseRed
import com.oduit.app.ui.theme.IncomeGreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit = {},
) {
    val viewModel = viewModel<SettingsViewModel>(
        factory = SettingsViewModel.Factory(
            categoryDao = OduitApp.instance.database.categoryDao(),
            accountDao = OduitApp.instance.database.accountDao(),
            transactionDao = OduitApp.instance.database.transactionDao(),
            budgetDao = OduitApp.instance.database.budgetDao(),
            savingsGoalDao = OduitApp.instance.database.savingsGoalDao(),
        ),
    )
    val state by viewModel.uiState.collectAsState()
    val showCategoryForm by viewModel.showCategoryForm.collectAsState()
    val editingCategory by viewModel.editingCategory.collectAsState()
    val showAccountForm by viewModel.showAccountForm.collectAsState()
    val editingAccount by viewModel.editingAccount.collectAsState()

    val context = LocalContext.current
    var showDeleteConfirm by remember { mutableStateOf<Category?>(null) }
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
    ) { uri ->
        uri?.let {
            viewModel.exportToUri(context, it)
            Toast.makeText(context, "Data berhasil diexport", Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Pengaturan", style = MaterialTheme.typography.headlineSmall) },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Filled.ArrowBack, "Kembali")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            // Section: Kategori
            item {
                SectionHeader("Kategori", Icons.Outlined.Category)
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                ) {
                    Column {
                        state.categories.forEachIndexed { i, cat ->
                            SettingsItem(
                                title = cat.name,
                                subtitle = if (cat.type == "income") "Pemasukan" else "Pengeluaran",
                                trailing = {
                                    Row {
                                        IconButton(onClick = { viewModel.showEditCategory(cat) }) {
                                            Icon(Icons.Outlined.Edit, "Edit", tint = MaterialTheme.colorScheme.primary)
                                        }
                                        if (!cat.isDefault) {
                                            IconButton(onClick = { showDeleteConfirm = cat }) {
                                                Icon(Icons.Outlined.Delete, "Hapus", tint = ExpenseRed)
                                            }
                                        }
                                    }
                                },
                            )
                            if (i < state.categories.lastIndex) {
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                            }
                        }
                    }
                }
            }

            item {
                OutlinedButton(
                    onClick = { viewModel.showAddCategory() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                ) {
                    Icon(Icons.Filled.Add, null, modifier = Modifier.padding(end = 4.dp))
                    Text("Tambah Kategori")
                }
            }

            // Section: Akun
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader("Akun / Dompet", Icons.Outlined.AccountBalanceWallet)
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                ) {
                    Column {
                        state.accounts.forEachIndexed { i, acc ->
                            SettingsItem(
                                title = acc.name,
                                subtitle = "Saldo awal: ${acc.initialBalance.toRupiah()}",
                                trailing = {
                                    Row {
                                        IconButton(onClick = { viewModel.showEditAccount(acc) }) {
                                            Icon(Icons.Outlined.Edit, "Edit", tint = MaterialTheme.colorScheme.primary)
                                        }
                                        IconButton(onClick = { viewModel.deleteAccount(acc) }) {
                                            Icon(Icons.Outlined.Delete, "Hapus", tint = ExpenseRed)
                                        }
                                    }
                                },
                            )
                            if (i < state.accounts.lastIndex) {
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                            }
                        }
                    }
                }
            }

            item {
                OutlinedButton(
                    onClick = { viewModel.showAddAccount() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                ) {
                    Icon(Icons.Filled.Add, null, modifier = Modifier.padding(end = 4.dp))
                    Text("Tambah Akun")
                }
            }

            // Section: Export/Import
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader("Data", Icons.Outlined.FileDownload)
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                ) {
                    Column {
                        SettingsItem(
                            title = "Export JSON",
                            subtitle = "Backup semua data ke file JSON",
                            onClick = { exportLauncher.launch("oduit_backup.json") },
                        )
                    }
                }
            }

            // About
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader("Tentang", Icons.Outlined.Info)
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                ) {
                    SettingsItem(
                        title = "O'Duit v1.0.0",
                        subtitle = "Aplikasi Manajemen Keuangan Pribadi",
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    // Category form sheet
    if (showCategoryForm) {
        CategoryFormSheet(
            editingCategory = editingCategory,
            onSave = { id, name, type, icon, color ->
                viewModel.saveCategory(id, name, type, icon, color)
            },
            onDismiss = { viewModel.hideCategoryForm() },
        )
    }

    // Account form sheet
    if (showAccountForm) {
        AccountFormSheet(
            editingAccount = editingAccount,
            onSave = { id, name, balance, icon ->
                viewModel.saveAccount(id, name, balance, icon)
            },
            onDismiss = { viewModel.hideAccountForm() },
        )
    }

    // Delete confirmation
    showDeleteConfirm?.let { cat ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = null },
            title = { Text("Hapus Kategori") },
            text = { Text("Hapus kategori \"${cat.name}\"? Transaksi dengan kategori ini akan kehilangan referensi kategori.") },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteCategory(cat); showDeleteConfirm = null }) {
                    Text("Hapus", color = ExpenseRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = null }) { Text("Batal") }
            },
        )
    }
}

@Composable
private fun SectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp),
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(end = 8.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String = "",
    trailing: @Composable () -> Unit = {},
    onClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface)
            if (subtitle.isNotEmpty()) {
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        trailing()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryFormSheet(
    editingCategory: Category?,
    onSave: (id: Long?, name: String, type: String, icon: String, color: String) -> Unit,
    onDismiss: () -> Unit,
) {
    val isEditing = editingCategory != null
    var name by remember { mutableStateOf(editingCategory?.name ?: "") }
    var type by remember { mutableStateOf(editingCategory?.type ?: "expense") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Text(if (isEditing) "Edit Kategori" else "Kategori Baru",
                style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            androidx.compose.material3.SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(selected = type == "expense", onClick = { type = "expense" },
                    shape = RoundedCornerShape(8.dp),
                    colors = SegmentedButtonDefaults.colors(activeContainerColor = ExpenseRed.copy(alpha = 0.15f), activeContentColor = ExpenseRed)) { Text("Pengeluaran", color = MaterialTheme.colorScheme.onSurface) }
                SegmentedButton(selected = type == "income", onClick = { type = "income" },
                    shape = RoundedCornerShape(8.dp),
                    colors = SegmentedButtonDefaults.colors(activeContainerColor = IncomeGreen.copy(alpha = 0.15f), activeContentColor = IncomeGreen)) { Text("Pemasukan", color = MaterialTheme.colorScheme.onSurface) }
            }
            Spacer(Modifier.height(20.dp))
            Row(Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Batal") }
                Spacer(Modifier.width(12.dp))
                Button(onClick = { onSave(editingCategory?.id, name, type, "category", "#009688") },
                    modifier = Modifier.weight(1f), enabled = name.isNotBlank()) { Text("Simpan") }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountFormSheet(
    editingAccount: Account?,
    onSave: (id: Long?, name: String, initialBalance: Double, icon: String) -> Unit,
    onDismiss: () -> Unit,
) {
    val isEditing = editingAccount != null
    var name by remember { mutableStateOf(editingAccount?.name ?: "") }
    var balanceText by remember { mutableStateOf(editingAccount?.let { it.initialBalance.toLong().toString() } ?: "") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Text(if (isEditing) "Edit Akun" else "Akun Baru",
                style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Akun") }, placeholder = { Text("Contoh: Tunai, Bank BCA") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = balanceText, onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 12) balanceText = it },
                label = { Text("Saldo Awal") }, prefix = { Text("Rp ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(20.dp))
            Row(Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Batal") }
                Spacer(Modifier.width(12.dp))
                Button(onClick = { onSave(editingAccount?.id, name, balanceText.toDoubleOrNull() ?: 0.0, "wallet") },
                    modifier = Modifier.weight(1f), enabled = name.isNotBlank()) { Text("Simpan") }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
