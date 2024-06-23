package hr.tvz.android.androidproject.model

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Transaction::class, Balance::class],
          version = 2,
          exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun balanceDao(): BalanceDao
}