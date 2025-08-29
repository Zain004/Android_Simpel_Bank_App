package com.example.simpel_bank_appv2.data

class KontoRepository(private val dao: BankKontoDao,
    private val transaksjonsDao: TransaksjonsDao) {

    suspend fun getKontoByVisueltKontonummer(visueltKontonummer: Long): BankKontoEntity? {
        return dao.getKonto(visueltKontonummer)
    }

    suspend fun oppdaterKonto(konto: BankKontoEntity) {
        dao.oppdaterKonto(konto)
    }

    suspend fun leggTilKonto(konto: BankKontoEntity) {
        dao.leggTilKonto(konto)
    }

    suspend fun getAlleKontoer(): List<BankKontoEntity> {
        return dao.getAlleKontoer()
        // evt. flere metoder, f.eks. updateKonto(), insertKonto() osv.
    }
    suspend fun leggTilTransaksjon(transaksjon: TransaksjonEntity) {
        transaksjonsDao.leggTilTransaksjon(transaksjon)
    }

    suspend fun getTransaksjoner(kontoId: Long): List<TransaksjonEntity> {
        return transaksjonsDao.getTransaksjoner(kontoId.toString())
    }
}