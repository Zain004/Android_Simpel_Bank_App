package com.example.simpel_bank_appv2.ui.theme.screens

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.simpel_bank_app.data.BankKonto
import java.util.UUID

class LandingViewModel : ViewModel() {
    // Bruk mutableStateListOf for at Compose skal reagere på endringer i listen
    private val _kontoer = mutableStateListOf<BankKonto>()
    val kontoer: List<BankKonto> get() = _kontoer

    private var nesteVisuelleKontonummerIndex = 1L

    init {
        if (_kontoer.isEmpty()) {
            leggTilKonto("Navn NavnNesen")
        }
    }
    fun leggTilKonto(navn: String) {
        if (navn.isNotBlank()) {
            val nyKonto = BankKonto(
                id = UUID.randomUUID().toString(),
                visueltKontonummer = genererVisueltKontonummer(),
                kontoeierNavn = navn,
                pengeSum = 0.0
            )
            _kontoer.add(nyKonto)
        }
    }

    private fun genererVisueltKontonummer(): Long {
        // Vi kan formatere det som en streng senere for visning hvis ønskelig (f.eks. med ledende nuller)
        // For nå returnerer vi bare Long-verdien.
        val kontonummer = nesteVisuelleKontonummerIndex
        nesteVisuelleKontonummerIndex++
        return kontonummer
    }

    fun getKontoByVisueltKontonummer(visueltKontonummer: Long) : BankKonto? {
        return _kontoer.find {it.visueltKontonummer == visueltKontonummer}
    }
    fun oppdaterKontoeierNavn(visueltKontonummer: Long, nyttNavn: String) {
        val kontoIndex = _kontoer.indexOfFirst { it.visueltKontonummer == visueltKontonummer }
        if (kontoIndex != -1) {
            val gammelKonto = _kontoer[kontoIndex]
            _kontoer[kontoIndex] = gammelKonto.copy(kontoeierNavn = nyttNavn)
        }
    }
}