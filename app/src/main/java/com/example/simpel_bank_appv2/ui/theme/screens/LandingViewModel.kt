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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class LandingViewModel(
    private val repository: KontoRepository
) : ViewModel() {

    var nesteVisuelleKontonummerIndex: Long = 0
    val kontoer = repository.getAlleKontoer()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Opprett en standardkonto hvis databasen er tom
        viewModelScope.launch {
            repository.getAlleKontoer().collect { alleKontoer ->
                if (alleKontoer.isEmpty()) {
                    val nyKonto = BankKontoEntity(
                        kontoeierNavn = "Navn NavnNesen",
                        pengeSum = 0.0
                    )
                    repository.leggTilKonto(nyKonto)
                }
            }
        }
    }

    fun leggTilKonto(navn: String) {
        if (navn.isNotBlank()) {
            viewModelScope.launch {
                val nyKonto = BankKontoEntity(
                    kontoeierNavn = navn,
                    pengeSum = 0.0
                )
                repository.leggTilKonto(nyKonto)
            }
        }
    }

    fun getKontoByVisueltKontonummer(id: Long): Flow<BankKontoEntity?> {
        return repository.getKontoByVisueltKontonummer(id)
    }

    fun oppdaterKontoeierNavn(visueltKontonummer: Long, nyttNavn: String) {
        viewModelScope.launch {
            repository.getKontoByVisueltKontonummer(visueltKontonummer).collect { konto ->
                konto?.let {
                    val oppdatertKonto = it.copy(kontoeierNavn = nyttNavn)
                    repository.oppdaterKonto(oppdatertKonto)
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
