package hr.tvz.android.androidproject.controller

import TransactionAdapter
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import hr.tvz.android.androidproject.model.AppDatabase
import hr.tvz.android.androidproject.model.Balance
import hr.tvz.android.androidproject.model.BalanceDao
import hr.tvz.android.androidproject.model.Transaction
import hr.tvz.android.androidproject.model.TransactionDao
import java.text.SimpleDateFormat
import hr.tvz.android.androidproject.view.MainActivity
import hr.tvz.android.androidproject.view.NewTransactionActivity
import hr.tvz.android.androidproject.view.TransactionOverviewActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.*
import kotlin.collections.ArrayList

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
    private fun displayBalanceOnDate(newDate: String) {
        mainActivity?.updateDateTextView("Balance on " + newDate + " is " + getBalanceUntilDate(newDate).current_balance + "EUR.")
    }
    fun initializeAdapter(){
        val transactions = getAllTransactions()
        transactionOverviewActivity?.let { setAdapter(transactions, it) }
    }
    private fun setAdapter(transactions: List<Transaction>, activity: AppCompatActivity) {
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
        if(endDate!!.before(balanceDate)){
            return balance
        }

        transactions?.forEach { transaction ->
            var transactionDate = sdf.parse(transaction.date!!)
            transactionDate?.let {
                if (it.after(balanceDate) && !it.after(endDate) && transaction.frequency == "Only once") {
                    transaction.amount?.let { amount ->
                            balance.current_balance += if (transaction.transactionType == "Income") amount else -amount
                    }
                }
                else{
                    val calendar = Calendar.getInstance()
                    calendar.time = transactionDate!!
                    if(transaction.frequency == "Once a week"){
                        while(transactionDate!!.before(balanceDate)){
                            calendar.add(Calendar.WEEK_OF_YEAR, 1)
                            transactionDate = calendar.time
                        }
                        val daysDifference = ((endDate.time - transactionDate!!.time) / (1000 * 60 * 60 * 24)).toInt()
                        if(daysDifference < 0){
                            return@forEach
                        }

                        val weeksDifference = daysDifference / 7 + 1
                        transaction.amount?.let { amount ->
                            balance.current_balance += if (transaction.transactionType == "Income") amount * weeksDifference else -amount * weeksDifference
                        }
                    }
                    else if(transaction.frequency == "Once a month"){
                        val endCalendar = Calendar.getInstance()
                        endCalendar.time = endDate

                        while(transactionDate!!.before(balanceDate)){
                            calendar.add(Calendar.MONTH, 1)
                            transactionDate = calendar.time
                        }

                        val monthsBetween = when {
                            calendar.get(Calendar.YEAR) == endCalendar.get(Calendar.YEAR) -> endCalendar.get(Calendar.MONTH) - calendar.get(Calendar.MONTH)
                            else -> 12 * (endCalendar.get(Calendar.YEAR) - calendar.get(Calendar.YEAR)) + endCalendar.get(Calendar.MONTH) - calendar.get(Calendar.MONTH)
                        } + 1

                        if(monthsBetween < 0){
                            return@forEach
                        }

                        transaction.amount?.let { amount ->
                            balance.current_balance += if (transaction.transactionType == "Income") amount * monthsBetween else -amount * monthsBetween
                        }
                    }
                }
            }
        }
        return balance
    }
    private fun getTransactionOnDate(date: String): List<Transaction> {
        val transactions = ArrayList<Transaction>()
        for(transaction in transactionDao!!.getAll()){
            if(transaction.frequency == "Only once"){
                if(transaction.date == date){
                    transactions.add(transaction)
                }
            }
            else{
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val transactionDate = sdf.parse(transaction.date!!)
                val wantedDate = sdf.parse(date)

                val cal = Calendar.getInstance()
                if (transactionDate != null) {
                    cal.time = transactionDate
                }

                val calWanted = Calendar.getInstance()
                if (wantedDate != null) {
                    calWanted.time = wantedDate
                }

                if(transaction.frequency == "Once a week" && !wantedDate!!.before(transactionDate)){
                    if(cal.get(Calendar.DAY_OF_WEEK) == calWanted.get(Calendar.DAY_OF_WEEK) && cal.before(calWanted)){
                        transactions.add(transaction)
                    }
                }
                else if(transaction.frequency == "Once a month" && !wantedDate!!.before(transactionDate)){
                    val lastDayOfWantedMonth= calWanted.getActualMaximum(Calendar.DAY_OF_MONTH)
                    if(cal.get(Calendar.DAY_OF_MONTH) == calWanted.get(Calendar.DAY_OF_MONTH) || ((cal.get(Calendar.DAY_OF_MONTH) > lastDayOfWantedMonth) && (calWanted.get(Calendar.DAY_OF_MONTH) == lastDayOfWantedMonth))){
                        if(cal.before(calWanted))
                            transactions.add(transaction)
                    }
                }
            }
        }
        return transactions
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
        }
    }

    fun refreshBalance() {
        val currentDate = mainActivity?.getDate(Date())
        mainActivity?.setBalance(getBalanceUntilDate(currentDate!!))
    }
    fun setUpGraphView() {
        CoroutineScope(Dispatchers.Default).launch {
            val dataPoints = mutableListOf<DataPoint>()
            val calendar = Calendar.getInstance()

            dataPoints.add(DataPoint(Date(), getBalanceUntilDate(mainActivity!!.getDate(Date())).current_balance))

            for (i in 1..180) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                val newDate = calendar.time
                dataPoints.add(DataPoint(newDate, getBalanceUntilDate(mainActivity!!.getDate(newDate)).current_balance))
            }

            val series = LineGraphSeries(dataPoints.toTypedArray())

            withContext(Dispatchers.Main) {
                mainActivity!!.setUpGraphView(series)
            }
        }
    }
}