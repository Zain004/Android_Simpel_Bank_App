package com.example.simpel_bank_appv2.data
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.simpel_bank_app.data.BankKontoEntity
import com.example.simpel_bank_app.data.TransaksjonEntity

@Database(entities = [BankKontoEntity::class, TransaksjonEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bankKontoDao(): BankKontoDao
    abstract fun transaksjonDao(): TransaksjonsDao
}