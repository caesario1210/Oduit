package com.oduit.app.util

private val lastMessage = mutableMapOf<String, String>()

private fun pickNoRepeat(key: String, messages: List<String>): String {
    val filtered = messages.filter { it != lastMessage[key] }
    val chosen = (if (filtered.isNotEmpty()) filtered else messages).random()
    lastMessage[key] = chosen
    return chosen
}

fun getWittyMessage(category: String, type: String): String {
    return when (type.lowercase()) {
        "pemasukan" -> pickNoRepeat("pemasukan", listOf(
            "Asyik! Fase kaya raya sementara resmi dimulai \uD83D\uDE0E",
            "Ada angin segar masuk, tahan selera jajan ya! \uD83D\uDCB8",
            "Alhamdulillah, saldo terisi kembali. Jaga baik-baik!",
        ))

        "transfer" -> pickNoRepeat("transfer", listOf(
            "Cuma pindah lapak, uangnya tetap milikmu kok \uD83D\uDD01",
            "Oper bola finansial berhasil! \u26BD",
            "Uang cuma pindah rumah, jangan baper ya.",
        ))

        else -> when (category) {
            "Makan & Minum" -> pickNoRepeat("Makan & Minum", listOf(
                "Perut kenyang, dompet yang ganti diet \uD83C\uDF5C",
                "Makan enak jalan terus, dompet menangis kemudian \uD83C\uDF7D\uFE0F",
            ))

            "Belanja" -> pickNoRepeat("Belanja", listOf(
                "Katanya self-reward, tapi kok dompet tersiksa? \uD83D\uDECD\uFE0F",
                "Checkout sekarang, meringis kemudian \uD83D\uDCE6",
            ))

            "Hiburan" -> pickNoRepeat("Hiburan", listOf(
                "Healing sejenak, pusingnya seminggu! \uD83C\uDFAE",
                "Bahagianya dapat, saldonya lewat \u2728",
            ))

            "Kesehatan" -> pickNoRepeat("Kesehatan", listOf(
                "Sehat itu mahal, tapi dompet ikhlas demi kesehatan \uD83E\uDE7A",
                "Lekas pulih badan dan saldonya!",
            ))

            "Transportasi" -> pickNoRepeat("Transportasi", listOf(
                "Bensin terisi, siap menerjang kerasnya dunia \uD83D\uDEF5",
                "Ongkos jalan aman!",
            ))

            else -> pickNoRepeat("default", listOf(
                "Pengeluaran dicatat! Dompet berbisik: 'Hemat ya!' \uD83D\uDCB3",
                "Catatan disimpan. Tetap kendalikan keuanganmu!",
                "Uang keluar lagi, dompet mulai protes \uD83D\uDCB0",
                "Semoga bahagia, saldo: berkurang \uD83D\uDE22",
            ))
        }
    }
}
