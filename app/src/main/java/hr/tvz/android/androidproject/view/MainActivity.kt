package hr.tvz.android.androidproject.view

import TransactionAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CalendarView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import hr.tvz.android.androidproject.controller.MainController
import hr.tvz.android.androidproject.databinding.ActivityMainBinding
import hr.tvz.android.androidproject.model.AppDatabase
import hr.tvz.android.androidproject.model.Balance
import hr.tvz.android.androidproject.model.BalanceDao
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var mainController : MainController
    private lateinit var binding: ActivityMainBinding
    private var db: AppDatabase? = null
    private var balanceDao: BalanceDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        mainController = MainController(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val calendarView: CalendarView = binding.calendarView
        calendarView.date = System.currentTimeMillis()
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val selectedDate = "$dayOfMonth/${month + 1}/$year"
            onDateSelected(selectedDate)
        }
        onDateSelected(getDate(Date()))
        setContentView(view)
        initializeDatabase()
        val balance = balanceDao?.getAll()
        if (balance != null) {
            if(balance.isEmpty()){
                val dialog = BalanceDialog(mainController)
                dialog.show(supportFragmentManager, "BalanceDialog")
            }
            else{
                val currentDate = getDate(Date())
                setBalance(getBalanceUntilDate(currentDate))
            }
        }
    }
    override fun onResume() {
        super.onResume()
        onDateSelected(getDate(Date()))
        mainController.refreshBalance()
    }
    fun getDate(date: Date): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(date)
    }

    fun getBalanceUntilDate(date: String): Balance {
        return mainController.getBalanceUntilDate(date)
    }


    private fun onDateSelected(date: String) {
        mainController.onDateSelected(date)
    }
    fun updateDateTextView(date: String) {
        val textView: TextView = binding.datum
        textView.text = date
    }
    fun addTransaction(view: View) {
        mainController.addTransaction()
    }
    private fun initializeDatabase() {
        val db = Room.databaseBuilder(
            this.applicationContext,
            AppDatabase::class.java, "transaction-database"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
        this.db = db
        this.balanceDao = db.balanceDao()
    }

    fun setBalance(balance: Balance) {
        binding.balance.text = "Current balance: " + balance.current_balance + " EUR"
    }

    fun setAdapter(transactionAdapter: TransactionAdapter) {
        binding.transactionRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.transactionRecyclerView.adapter = transactionAdapter
    }

    fun showOverview(view: View) {
        mainController.showTransactionOverview()
    }
}