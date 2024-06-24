package hr.tvz.android.androidproject.controller

import TransactionAdapter
import android.content.Intent
import android.view.View
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
        mainActivity?.updateDateTextView("Balance on " + newDate + " is " + getBalanceUntilDate(newDate).current_balance + "EUR.")

        val transactions = getTransactionOnDate(newDate)
        val transactionAdapter = TransactionAdapter(transactions)
        mainActivity?.setAdapter(transactionAdapter)
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
    fun getBalanceUntilDate(date: String): Balance {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val endDate = sdf.parse(date)
        val transactions = transactionDao?.getAll()
        var balance = balanceDao!!.getAll()[0]

        var balanceDate = sdf.parse(balance.date!!)

        if (transactions != null) {
            for (transaction in transactions) {
                val transactionDate = sdf.parse(transaction.date!!)
                if (transactionDate != null) {
                    if (transactionDate.after(balanceDate) && !transactionDate.after(endDate)) {
                        if (transaction.transactionType == "Income") {
                            if (transaction.amount != null) {
                                balance.current_balance = balance.current_balance.plus(transaction.amount)
                            }
                        } else {
                            if (transaction.amount != null) {
                                balance.current_balance = balance.current_balance.minus(transaction.amount)
                            }
                        }
                    }
                }
            }
        }
        return balance
    }
    fun getTransactionOnDate(date: String): List<Transaction>{
        val transactions = transactionDao!!.getAll().filter { it.date == date }
        return transactions
    }

    fun setBalance(balance: Balance) {
        balanceDao?.insertAll(balance)
        mainActivity?.getBalanceUntilDate(balance.date!!)
    }
}