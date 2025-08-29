package com.example.simpel_bank_appv2.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TransaksjonsDao {
    @Query("SELECT * FROM transaksjoner WHERE kontoId = :kontoId")
    suspend fun getTransaksjoner(kontoId: String): List<TransaksjonEntity>

    @Insert
    suspend fun leggTilTransaksjon(transaksjon: TransaksjonEntity)
}