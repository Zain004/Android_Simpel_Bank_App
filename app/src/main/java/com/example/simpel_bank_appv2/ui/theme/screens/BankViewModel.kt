package com.example.simpel_bank_app.ui.screens

// ui/BankViewModel.kt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.simpel_bank_app.data.BankKonto
import com.example.simpel_bank_app.data.Transaksjon
import com.example.simpel_bank_app.data.Transaksjonstype
import java.util.Date
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/*
Tanken bak dette model viewet er at koden min skal være gjenbrukbar altså single responsibility,
slik at klassen har kun attributer og model viewet har metodene
 */
class BankViewModel : ViewModel() {

    // Synkron lås for trådsikker oppdatering
    private val lock = ReentrantLock()

    // Observable state for Compose
    var bankKonto by mutableStateOf(BankKonto())
        private set

    // Sett inn beløp
    fun settInn(belop: Double) {
        if (belop > 0) {
            lock.withLock {
                bankKonto.pengeSum += belop
                bankKonto.transaksjoner.add(
                    Transaksjon(
                        Transaksjonstype.INNSETT,
                        belop = belop,
                        tidspunkt = Date()
                    ))
            }
        }
    }

    // Ta ut beløp
    fun taUt(belop: Double): Boolean {
        return lock.withLock {
            if (belop > 0 && belop <= bankKonto.pengeSum) {
                bankKonto.pengeSum -= belop
                bankKonto.transaksjoner.add(
                    Transaksjon(
                        Transaksjonstype.UTTAK,
                        belop = belop,
                        Date()))
                true
            } else {
                false
            }
        }
    }

    // Vis saldo
    fun visBalanse(): Double {
        return bankKonto.pengeSum
    }

    // Vis transaksjoner
    fun visTransaksjoner(): List<Transaksjon> {
        return bankKonto.transaksjoner
    }

    // Sett kontoeiers navn
    fun settKontoeierNavn(navn: String) {
        lock.withLock {
            bankKonto = bankKonto.copy(kontoeierNavn = navn)
        }
    }
}