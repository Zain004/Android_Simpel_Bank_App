package com.example.simpel_bank_appv2.data

class KontoRepository(private val dao: BankKontoDao,
    private val transaksjonsDao: TransaksjonsDao) {

    fun getKontoByVisueltKontonummer(visueltKontonummer: Long) = dao.getKontoFlow(visueltKontonummer)

    suspend fun oppdaterKonto(konto: BankKontoEntity) {
        dao.oppdaterKonto(konto)
    }

    suspend fun leggTilKonto(konto: BankKontoEntity) {
        dao.leggTilKonto(konto)
    }

    fun getAlleKontoer() = dao.getAlleKontoerFlow()
    suspend fun leggTilTransaksjon(transaksjon: TransaksjonEntity) {
        transaksjonsDao.leggTilTransaksjon(transaksjon)
    }

    suspend fun getTransaksjoner(kontoId: Long): List<TransaksjonEntity> {
        return transaksjonsDao.getTransaksjoner(kontoId.toString())
    }
}