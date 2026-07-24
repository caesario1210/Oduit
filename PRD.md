# PRD: O'Duit — Aplikasi Manajemen Keuangan Pribadi

**Nama produk:** O'Duit
**Tagline:** Duit kamu, kendali kamu.
**Platform:** Android (native)
**Target Pengguna:** Personal (single-user)
**Versi Dokumen:** 1.0
**Tanggal:** 15 Juli 2026

---

## 1. Latar Belakang & Tujuan

Saat ini pencatatan keuangan pribadi sering dilakukan manual (catatan/spreadsheet) yang mudah lupa diisi dan sulit dianalisis. Aplikasi ini dibuat untuk membantu pengguna:

- Mencatat pemasukan & pengeluaran dengan cepat
- Mengatur budget per kategori agar pengeluaran terkendali
- Melacak progres tabungan/target keuangan
- Melihat laporan & tren keuangan lewat grafik

**Goal utama v1:** Pengguna bisa mencatat transaksi harian dalam <10 detik, dan melihat ringkasan keuangan bulanan dengan jelas.

---

## 2. Ruang Lingkup

### In Scope (v1 / MVP)
- Catat pemasukan & pengeluaran
- Kategori transaksi (custom + default)
- Budgeting per kategori per bulan
- Target tabungan (savings goal)
- Laporan & grafik (pie chart kategori, line chart tren bulanan)
- Data tersimpan lokal di device (offline-first)

### Out of Scope (v1 — jadi backlog v2+)
- Multi-user / shared account
- Sinkronisasi cloud / backup otomatis ke akun
- Integrasi rekening bank / e-wallet otomatis
- Manajemen utang-piutang
- Reminder tagihan berulang
- Widget home screen

---

## 3. User Persona

**Nama:** Individu produktif, mengelola keuangan sendiri
**Kebutuhan:** Alat sederhana, cepat dipakai, tidak ribet input data, privasi data terjaga (lokal, tanpa akun wajib)
**Pain point saat ini:** Lupa mencatat, tidak tahu kemana uang habis, tidak ada gambaran progres nabung

---

## 4. Fitur & User Stories

### 4.1 Catat Pemasukan & Pengeluaran
- Sebagai pengguna, saya ingin menambah transaksi (jumlah, kategori, tanggal, catatan, jenis: masuk/keluar) dengan cepat dari tombol utama (FAB).
- Saya ingin memilih kategori dari daftar atau membuat kategori baru.
- Saya ingin mengedit/menghapus transaksi yang sudah dicatat.
- Saya ingin melihat daftar transaksi harian/mingguan/bulanan.
- Saya ingin memiliki lebih dari satu "akun/dompet" (misal: Tunai, Bank, E-wallet) — opsional, default 1 akun.

### 4.2 Budgeting per Kategori
- Sebagai pengguna, saya ingin menetapkan limit budget bulanan per kategori (misal: Makan Rp1.500.000).
- Saya ingin melihat progres pemakaian budget (progress bar: terpakai vs sisa).
- Saya ingin mendapat indikator visual (hijau/kuning/merah) saat mendekati/melewati limit.

### 4.3 Tracking Tabungan / Target Keuangan
- Sebagai pengguna, saya ingin membuat target tabungan (nama, nominal target, tanggal target, nominal saat ini).
- Saya ingin menambah dana ke target tabungan dan melihat progres (%).
- Saya ingin melihat estimasi apakah target tercapai tepat waktu berdasarkan rata-rata kontribusi.

### 4.4 Laporan & Grafik
- Sebagai pengguna, saya ingin melihat ringkasan bulanan: total masuk, total keluar, saldo.
- Saya ingin melihat pie chart pengeluaran per kategori.
- Saya ingin melihat line/bar chart tren pemasukan-pengeluaran per bulan (6-12 bulan terakhir).
- Saya ingin memfilter laporan berdasarkan rentang tanggal & kategori.

---

## 5. Non-Functional Requirements

| Aspek | Requirement |
|---|---|
| Offline-first | Semua fitur inti berfungsi tanpa internet, data disimpan lokal (Room DB) |
| Performa | Aplikasi ringan, load < 2 detik, input transaksi < 10 detik |
| Keamanan | Opsi kunci aplikasi (PIN/biometrik) karena data finansial sensitif |
| Backup | Export/import data ke file (JSON/CSV) untuk backup manual di v1 |
| Skalabilitas data | Mendukung minimal 5 tahun data transaksi tanpa lag |
| Aksesibilitas | Kontras warna cukup, ukuran font bisa disesuaikan sistem |

---

## 6. Data Model (Draft)

```
Account (Akun/Dompet)
- id, name, initial_balance, icon

Category (Kategori)
- id, name, type (income/expense), icon, color

Transaction (Transaksi)
- id, account_id, category_id, amount, type, date, note, created_at

Budget (Anggaran Bulanan)
- id, category_id, month, year, limit_amount

SavingsGoal (Target Tabungan)
- id, name, target_amount, current_amount, target_date, created_at

SavingsContribution (Kontribusi Tabungan)
- id, goal_id, amount, date
```

---

## 7. Tech Stack Rekomendasi

- **Bahasa:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Database lokal:** Room (SQLite)
- **Arsitektur:** MVVM + Clean Architecture (ringan, tidak perlu multi-module untuk personal app)
- **Chart:** Vico atau MPAndroidChart (Compose-friendly: Vico direkomendasikan)
- **Dependency Injection:** Hilt (opsional, bisa manual DI untuk app kecil)
- **Export/Import:** Storage Access Framework untuk file JSON/CSV

---

## 8. Struktur Navigasi (Screens)

1. **Dashboard/Home** — ringkasan saldo, grafik pengeluaran bulan ini, transaksi terbaru
2. **Transaksi** — daftar semua transaksi + filter, tombol tambah (FAB)
3. **Tambah/Edit Transaksi** — form input
4. **Budget** — daftar kategori + progress budget bulanan
5. **Tabungan** — daftar target tabungan + detail progres
6. **Laporan** — grafik & filter periode
7. **Pengaturan** — kategori, akun, export/import, keamanan (PIN)

Bottom navigation: **Home | Transaksi | Budget | Tabungan | Laporan** (atau gabung ke drawer jika ingin bottom nav lebih ringkas: Home | Transaksi | Budget | Laporan, dengan Tabungan masuk ke Home/sub-menu)

---

## 9. Design Guidelines — O'Duit (Modern & Simple)

- **Prinsip desain:** Minim elemen, banyak whitespace, satu fokus per layar. Hindari dekorasi berlebihan (gradient, shadow tebal, warna ramai) — biarkan angka & data yang jadi pusat perhatian.
- **Gaya visual:** Flat design, rounded corner lembut, card-based layout. Terasa ringan dan cepat, bukan aplikasi "berat" ala software akuntansi.
- **Warna:**
  - Warna dasar netral (putih/abu terang untuk light mode)
  - 1 warna aksen brand (teal/ungu — mencerminkan nama "O'Duit" yang fresh & personal)
  - Hijau khusus untuk pemasukan, merah untuk pengeluaran (semantic color, konsisten di semua layar)
- **Tipografi:** Sans-serif modern, angka nominal ditulis besar & tebal (jadi elemen visual utama di tiap card), label pendukung kecil dan warna netral/muted.
- **Logo/branding:** Wordmark "O'Duit" dengan aksen pada apostrof (bisa berupa simbol koin/lingkaran kecil) — simpel, mudah dikenali di app icon kecil.
- **Interaksi kunci:** Tambah transaksi maksimal 2 tap dari layar manapun (FAB persisten di bottom nav).
- **Motion:** Transisi halus & singkat (150-200ms), hindari animasi berlebihan — kesan cepat dan responsif.

*(Lihat mockup visual "O'Duit" di bawah chat untuk gambaran layout Dashboard)*

---

## 10. Task Breakdown

### Epic 1: Setup Project & Foundation
- [ ] Setup project Android (Kotlin, Compose, struktur folder MVVM)
- [ ] Setup Room DB + entities (Account, Category, Transaction, Budget, SavingsGoal)
- [ ] Setup navigasi dasar (bottom nav + NavHost)
- [ ] Seed data kategori default (Makan, Transport, Belanja, Gaji, dll)

### Epic 2: Catat Pemasukan & Pengeluaran
- [ ] UI daftar transaksi (list per hari, grouped by date)
- [ ] UI form tambah/edit transaksi
- [ ] Fungsi CRUD transaksi (ViewModel + Repository)
- [ ] Fitur pilih/tambah kategori custom
- [ ] Fitur multi-akun (opsional, minimal 1 akun default)
- [ ] Validasi input (nominal, tanggal wajib)

### Epic 3: Budgeting per Kategori
- [ ] UI daftar budget per kategori dengan progress bar
- [ ] Form set/edit limit budget bulanan
- [ ] Kalkulasi otomatis pemakaian budget dari transaksi
- [ ] Indikator warna status budget (aman/warning/over)

### Epic 4: Tracking Tabungan
- [ ] UI daftar target tabungan (card dengan progress %)
- [ ] Form buat/edit target tabungan
- [ ] Fitur tambah kontribusi ke target
- [ ] Kalkulasi estimasi tercapai (berdasarkan rata-rata kontribusi)

### Epic 5: Laporan & Grafik
- [ ] Integrasi library chart (Vico)
- [ ] Pie chart pengeluaran per kategori
- [ ] Line/bar chart tren bulanan
- [ ] Filter periode (bulan ini, 3 bulan, custom range)
- [ ] Card ringkasan (total masuk/keluar/saldo)

### Epic 6: Pengaturan & Keamanan
- [ ] Manajemen kategori (tambah/edit/hapus)
- [ ] Manajemen akun/dompet
- [ ] Export data ke JSON/CSV
- [ ] Import data dari file backup
- [ ] Kunci aplikasi dengan PIN/biometrik

### Epic 7: Polish & Release
- [ ] Dashboard/Home — rangkum semua widget ringkasan
- [ ] Empty state & error handling di semua layar
- [ ] Testing manual end-to-end
- [ ] Icon app, splash screen, app naming
- [ ] Build release APK

---

## 11. Milestone Usulan

| Milestone | Fitur | Estimasi (kerja santai, 1 org) |
|---|---|---|
| M1 — Core | Epic 1 + Epic 2 | 1-2 minggu |
| M2 — Budget & Tabungan | Epic 3 + Epic 4 | 1-2 minggu |
| M3 — Laporan | Epic 5 | 1 minggu |
| M4 — Settings & Release | Epic 6 + Epic 7 | 1 minggu |

Total estimasi kasar: **4-6 minggu** dikerjakan santai di luar kesibukan utama.