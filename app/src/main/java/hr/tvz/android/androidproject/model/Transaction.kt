package hr.tvz.android.androidproject.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "financial_transaction")
data class Transaction(@PrimaryKey(autoGenerate = true) val uid: Int? = null,
                       @ColumnInfo(name = "description") val description: String?,
                       @ColumnInfo(name = "transaction_type") val transactionType: String?,
                       @ColumnInfo(name = "date") val date: String?,
                       @ColumnInfo(name = "frequency") val frequency: String?,
                       @ColumnInfo(name = "amount") val amount: Double?)
