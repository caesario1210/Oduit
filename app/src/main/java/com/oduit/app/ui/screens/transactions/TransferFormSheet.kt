package com.oduit.app.ui.screens.transactions

import androidx.compose.foundation.layout.Column
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
import com.oduit.app.ui.components.CustomNumpad
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferFormSheet(
    accounts: List<Account>,
    onSave: (fromAccountId: Long, toAccountId: Long, amount: Double, date: Long, note: String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cal = Calendar.getInstance()
    val accountOptions = accounts.filter { it.id != 0L }

    var fromAccountId by remember {
        mutableStateOf(accountOptions.firstOrNull()?.id ?: 1L)
    }
    var toAccountId by remember {
        mutableStateOf(accountOptions.getOrNull(1)?.id ?: accountOptions.firstOrNull()?.id ?: 1L)
    }
    var amountText by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(cal.timeInMillis) }
    var note by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = "Transfer",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Pindahkan uang antar dompet",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // From account
        Text("Dari", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(4.dp))
        Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
            accountOptions.forEach { acc ->
                val sel = acc.id == fromAccountId
                OutlinedButton(
                    onClick = { fromAccountId = acc.id },
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    colors = if (sel) ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    ) else ButtonDefaults.outlinedButtonColors(),
                ) { Text(acc.name, color = if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface) }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // To account
        Text("Ke", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(4.dp))
        Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
            accountOptions.forEach { acc ->
                val sel = acc.id == toAccountId
                OutlinedButton(
                    onClick = { toAccountId = acc.id },
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    colors = if (sel) ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    ) else ButtonDefaults.outlinedButtonColors(),
                ) { Text(acc.name, color = if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface) }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Amount
        val displayAmount = if (amountText.isEmpty()) "" else {
            java.text.NumberFormat.getNumberInstance(java.util.Locale("id", "ID")).format(amountText.toLong())
        }
        OutlinedTextField(
            value = displayAmount,
            onValueChange = {},
            readOnly = true,
            label = { Text("Jumlah Transfer") },
            prefix = { Text("Rp ", fontWeight = FontWeight.Bold) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomNumpad(
            value = amountText,
            onValueChange = { amountText = it },
        )

        Spacer(modifier = Modifier.height(12.dp))

        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
        OutlinedButton(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Tanggal: ${dateFormat.format(Date(selectedDate))}") }

        if (showDatePicker) {
            val state = rememberDatePickerState(initialSelectedDateMillis = selectedDate)
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = { TextButton(onClick = { state.selectedDateMillis?.let { selectedDate = it }; showDatePicker = false }) { Text("Pilih") } },
                dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Batal") } },
            ) { DatePicker(state = state) }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Catatan (opsional)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Batal") }
            Spacer(Modifier.width(12.dp))
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: return@Button
                    if (fromAccountId == toAccountId) return@Button
                    onSave(fromAccountId, toAccountId, amount, selectedDate, note)
                    onDismiss()
                },
                modifier = Modifier.weight(1f),
                enabled = amountText.toDoubleOrNull() != null
                    && amountText.toDoubleOrNull()!! > 0
                    && fromAccountId != toAccountId,
            ) { Text("Transfer") }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
