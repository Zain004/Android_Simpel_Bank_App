package com.example.simpel_bank_appv2.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.simpel_bank_appv2.data.Transaksjonstype
import java.util.Date

@Entity(
    tableName = "transaksjoner",
    foreignKeys = [
        ForeignKey(
            entity = BankKontoEntity::class,
            parentColumns = ["id"],
            childColumns = ["kontoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["kontoId"])] // Viktig for Room!
)
data class TransaksjonEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val kontoId: Long,
    val type: Transaksjonstype, // INNSETT eller UTTAK
    val belop: Double,
    val tidspunkt: Long
)