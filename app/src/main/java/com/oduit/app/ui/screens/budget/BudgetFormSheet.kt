package com.oduit.app.ui.screens.budget

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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

@Composable
fun BudgetFormSheet(
    budget: BudgetWithSpent?,
    onSave: (categoryId: Long, limitAmount: Double) -> Unit,
    onDelete: (() -> Unit)? = null,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isEditing = budget?.budget != null
    val currentLimit = budget?.limit ?: 0.0

    var amountText by remember {
        mutableStateOf(if (currentLimit > 0) currentLimit.toLong().toString() else "")
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Text(
            text = "Budget ${budget?.categoryName ?: ""}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tetapkan limit budget bulanan untuk kategori ini",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = amountText,
            onValueChange = { text ->
                if (text.all { it.isDigit() } && text.length <= 12) {
                    amountText = text
                }
            },
            label = { Text("Limit Budget") },
            prefix = { Text("Rp ", fontWeight = FontWeight.Bold) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            if (isEditing && onDelete != null) {
                OutlinedButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = DangerRed,
                    ),
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
                    val amount = amountText.toDoubleOrNull() ?: return@Button
                    budget?.let { onSave(it.categoryId, amount) }
                },
                modifier = Modifier.weight(1f),
                enabled = amountText.toDoubleOrNull() != null && amountText.toDoubleOrNull()!! > 0 && budget != null,
            ) {
                Text(if (isEditing) "Simpan" else "Tambah")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
