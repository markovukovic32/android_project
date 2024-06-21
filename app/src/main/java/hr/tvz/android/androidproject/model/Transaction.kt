package hr.tvz.android.androidproject.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "financial_transaction")
data class Transaction(@PrimaryKey val uid: Int,
                       @ColumnInfo(name = "description") val firstName: String?,
                       @ColumnInfo(name = "transaction_type") val transactionType: String?,
                       @ColumnInfo(name = "date") val date: String?,
                       @ColumnInfo(name = "interval") val category: String?,
                       @ColumnInfo(name = "amount") val amount: Double?)
