package com.example.simpel_bank_app.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList

// data/BankKonto.kt
data class BankKonto(
    var kontoeierNavn: String = "Ukjent", // hvis ingen input defineres, setter man navn til ukjent automatisk
    var pengeSum: Double = 0.0, // Kontoen starter tom hvis du ikke definerer noen verdi selv som skal overføres
    val transaksjoner: SnapshotStateList<Transaksjon> = mutableStateListOf()// Liste over kontotransaksjoner som kan endres; bruker mutableStateListOf, fordi gemini anbefaler dette
)