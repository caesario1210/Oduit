package com.oduit.app.ui.screens.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.oduit.app.data.local.entity.Account
import com.oduit.app.data.local.entity.Category
import com.oduit.app.ui.components.CategoryPicker
import com.oduit.app.ui.components.CustomNumpad
import com.oduit.app.ui.theme.ExpenseRed
import com.oduit.app.ui.theme.IncomeGreen
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFormSheet(
    editingTransaction: com.oduit.app.data.local.entity.Transaction?,
    categories: List<Category>,
    accounts: List<Account> = emptyList(),
    onSave: (amount: Double, type: String, accountId: Long, categoryId: Long?, date: Long, note: String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isEditing = editingTransaction != null
    val cal = Calendar.getInstance()

    var type by remember {
        mutableStateOf(editingTransaction?.type ?: "expense")
    }
    var selectedAccountId by remember {
        mutableStateOf(editingTransaction?.accountId ?: (accounts.firstOrNull()?.id ?: 1L))
    }
    var amountText by remember {
        mutableStateOf(editingTransaction?.let { if (it.amount == 0.0) "" else it.amount.toLong().toString() } ?: "")
    }
    var selectedCategoryId by remember {
        mutableStateOf(editingTransaction?.categoryId)
    }
    var selectedDate by remember {
        mutableStateOf(editingTransaction?.date ?: cal.timeInMillis)
    }
    var note by remember {
        mutableStateOf(editingTransaction?.note ?: "")
    }
    var showDatePicker by remember {
        mutableStateOf(false)
    }

    val filteredCategories = remember(type, categories) {
        categories.filter { it.type == type }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = if (isEditing) "Edit Transaksi" else "Tambah Transaksi",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Type selector
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = type == "expense", onClick = { type = "expense" },
                shape = RoundedCornerShape(8.dp),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = ExpenseRed.copy(alpha = 0.15f),
                    activeContentColor = ExpenseRed,
                ),
            ) { Text("Pengeluaran") }
            SegmentedButton(
                selected = type == "income", onClick = { type = "income" },
                shape = RoundedCornerShape(8.dp),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = IncomeGreen.copy(alpha = 0.15f),
                    activeContentColor = IncomeGreen,
                ),
            ) { Text("Pemasukan") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Account selector
        if (accounts.isNotEmpty()) {
            Text("Dompet", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                accounts.forEach { acc ->
                    val isAccountSelected = acc.id == selectedAccountId
                    OutlinedButton(
                        onClick = { selectedAccountId = acc.id },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        colors = if (isAccountSelected) ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        ) else ButtonDefaults.outlinedButtonColors(),
                    ) {
                        Text(acc.name, style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Amount input
        val displayAmount = if (amountText.isEmpty()) "" else {
            java.text.NumberFormat.getNumberInstance(java.util.Locale("id", "ID")).format(amountText.toLong())
        }
        OutlinedTextField(
            value = displayAmount,
            onValueChange = {},
            readOnly = true,
            label = { Text("Nominal") },
            prefix = { Text("Rp ", fontWeight = FontWeight.Bold) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomNumpad(
            value = amountText,
            onValueChange = { amountText = it },
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Category picker
        CategoryPicker(
            categories = filteredCategories,
            selectedCategoryId = selectedCategoryId,
            onCategorySelected = { selectedCategoryId = it.id },
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Date picker trigger
        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
        OutlinedButton(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Tanggal: ${dateFormat.format(Date(selectedDate))}")
        }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { selectedDate = it }
                        showDatePicker = false
                    }) { Text("Pilih") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Batal") }
                },
            ) { DatePicker(state = datePickerState) }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Note
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Catatan (opsional)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Buttons
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Batal") }
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: return@Button
                    onSave(amount, type, selectedAccountId, selectedCategoryId, selectedDate, note)
                },
                modifier = Modifier.weight(1f),
                enabled = amountText.toDoubleOrNull() != null && amountText.toDoubleOrNull()!! > 0,
            ) { Text(if (isEditing) "Simpan" else "Tambah") }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
