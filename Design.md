# design.md — O'Duit

> Dokumen ini adalah spesifikasi desain untuk aplikasi **O'Duit**, ditulis agar bisa langsung dipakai sebagai referensi oleh AI coding assistant (misal Claude Code) saat mengimplementasikan UI. Semua nilai di sini bersifat definitif kecuali ditandai `(opsional)`.

## 1. Identitas Produk

| Key | Value |
|---|---|
| App name | O'Duit |
| Tagline | Duit kamu, kendali kamu. |
| Platform | Android (native), min SDK 26 (Android 8.0) |
| Package name (usulan) | `com.oduit.app` |
| Design direction | Modern, simple, flat, minim dekorasi, data-first |

## 2. Design Tokens

### 2.1 Warna

Gunakan format ini sebagai `Color.kt` di Jetpack Compose (light mode). Dark mode wajib didukung — gunakan mapping yang sama polanya (surface makin gelap, text makin terang).

```
// Brand
brand            = #6C5CE7   // ungu — warna aksen utama (tombol, FAB, highlight)
brandOnColor     = #FFFFFF   // teks/icon di atas brand

// Semantic
income           = #1D9E75   // hijau — pemasukan
incomeBg         = #E1F5EE   // background chip/badge income
expense          = #E24B4A   // merah — pengeluaran
expenseBg        = #FCEBEB   // background chip/badge expense
warning          = #EF9F27   // amber — budget mendekati limit
warningBg        = #FAEEDA
danger           = #E24B4A   // budget over limit (sama dgn expense)

// Neutral / Surface (light mode)
surfacePage      = #F7F6F3   // background layar
surfaceCard      = #FFFFFF   // background card
surfaceMuted     = #F1EFE8   // background elemen sekunder (progress bar track, dll)
border           = #E4E2DA   // border tipis 0.5-1dp
textPrimary      = #1C1B18   // teks utama
textSecondary    = #6B6A64   // teks pendukung/label
textMuted        = #9C9A92   // placeholder, caption

// Neutral / Surface (dark mode)
surfacePageDark  = #161513
surfaceCardDark  = #211F1C
surfaceMutedDark = #2B2925
borderDark       = #37352F
textPrimaryDark  = #F5F4F1
textSecondaryDark= #B5B3AC
textMutedDark    = #7C7A73
```

### 2.2 Tipografi

Font: **Inter** (atau font sistem default Android jika ingin lebih ringan — Roboto).

| Style | Size | Weight | Penggunaan |
|---|---|---|---|
| `display` | 30sp | Medium (500) | Angka saldo di dashboard |
| `headline` | 22sp | Medium (500) | Judul layar |
| `title` | 16sp | Medium (500) | Judul card / section header |
| `body` | 14sp | Regular (400) | Teks umum |
| `label` | 12sp | Regular (400) | Label field, caption, timestamp |
| `numberMedium` | 18sp | Medium (500) | Nominal transaksi di list |

Aturan: **hanya 2 font weight** dipakai di seluruh app — Regular (400) dan Medium (500). Jangan pakai Bold (700) agar terasa ringan.

### 2.3 Spacing & Radius

```
spacing: 4, 8, 12, 16, 20, 24, 32   (dp, kelipatan 4)
radius.card    = 12dp
radius.control = 8dp
radius.pill    = 999dp   (badge, chip)
radius.fab     = 50%     (lingkaran penuh)
```

### 2.4 Elevation

Hindari shadow berat. Gunakan border 0.5-1dp (`border` token) untuk memisahkan card dari background, bukan drop shadow. Jika perlu elevation (misal bottom sheet), gunakan shadow sangat tipis: `elevation = 2dp` max.

## 3. Komponen UI (Reusable)

### 3.1 `AmountText`
Menampilkan nominal uang. Selalu format `Rp` + pemisah ribuan titik. Warna otomatis mengikuti tipe:
- income → `income` color, prefix `+`
- expense → `expense` color, prefix `-`
- netral (saldo) → `textPrimary`, tanpa prefix

### 3.2 `TransactionRow`
Layout: `[icon kategori (circle 32dp)] [nama transaksi + tanggal (2 baris)] .... [AmountText]`
- Icon background = `surfaceMuted`, icon color = `textSecondary`
- Tap → buka detail/edit transaksi

### 3.3 `BudgetProgressItem`
Layout: label kategori + persentase di kanan atas, progress bar tipis (5-6dp height, radius penuh) di bawahnya.
- Warna progress bar: `<80%` → `brand`, `80-100%` → `warning`, `>100%` → `danger`

### 3.4 `PrimaryButton` / `SecondaryButton`
- Primary: background `brand`, text `brandOnColor`, radius `radius.control`, height 48dp
- Secondary: background transparan, border 1dp `border`, text `textPrimary`

### 3.5 `FAB Tambah Transaksi`
- Posisi: tengah bottom navigation, sedikit menonjol ke atas (offset -12dp dari bar)
- Size: 56dp, background `brand`, icon `+` putih 24dp
- Selalu terlihat di semua tab utama (Home, Transaksi, Budget, Laporan)

### 3.6 `CategoryChip`
Pill kecil untuk filter kategori. Selected state: background `brand` 10% opacity + text `brand`. Unselected: background `surfaceMuted` + text `textSecondary`.

## 4. Struktur Navigasi

Bottom navigation, 4 tab tetap + FAB di tengah:

```
[Home] [Transaksi] (FAB: Tambah) [Budget] [Laporan]
```

Tabungan diakses dari Home (card ringkasan) atau dari menu di app bar Laporan — bukan tab utama, supaya bottom nav tetap 4 item + FAB dan tidak sesak (ikuti batas mobile: maksimal item nav dibuat sedikit dan jelas).

## 5. Spesifikasi Layar

### 5.1 Home / Dashboard
Urutan elemen dari atas ke bawah:
1. App bar: logo + nama "O'Duit" (kiri), icon notifikasi (kanan)
2. Card saldo: label "Saldo total" (label style) → nominal besar (display style) → 2 baris kecil ringkasan masuk/keluar bulan ini
3. Section "Budget bulan ini": list `BudgetProgressItem` untuk 2-3 kategori dengan pemakaian tertinggi, link "Lihat semua" ke tab Budget
4. Section "Transaksi terbaru": 3-5 `TransactionRow` terbaru, link "Semua" ke tab Transaksi
5. Bottom navigation + FAB

### 5.2 Transaksi (List)
1. App bar: judul "Transaksi", icon filter (kanan)
2. Filter bar horizontal: chip periode (Hari ini/Minggu ini/Bulan ini/Custom) + chip kategori (opsional, scrollable)
3. List `TransactionRow` dikelompokkan per tanggal (header tanggal: `title` style, sticky)
4. Empty state jika belum ada transaksi: ilustrasi sederhana + teks "Belum ada transaksi" + tombol "Tambah transaksi"

### 5.3 Tambah/Edit Transaksi (Bottom sheet atau full screen)
Form fields berurutan:
1. Toggle jenis: Pengeluaran / Pemasukan (segmented control, default: Pengeluaran)
2. Input nominal — besar, di tengah, keyboard numerik langsung aktif
3. Pilih kategori — grid icon (`CategoryChip` style, scrollable), tombol "+" untuk kategori baru
4. Tanggal — default hari ini, tap untuk buka date picker
5. Catatan (opsional) — text field satu baris
6. Pilih akun/dompet — dropdown, default akun pertama
7. `PrimaryButton` "Simpan" di bawah, sticky

### 5.4 Budget
1. App bar: judul "Budget", selector bulan (kiri/kanan arrow + nama bulan)
2. List `BudgetProgressItem` untuk semua kategori pengeluaran, termasuk yang belum di-set limit (tampilkan CTA "Set limit")
3. FAB kecil "+" untuk menambah budget kategori baru (terpisah dari FAB tambah transaksi, atau gunakan app bar action)

### 5.5 Tabungan
1. App bar: judul "Tabungan"
2. Grid/list card target tabungan: nama target, progress ring atau bar, nominal terkumpul/target, estimasi tanggal tercapai
3. Tap card → detail target: riwayat kontribusi + tombol "Tambah dana"
4. Tombol "+ Target baru" mengambang atau di app bar

### 5.6 Laporan
1. App bar: judul "Laporan", filter periode (kanan)
2. Card ringkasan: total masuk, keluar, saldo bersih periode terpilih (3 angka horizontal)
3. Pie chart pengeluaran per kategori + legend di bawahnya
4. Line/bar chart tren 6-12 bulan terakhir
5. List breakdown kategori (nominal + persentase), tap untuk drill-down ke daftar transaksi kategori tsb

### 5.7 Pengaturan
List menu sederhana (ListItem dengan icon + label + chevron kanan):
- Kategori (kelola)
- Akun/Dompet (kelola)
- Keamanan (PIN/biometrik)
- Export/Import data
- Tentang O'Duit

## 6. Ikon

Gunakan icon set outline konsisten (contoh referensi: Tabler Icons / Material Symbols Outlined). Daftar icon per konteks:

```
home        -> ti-home
transaksi   -> ti-list
budget      -> ti-chart-pie
laporan     -> ti-report-analytics  (jika laporan jadi tab terpisah)
tabungan    -> ti-target
tambah      -> ti-plus
pengaturan  -> ti-settings
kategori makan     -> ti-tools-kitchen-2
kategori transport -> ti-car
kategori belanja   -> ti-shopping-bag
kategori gaji      -> ti-wallet
kategori tagihan   -> ti-file-invoice
```

## 7. Dark Mode

Wajib didukung sejak awal (bukan tambahan v2). Semua token warna sudah punya pasangan dark mode di section 2.1. Semantic color (income/expense/warning) tetap sama hue, hanya surface & text yang berubah.

## 8. Referensi Visual

Mockup dashboard sudah dibuat dan ditampilkan di percakapan sebelumnya (card saldo besar, progress bar tipis untuk budget, list transaksi dengan icon bulat, bottom nav dengan FAB menonjol). Gunakan itu sebagai acuan visual utama untuk layout Home.