# Task Breakdown — O'Duit

**Total estimasi: 4-6 minggu (santai, 1 orang)**

---

## Epic 1: Setup Project & Foundation

| ID | Task | File/Component | Priority | Status |
|----|------|----------------|----------|--------|
| 1.1 | Setup project Android (Kotlin, Compose, Gradle) | `build.gradle.kts`, `settings.gradle.kts` | P0 | 🏗 |
| 1.2 | Struktur folder MVVM | Clean Architecture packages | P0 | ⬜ |
| 1.3 | Theme: Color, Typography, Shape | `ui/theme/*.kt` | P0 | ⬜ |
| 1.4 | Setup Room DB + entities | `data/local/entity/*.kt` | P0 | ⬜ |
| 1.5 | Setup DAOs | `data/local/dao/*.kt` | P0 | ⬜ |
| 1.6 | AppDatabase + TypeConverters | `data/local/AppDatabase.kt` | P0 | ⬜ |
| 1.7 | Seed data kategori default | `data/local/seed/` | P0 | ⬜ |
| 1.8 | Navigation: Bottom nav + NavHost | `navigation/NavGraph.kt` | P0 | ⬜ |
| 1.9 | MainActivity + entry point | `MainActivity.kt` | P0 | ⬜ |

---

## Epic 2: Dashboard/Home

| ID | Task | File/Component | Priority | Status |
|----|------|----------------|----------|--------|
| 2.1 | Saldo card (total all accounts) | `home/SaldoCard.kt` | P0 | ⬜ |
| 2.2 | Monthly income/expense ringkasan | `home/IncomeExpenseSummary.kt` | P0 | ⬜ |
| 2.3 | Mini bar chart (monthly trend) | `home/MiniChart.kt` | P0 | ⬜ |
| 2.4 | Recent transactions list (grouped) | `home/RecentTransactions.kt` | P0 | ⬜ |
| 2.5 | "Lihat Semua" link to transaction screen | `home/RecentTransactions.kt` | P1 | ⬜ |
| 2.6 | FAB tambah transaksi | `home/HomeScreen.kt` | P0 | ⬜ |
| 2.7 | HomeViewModel (load data aggregate) | `home/HomeViewModel.kt` | P0 | ⬜ |
| 2.8 | Empty state (no transactions yet) | `home/EmptyState.kt` | P1 | ⬜ |

---

## Epic 3: Catat Pemasukan & Pengeluaran

| ID | Task | File/Component | Priority | Status |
|----|------|----------------|----------|--------|
| 3.1 | Transaction list screen (all transactions) | `transaction/TransactionScreen.kt` | P0 | ⬜ |
| 3.2 | TransactionItem composable | `transaction/TransactionItem.kt` | P0 | ⬜ |
| 3.3 | Group by date header | `transaction/DateGroupHeader.kt` | P0 | ⬜ |
| 3.4 | Add/Edit transaction form (bottom sheet) | `transaction/TransactionFormSheet.kt` | P0 | ⬜ |
| 3.5 | Category picker dialog | `transaction/CategoryPicker.kt` | P0 | ⬜ |
| 3.6 | Date picker | `transaction/TransactionFormSheet.kt` | P0 | ⬜ |
| 3.7 | TransactionViewModel (CRUD) | `transaction/TransactionViewModel.kt` | P0 | ⬜ |
| 3.8 | TransactionRepository | `data/repository/TransactionRepository.kt` | P0 | ⬜ |
| 3.9 | Multi-account support (default 1) | `data/repository/AccountRepository.kt` | P1 | ⬜ |

---

## Epic 4: Budgeting per Kategori

| ID | Task | File/Component | Priority | Status |
|----|------|----------------|----------|--------|
| 4.1 | Budget list screen (grid per kategori) | `budget/BudgetScreen.kt` | P0 | ⬜ |
| 4.2 | BudgetProgressCard (progress bar + status) | `budget/BudgetProgressCard.kt` | P0 | ⬜ |
| 4.3 | Set/edit budget form | `budget/BudgetFormSheet.kt` | P0 | ⬜ |
| 4.4 | BudgetViewModel (load + calculate usage) | `budget/BudgetViewModel.kt` | P0 | ⬜ |
| 4.5 | BudgetRepository | `data/repository/BudgetRepository.kt` | P0 | ⬜ |
| 4.6 | Color indicator (green/yellow/red) | `budget/BudgetProgressCard.kt` | P0 | ⬜ |

---

## Epic 5: Tracking Tabungan

| ID | Task | File/Component | Priority | Status |
|----|------|----------------|----------|--------|
| 5.1 | Savings goal list screen | `savings/SavingsScreen.kt` | P0 | ⬜ |
| 5.2 | SavingsGoalCard (progress %, nama, nominal) | `savings/SavingsGoalCard.kt` | P0 | ⬜ |
| 5.3 | Add/edit goal form | `savings/SavingsFormSheet.kt` | P0 | ⬜ |
| 5.4 | Add contribution dialog | `savings/ContributionDialog.kt` | P0 | ⬜ |
| 5.5 | Detail goal screen (riwayat kontribusi) | `savings/SavingsDetailScreen.kt` | P1 | ⬜ |
| 5.6 | Estimasi target date | `savings/SavingsViewModel.kt` | P1 | ⬜ |
| 5.7 | SavingsViewModel | `savings/SavingsViewModel.kt` | P0 | ⬜ |

---

## Epic 6: Laporan & Grafik

| ID | Task | File/Component | Priority | Status |
|----|------|----------------|----------|--------|
| 6.1 | Report screen with filter period | `report/ReportScreen.kt` | P0 | ⬜ |
| 6.2 | Ringkasan card (total in/out/balance) | `report/SummaryCard.kt` | P0 | ⬜ |
| 6.3 | Pie chart pengeluaran per kategori | `report/CategoryPieChart.kt` | P0 | ⬜ |
| 6.4 | Line/bar chart tren bulanan | `report/MonthlyTrendChart.kt` | P0 | ⬜ |
| 6.5 | Filter chip (range tanggal, kategori) | `report/FilterChips.kt` | P0 | ⬜ |
| 6.6 | ReportViewModel | `report/ReportViewModel.kt` | P0 | ⬜ |
| 6.7 | Integrasi Vico library | `build.gradle.kts` | P0 | ⬜ |

---

## Epic 7: Pengaturan & Keamanan

| ID | Task | File/Component | Priority | Status |
|----|------|----------------|----------|--------|
| 7.1 | Settings screen | `settings/SettingsScreen.kt` | P0 | ⬜ |
| 7.2 | Category management (add/edit/delete) | `settings/CategoryManagement.kt` | P0 | ⬜ |
| 7.3 | Account/wallet management | `settings/AccountManagement.kt` | P1 | ⬜ |
| 7.4 | Export data (JSON/CSV) | `settings/ExportImport.kt` | P0 | ⬜ |
| 7.5 | Import data | `settings/ExportImport.kt` | P0 | ⬜ |
| 7.6 | App lock (PIN/biometric) | `settings/AppLock.kt` | P1 | ⬜ |

---

## Epic 8: Polish & Release

| ID | Task | File/Component | Priority | Status |
|----|------|----------------|----------|--------|
| 8.1 | App icon design | `res/mipmap/` | P0 | ⬜ |
| 8.2 | Splash screen | `SplashScreen.kt` | P0 | ⬜ |
| 8.3 | Error handling (try-catch, snackbar) | All screens | P1 | ⬜ |
| 8.4 | Loading state (shimmer) | All screens | P1 | ⬜ |
| 8.5 | Dark mode support | Theme | P1 | ⬜ |
| 8.6 | Testing manual e2e | — | P1 | ⬜ |
| 8.7 | Build release APK | — | P0 | ⬜ |

---

## Milestone Tracking

| Milestone | Tasks | Target | Status |
|-----------|-------|--------|--------|
| M1 — Foundation | Epic 1 | Day 1-2 | 🏗 |
| M2 — Dashboard | Epic 2 | Day 2-3 | ⬜ |
| M3 — Core Transaksi | Epic 3 | Day 4-7 | ⬜ |
| M4 — Budget & Tabungan | Epic 4 + 5 | Day 8-14 | ⬜ |
| M5 — Laporan | Epic 6 | Day 15-18 | ⬜ |
| M6 — Settings | Epic 7 | Day 19-22 | ⬜ |
| M7 — Polish | Epic 8 | Day 23-28 | ⬜ |

**Legend:** P0 = Must have, P1 = Nice to have, 🏗 = In Progress, ✅ = Done, ⬜ = Pending
