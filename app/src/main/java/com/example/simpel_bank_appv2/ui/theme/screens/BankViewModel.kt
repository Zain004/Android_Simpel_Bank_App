package com.example.simpel_bank_app.ui.screens

// ui/BankViewModel.kt
import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.simpel_bank_appV2.data.Transaksjonstype
import com.example.simpel_bank_appv2.data.AppDatabase
import com.example.simpel_bank_appv2.data.BankKontoEntity
import com.example.simpel_bank_appv2.data.TransaksjonEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/*
Tanken bak dette model viewet er at koden min skal være gjenbrukbar altså single responsibility,
slik at klassen har kun attributer og model viewet har metodene
 */
class BankViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "bank_db"
    ).build()
    private val lock: ReentrantLock
        get() = ReentrantLock()

    val kontoDao = db.bankKontoDao()
    val transaksjonDao = db.transaksjonDao()

    private val _currentKonto = MutableStateFlow<BankKontoEntity?>(null)
    val currentKonto: StateFlow<BankKontoEntity?> = _currentKonto.asStateFlow()


    // Sett inn beløp
    fun settInn(belop: Double) {
        if (belop > 0) {
            viewModelScope.launch(Dispatchers.IO) {
                    val currentKontoState = _currentKonto.value ?: return@launch
                    val updatedKonto = currentKontoState.copy(
                        pengeSum = currentKontoState.pengeSum + belop
                    )

                    val nyTransaksjon = TransaksjonEntity(
                        kontoId = updatedKonto.id,
                        type = Transaksjonstype.INNSETT,
                        belop = updatedKonto.pengeSum,
                        tidspunkt = System.currentTimeMillis()
                    )
                    transaksjonDao.leggTilTransaksjon(nyTransaksjon)
                    val updatedKontoEntity = BankKontoEntity(
                        id = updatedKonto.id,
                        visueltKontonummer = updatedKonto.visueltKontonummer,
                        kontoeierNavn = updatedKonto.kontoeierNavn,
                        pengeSum = updatedKonto.pengeSum
                    )
                    kontoDao.oppdaterKonto(updatedKontoEntity)
            }
        }
    }

    // Ta ut beløp
    suspend fun taUt(belop: Double): Boolean {
        if (belop <= 0) return false

        val currentKontoState = _currentKonto.value ?: return false

        if (belop <= currentKontoState.pengeSum) {
            val updatedKonto = currentKontoState.copy(pengeSum = currentKontoState.pengeSum - belop)

            val nyTransaksjon = TransaksjonEntity(
                kontoId = updatedKonto.id,
                type = Transaksjonstype.UTTAK,
                belop = belop,
                tidspunkt = System.currentTimeMillis()
            )

            transaksjonDao.leggTilTransaksjon(nyTransaksjon)
            kontoDao.oppdaterKonto(updatedKonto)
            _currentKonto.value = updatedKonto

            return true
        }

        return false
    }


    // Vis saldo
    fun visBalanse(): Double {
        return _currentKonto.value?.pengeSum ?: 0.0
    }

    // Vis transaksjoner
    suspend fun visTransaksjoner(): List<TransaksjonEntity> {
        val konto = _currentKonto.value ?: return emptyList()
        return transaksjonDao.getTransaksjoner(konto.id)
    }


    // Sett kontoeiers navn
    fun settKontoeierNavn(navn: String) {
        if (navn.isBlank()) return // Validering

        viewModelScope.launch(Dispatchers.IO) {
            val currentKontoState = _currentKonto.value ?: return@launch

            // Oppdater ViewModel State FØR DB
            val updatedKonto = currentKontoState.copy(kontoeierNavn = navn)
            _currentKonto.value = updatedKonto

            // Persister endringer til Room
            val updatedKontoEntity = BankKontoEntity(
                id = updatedKonto.id,
                visueltKontonummer = updatedKonto.visueltKontonummer,
                kontoeierNavn = updatedKonto.kontoeierNavn,
                pengeSum = updatedKonto.pengeSum
            )
            kontoDao.oppdaterKonto(updatedKontoEntity)
        }
    }
}