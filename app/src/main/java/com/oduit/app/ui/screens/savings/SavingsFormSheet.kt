package com.oduit.app.ui.screens.savings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.oduit.app.ui.theme.DangerRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingsFormSheet(
    editingGoal: com.oduit.app.data.local.entity.SavingsGoal?,
    onSave: (id: Long?, name: String, targetAmount: Double, targetDate: Long?) -> Unit,
    onDelete: (() -> Unit)? = null,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isEditing = editingGoal != null

    var name by remember {
        mutableStateOf(editingGoal?.name ?: "")
    }
    var targetText by remember {
        mutableStateOf(editingGoal?.let {
            if (it.targetAmount == 0.0) "" else it.targetAmount.toLong().toString()
        } ?: "")
    }
    var targetDate by remember {
        mutableStateOf(editingGoal?.targetDate)
    }
    var showDatePicker by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Text(
            text = if (isEditing) "Edit Target" else "Target Baru",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nama Target") },
            placeholder = { Text("Contoh: Liburan Bali") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = targetText,
            onValueChange = { text ->
                if (text.all { it.isDigit() } && text.length <= 12) {
                    targetText = text
                }
            },
            label = { Text("Nominal Target") },
            prefix = { Text("Rp ", fontWeight = FontWeight.Bold) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
        OutlinedButton(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = if (targetDate != null) "Target: ${dateFormat.format(Date(targetDate!!))}"
                else "Tambahkan target tanggal (opsional)",
            )
        }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = targetDate,
            )
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { targetDate = it }
                        showDatePicker = false
                    }) { Text("Pilih") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Batal") }
                },
            ) {
                DatePicker(state = datePickerState)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            if (isEditing && onDelete != null) {
                OutlinedButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                ) {
                    Text("Hapus")
                }
                Spacer(modifier = Modifier.width(12.dp))
            }
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
            ) {
                Text("Batal")
            }
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = {
                    val amount = targetText.toDoubleOrNull() ?: return@Button
                    onSave(editingGoal?.id, name, amount, targetDate)
                },
                modifier = Modifier.weight(1f),
                enabled = name.isNotBlank() &&
                    targetText.toDoubleOrNull() != null &&
                    targetText.toDoubleOrNull()!! > 0,
            ) {
                Text(if (isEditing) "Simpan" else "Buat")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
