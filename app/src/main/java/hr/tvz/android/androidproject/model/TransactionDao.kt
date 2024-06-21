package hr.tvz.android.androidproject.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TransactionDao {
    @Query("SELECT * FROM financial_transaction")
    fun getAll(): List<Transaction>
    @Insert
    fun insertAll(vararg transactions: Transaction)
    @Delete
    fun delete(transaction: Transaction)
}