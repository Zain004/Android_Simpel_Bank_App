package com.example.simpel_bank_appv2.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransaksjonsDao {
    @Query("SELECT * FROM transaksjoner WHERE kontoId = :kontoId ORDER BY tidspunkt DESC")
    fun getTransaksjonerFlow(kontoId: Long): Flow<List<TransaksjonEntity>>

    @Insert
    suspend fun leggTilTransaksjon(transaksjon: TransaksjonEntity)
}