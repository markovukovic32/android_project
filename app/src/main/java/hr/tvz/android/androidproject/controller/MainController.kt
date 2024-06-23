package hr.tvz.android.androidproject.controller

import android.content.Intent
import android.widget.Toast
import androidx.room.Room
import hr.tvz.android.androidproject.model.AppDatabase
import hr.tvz.android.androidproject.model.Balance
import hr.tvz.android.androidproject.model.BalanceDao
import hr.tvz.android.androidproject.model.Transaction
import hr.tvz.android.androidproject.model.TransactionDao
import java.text.SimpleDateFormat
import hr.tvz.android.androidproject.view.MainActivity
import hr.tvz.android.androidproject.view.NewTransactionActivity
import java.util.Locale
import java.util.*

class MainController() {
    private var mainActivity: MainActivity? = null
    private var newTransactionActivity: NewTransactionActivity? = null
    private var db: AppDatabase? = null
    private var transactionDao: TransactionDao? = null
    private var balanceDao: BalanceDao? = null

    constructor(mainActivity: MainActivity) : this() {
        this.mainActivity = mainActivity
        val db = Room.databaseBuilder(
            mainActivity.applicationContext,
            AppDatabase::class.java, "transaction-database"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
        this.db = db
        this.transactionDao = db.transactionDao()
        this.balanceDao = db.balanceDao()
    }

    constructor(newTransactionActivity: NewTransactionActivity) : this() {
        this.newTransactionActivity = newTransactionActivity
        val db = Room.databaseBuilder(
            newTransactionActivity.applicationContext,
            AppDatabase::class.java, "transaction-database"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
        this.db = db
        this.transactionDao = db.transactionDao()
        this.balanceDao = db.balanceDao()
    }

    fun onDateSelected(date: String) {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.time = sdf.parse(date)
        val newDate = sdf.format(calendar.time)
        mainActivity?.updateDateTextView("Balance on " + newDate + " is 0.00 EUR.")
    }

    fun addTransaction() {
        val intent = Intent(mainActivity, NewTransactionActivity::class.java)
        mainActivity?.startActivity(intent)
    }

    fun addTransactionToDatabase(transaction: Transaction) {
        transactionDao?.insertAll(transaction)
        val context = mainActivity ?: newTransactionActivity
        Toast.makeText(context, "Transaction added successfully", Toast.LENGTH_SHORT).show()
    }

    fun setBalance(balance: Balance) {
        balanceDao?.insertAll(balance)
    }
}