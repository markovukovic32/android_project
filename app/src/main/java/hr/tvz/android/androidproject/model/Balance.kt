package hr.tvz.android.androidproject.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "balance")
data class Balance(@PrimaryKey(autoGenerate = true) val uid: Int? = null,
                       @ColumnInfo(name = "current_balance") val current_balance: Double?)
