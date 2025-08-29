package com.example.simpel_bank_appv2.data
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BankKontoEntity::class, TransaksjonEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bankKontoDao(): BankKontoDao
    abstract fun transaksjonDao(): TransaksjonsDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bank_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}