package com.example.simpel_bank_app.ui.screens

// ui/BankViewModel.kt
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.simpel_bank_appv2.data.Transaksjonstype
import com.example.simpel_bank_appv2.data.BankKontoEntity
import com.example.simpel_bank_appv2.data.KontoRepository
import com.example.simpel_bank_appv2.data.TransaksjonEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.locks.ReentrantLock

/*
Tanken bak dette model viewet er at koden min skal være gjenbrukbar altså single responsibility,
slik at klassen har kun attributer og model viewet har metodene
 */
class BankViewModel(
    private val visueltKontonummer: Long,
    private val repository: KontoRepository
) : ViewModel() {



    // Nå bruker vi Flow direkte fra repository
    private val _currentKonto: StateFlow<BankKontoEntity?> =
        repository.getKontoByVisueltKontonummer(visueltKontonummer)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )
    val currentKonto: StateFlow<BankKontoEntity?> = _currentKonto

    val transaksjoner: StateFlow<List<TransaksjonEntity>> =
        repository.getTransaksjoner(visueltKontonummer)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
/*
    init {
        loadKonto()
    }

    private fun loadKonto() {
        viewModelScope.launch(Dispatchers.IO) {
            val konto = repository.getKontoByVisueltKontonummer(visueltKontonummer)
            _currentKonto.value = konto
        }
    }

 */

    // Sett inn beløp
    fun settInn(belop: Double) {
        if (belop <= 0) return
        val konto = _currentKonto.value ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val oppdatertKonto = konto.copy(pengeSum = konto.pengeSum + belop)
            val nyTransaksjon = TransaksjonEntity(
                kontoId = konto.id,
                type = Transaksjonstype.INNSETT,
                belop = belop,
                tidspunkt = System.currentTimeMillis()
            )

            repository.leggTilTransaksjon(nyTransaksjon)
            repository.oppdaterKonto(oppdatertKonto)
            // Flow vil automatisk oppdatere _currentKonto når DB endres_currentKonto.value = oppdatertKonto
        }
    }

    // Ta ut beløp
    suspend fun taUt(belop: Double): Boolean {
        val konto = _currentKonto.value ?: return false
        if (belop <= 0 || belop > konto.pengeSum) return false

        val oppdatertKonto = konto.copy(pengeSum = konto.pengeSum - belop)
        val nyTransaksjon = TransaksjonEntity(
            kontoId = konto.id,
            type = Transaksjonstype.UTTAK,
            belop = belop,
            tidspunkt = System.currentTimeMillis()
        )

        repository.leggTilTransaksjon(nyTransaksjon)
        repository.oppdaterKonto(oppdatertKonto)
        return true
    }



    // Vis saldo

    fun visBalanse(): Double = _currentKonto.value?.pengeSum ?: 0.0


    // Sett kontoeiers navn
    fun settKontoeierNavn(navn: String) {
        if (navn.isBlank()) return
        val konto = _currentKonto.value ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val oppdatertKonto = konto.copy(kontoeierNavn = navn)
            repository.oppdaterKonto(oppdatertKonto)
            // Flow vil automatisk oppdatere _currentKonto når DB endres _currentKonto.value = oppdatertKonto
        }
    }
}