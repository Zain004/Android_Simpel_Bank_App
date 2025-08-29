package com.example.simpel_bank_appv2.ui.theme.screens

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.simpel_bank_appv2.data.AppDatabase
import com.example.simpel_bank_appv2.data.BankKontoEntity
import com.example.simpel_bank_appv2.data.KontoRepository
import kotlinx.coroutines.launch
import java.util.UUID

class LandingViewModel(
    private val repository: KontoRepository
) : ViewModel() {

    var nesteVisuelleKontonummerIndex: Long = 0
    val kontoer = mutableStateListOf<BankKontoEntity>()

    init {
        viewModelScope.launch {
            val alleKontoer = repository.getAlleKontoer()
            if (alleKontoer.isEmpty()) {
                val nyKonto = BankKontoEntity(
                    id = UUID.randomUUID().toString(),
                    visueltKontonummer = genererVisueltKontonummer(),
                    kontoeierNavn = "Navn NavnNesen",
                    pengeSum = 0.0
                )
                repository.leggTilKonto(nyKonto)
                kontoer.add(nyKonto)
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
                repository.leggTilKonto(nyKonto)
                kontoer.add(nyKonto)
            }
        }
    }

    private fun genererVisueltKontonummer(): Long {
        val kontonummer = nesteVisuelleKontonummerIndex
        nesteVisuelleKontonummerIndex++
        return kontonummer
    }

    suspend fun getKontoByVisueltKontonummer(visueltKontonummer: Long): BankKontoEntity? {
        return repository.getKontoByVisueltKontonummer(visueltKontonummer)
    }

    fun oppdaterKontoeierNavn(visueltKontonummer: Long, nyttNavn: String) {
        viewModelScope.launch {
            val konto = repository.getKontoByVisueltKontonummer(visueltKontonummer)
            if (konto != null) {
                val oppdatertKonto = konto.copy(kontoeierNavn = nyttNavn)
                repository.oppdaterKonto(oppdatertKonto)
                val index = kontoer.indexOfFirst { it.visueltKontonummer == visueltKontonummer }
                if (index != -1) {
                    kontoer[index] = oppdatertKonto
                }
            }
        }
    }

    fun getRepository(): KontoRepository = repository
}

class LandingViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LandingViewModel::class.java)) {
            val db = AppDatabase.getDatabase(application.applicationContext)
            val repository = KontoRepository(db.bankKontoDao(), db.transaksjonDao())
            @Suppress("UNCHECKED_CAST")
            return LandingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}