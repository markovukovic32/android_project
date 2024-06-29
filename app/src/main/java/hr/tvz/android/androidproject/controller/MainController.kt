package hr.tvz.android.androidproject.controller

import TransactionAdapter
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import hr.tvz.android.androidproject.model.AppDatabase
import hr.tvz.android.androidproject.model.Balance
import hr.tvz.android.androidproject.model.BalanceDao
import hr.tvz.android.androidproject.model.Transaction
import hr.tvz.android.androidproject.model.TransactionDao
import java.text.SimpleDateFormat
import hr.tvz.android.androidproject.view.MainActivity
import hr.tvz.android.androidproject.view.NewTransactionActivity
import hr.tvz.android.androidproject.view.TransactionOverviewActivity
import java.util.Locale
import java.util.*

class MainController() {
    private var mainActivity: MainActivity? = null
    private var newTransactionActivity: NewTransactionActivity? = null
    private var transactionOverviewActivity: TransactionOverviewActivity? = null
    private var db: AppDatabase? = null
    private var transactionDao: TransactionDao? = null
    private var balanceDao: BalanceDao? = null

    private fun initDb(context: Context) {
        val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "transaction-database"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
        this.db = db
        this.transactionDao = db.transactionDao()
        this.balanceDao = db.balanceDao()
    }

    constructor(mainActivity: MainActivity) : this() {
        this.mainActivity = mainActivity
        initDb(mainActivity.applicationContext)
    }

    constructor(newTransactionActivity: NewTransactionActivity) : this() {
        this.newTransactionActivity = newTransactionActivity
        initDb(newTransactionActivity.applicationContext)
    }

    constructor(transactionOverviewActivity: TransactionOverviewActivity) : this() {
        this.transactionOverviewActivity = transactionOverviewActivity
        initDb(transactionOverviewActivity.applicationContext)
    }
    fun onDateSelected(date: String) {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.time = sdf.parse(date)!!
        val newDate = sdf.format(calendar.time)
        displayBalanceOnDate(newDate)
        val transactions = getTransactionOnDate(newDate)
        mainActivity?.let { setAdapter(transactions, it) }
    }
    public fun displayBalanceOnDate(newDate: String) {
        mainActivity?.updateDateTextView("Balance on " + newDate + " is " + getBalanceUntilDate(newDate).current_balance + "EUR.")
    }
    fun initializeAdapter(){
        val transactions = getAllTransactions()
        transactionOverviewActivity?.let { setAdapter(transactions, it) }
    }
    fun setAdapter(transactions: List<Transaction>, activity: AppCompatActivity) {
        val transactionAdapter = TransactionAdapter(transactions, this)
        when (activity) {
            is MainActivity -> activity.setAdapter(transactionAdapter)
            is TransactionOverviewActivity -> activity.setAdapter(transactionAdapter)
        }
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
        val balance = balanceDao!!.getAll()[0]

        val balanceDate = sdf.parse(balance.date)

        transactions?.forEach { transaction ->
            val transactionDate = sdf.parse(transaction.date!!)
            transactionDate?.let {
                if (it.after(balanceDate) && !it.after(endDate)) {
                    transaction.amount?.let { amount ->
                        balance.current_balance += if (transaction.transactionType == "Income") amount else -amount
                    }
                }
            }
        }
        return balance
    }
    private fun getTransactionOnDate(date: String): List<Transaction> {
        return transactionDao!!.getAll().filter { it.date == date }
    }
    private fun getAllTransactions(): List<Transaction> {
        return transactionDao!!.getAll()
    }
    fun setBalance(balance: Balance) {
        balanceDao?.insertAll(balance)
        mainActivity?.getBalanceUntilDate(balance.date)
    }

    fun showTransactionOverview() {
        val intent = Intent(mainActivity, TransactionOverviewActivity::class.java)
        mainActivity?.startActivity(intent)
    }

    fun deleteTransaction(transactionId: Int) {
        val transaction = transactionDao?.getTransactionById(transactionId)
        Log.d("Transaction", transaction.toString())
        if (transaction != null) {
            val date = transaction.date
            transactionDao?.delete(transaction)

            transactionOverviewActivity?.let { setAdapter(getAllTransactions(), it) }
            mainActivity?.let { setAdapter(getTransactionOnDate(date!!), it) }
            displayBalanceOnDate(date!!)
        } else {
            // Handle the case where there is no transaction with the given ID
        }
    }

    fun refreshBalance() {
        val currentDate = mainActivity?.getDate(Date())
        mainActivity?.setBalance(getBalanceUntilDate(currentDate!!))
    }
}