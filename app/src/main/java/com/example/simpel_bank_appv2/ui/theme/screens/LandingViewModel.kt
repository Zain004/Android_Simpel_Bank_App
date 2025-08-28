package com.example.simpel_bank_appv2.ui.theme.screens

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.room.Room
import com.example.simpel_bank_appv2.data.AppDatabase
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room.databaseBuilder
import com.example.simpel_bank_app.data.BankKontoEntity
import kotlinx.coroutines.launch
import java.util.UUID

class LandingViewModel(application: Application) : AndroidViewModel(application) {

    val db: AppDatabase = databaseBuilder(
        application.applicationContext,   // Context
        AppDatabase::class.java,          // Database-klasse
        "bank_db"                         // Navn på databasen
    ).build()
    val kontoDao = db.bankKontoDao()
    val transaksjonDao = db.transaksjonDao()

    var nesteVisuelleKontonummerIndex: Long = 0

    val kontoer = mutableStateListOf<BankKontoEntity>()

    init {
        viewModelScope.launch {
            val alleKontoer = kontoDao.getAlleKontoer()
            if (alleKontoer.isEmpty()) {
                val nyKonto = BankKontoEntity(
                    id = UUID.randomUUID().toString(),
                    visueltKontonummer = genererVisueltKontonummer(),
                    kontoeierNavn = "Navn NavnNesen",
                    pengeSum = 0.0
                )
                kontoDao.leggTilKonto(nyKonto)
                kontoer.addAll(alleKontoer)
            } else {
                kontoer.addAll(alleKontoer)
            }
        }
    }
    fun leggTilKonto(navn: String) {
        if (navn.isNotBlank()) {
            viewModelScope.launch {
                val nyKonto = BankKontoEntity(
                    id = UUID.randomUUID().toString(),
                    visueltKontonummer = genererVisueltKontonummer(),
                    kontoeierNavn = navn,
                    pengeSum = 0.0
                )
                kontoDao.leggTilKonto(nyKonto)
            kontoer.add(nyKonto)
            }
        }
    }

    private fun genererVisueltKontonummer(): Long {
        // Vi kan formatere det som en streng senere for visning hvis ønskelig (f.eks. med ledende nuller)
        // For nå returnerer vi bare Long-verdien.
        val kontonummer = nesteVisuelleKontonummerIndex
        nesteVisuelleKontonummerIndex++
        return kontonummer
    }

    fun getKontoByVisueltKontonummer(visueltKontonummer: Long) : BankKontoEntity? {
        return kontoDao.getKonto(visueltKontonummer)
    }
    fun oppdaterKontoeierNavn(visueltKontonummer: Long, nyttNavn: String) {
        viewModelScope.launch {
            val konto = kontoDao.getKonto(visueltKontonummer)
            if (konto != null) {
                val oppdatertKonto = konto.copy(nyttNavn)
                // oppdater i databasen
                kontoDao.oppdaterKonto(oppdatertKonto)
                // oppdater i tabellen
                val index = kontoer.indexOfFirst { it.visueltKontonummer == visueltKontonummer }
                if (index != -1) {
                    kontoer[index] = oppdatertKonto
                }
            }
        }
    }
}