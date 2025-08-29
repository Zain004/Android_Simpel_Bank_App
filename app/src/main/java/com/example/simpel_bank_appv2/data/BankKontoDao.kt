package com.example.simpel_bank_appv2.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

import com.example.simpel_bank_appv2.data.BankKontoEntity
import kotlinx.coroutines.flow.Flow

// BankkontoDao.kt

@Dao
interface BankKontoDao {
    @Query("SELECT * FROM bankkontoer")
    fun getAlleKontoerFlow(): Flow<List<BankKontoEntity>>

    @Query("SELECT * FROM bankkontoer WHERE visueltKontonummer = :visueltKontonummer LIMIT 1")
    fun getKontoFlow(visueltKontonummer: Long): Flow<BankKontoEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun leggTilKonto(konto: BankKontoEntity)

    @Update
    suspend fun oppdaterKonto(konto: BankKontoEntity)
}
