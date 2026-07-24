package com.oduit.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Computer
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.oduit.app.ui.screens.transactions.TransactionWithCategory
import com.oduit.app.ui.theme.ExpenseRed
import com.oduit.app.ui.theme.IncomeGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionItem(
    transactionWithCategory: TransactionWithCategory,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val txn = transactionWithCategory.transaction
    val categoryLabel = transactionWithCategory.categoryName.ifEmpty {
        if (txn.type == "income") "Pemasukan" else "Pengeluaran"
    }
    val dateFormat = SimpleDateFormat("d MMM", Locale("id", "ID"))
    val amountColor = if (txn.type == "income") IncomeGreen else ExpenseRed
    val prefix = if (txn.type == "income") "+" else "-"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Category icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(transactionWithCategory.categoryColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = getCategoryIcon(categoryLabel),
                    contentDescription = categoryLabel,
                    modifier = Modifier.size(22.dp),
                    tint = transactionWithCategory.categoryColor,
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = categoryLabel,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row {
                    if (transactionWithCategory.accountName.isNotEmpty()) {
                        Text(
                            text = transactionWithCategory.accountName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = " · ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text(
                        text = dateFormat.format(Date(txn.date)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (txn.note.isNotEmpty()) {
                        Text(
                            text = " · ${txn.note}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Text(
                text = "$prefix ${txn.amount.toRupiah()}",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = amountColor,
            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
        )
    }
}

internal fun getCategoryIcon(categoryName: String): ImageVector {
    return when (categoryName) {
        "Belanja" -> Icons.Rounded.ShoppingCart
        "Hiburan" -> Icons.Rounded.SportsEsports
        "Kesehatan" -> Icons.Rounded.MedicalServices
        "Makan & Minum" -> Icons.Rounded.Fastfood
        "Pendidikan" -> Icons.Rounded.School
        "Tagihan" -> Icons.Rounded.Receipt
        "Transportasi" -> Icons.Rounded.DirectionsCar
        "Transfer" -> Icons.Rounded.SwapHoriz
        "Gaji" -> Icons.Rounded.AccountBalanceWallet
        "Freelance" -> Icons.Rounded.Computer
        "Investasi" -> Icons.AutoMirrored.Rounded.TrendingUp
        "Bonus" -> Icons.Rounded.CardGiftcard
        "Lain-lain (Masuk)", "Lain-lain (Keluar)" -> Icons.Rounded.MoreHoriz
        else -> Icons.Rounded.Category
    }
}
