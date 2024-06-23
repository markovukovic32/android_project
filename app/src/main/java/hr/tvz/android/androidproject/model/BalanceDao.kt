package hr.tvz.android.androidproject.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BalanceDao {
    @Query("SELECT * FROM balance")
    fun getAll(): List<Balance>
    @Insert
    fun insertAll(vararg balance: Balance)
}