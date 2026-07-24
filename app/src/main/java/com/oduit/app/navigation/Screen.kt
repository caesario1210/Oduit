package com.oduit.app.navigation

sealed class Screen(val route: String, val label: String, val icon: String) {
    data object Home : Screen("home", "Beranda", "home")
    data object Transactions : Screen("transactions", "Transaksi", "swap_horiz")
    data object Budget : Screen("budget", "Budget", "account_balance_wallet")
    data object Savings : Screen("savings", "Tabungan", "savings")
    data object Reports : Screen("reports", "Laporan", "bar_chart")
    data object Settings : Screen("settings", "Pengaturan", "settings")
}
