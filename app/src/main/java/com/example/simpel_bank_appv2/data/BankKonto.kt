package com.example.simpel_bank_appv2.data


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bankkontoer")
data class BankKontoEntity (
    @PrimaryKey val id: String,// Generer en unik ID
    val visueltKontonummer: Long = 0L,
    val kontoeierNavn: String = "Ukjent", // hvis ingen input defineres, setter man navn til ukjent automatisk
    val pengeSum: Double = 0.0, // Kontoen starter tom hvis du ikke definerer noen verdi selv som skal overføres
    //val transaksjoner: SnapshotStateList<Transaksjon> = mutableStateListOf()// Liste over kontotransaksjoner som kan endres; bruker mutableStateListOf, fordi gemini anbefaler dette
)