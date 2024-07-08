package hr.tvz.android.androidproject.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CalendarView
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import hr.tvz.android.androidproject.controller.MainController
import hr.tvz.android.androidproject.databinding.ActivityNewTransactionBinding
import hr.tvz.android.androidproject.model.Transaction
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NewTransactionActivity : AppCompatActivity() {
    private lateinit var mainController : MainController
    private lateinit var binding: ActivityNewTransactionBinding
    private var date: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        mainController = MainController(this)
        super.onCreate(savedInstanceState)
        binding = ActivityNewTransactionBinding.inflate(layoutInflater)
        val view = binding.root
        binding.type.text = "Income "
        binding.type.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            binding.type.text = if (isChecked) "Expense" else "Income"
        }
        val calendarView: CalendarView = binding.date
        date = System.currentTimeMillis()
        calendarView.date = date
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val c = Calendar.getInstance()
            c[year, month] = dayOfMonth
            date = c.getTimeInMillis()
        }
        setContentView(view)
    }

    fun addTransaction(view: View) {
        val transactionName = binding.transactionName.text.toString()
        val amount = binding.transactionAmount.text.toString().toDoubleOrNull()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = sdf.format(calendar.time)
        val transactionType = if (binding.type.isChecked) "Expense" else "Income"
        val frequency = binding.frequency.selectedItem.toString()
        val transaction = Transaction(
            description = transactionName,
            transactionType = transactionType,
            date = date,
            frequency = frequency,
            amount = amount
        )
        mainController.addTransactionToDatabase(transaction)
        mainController.refreshBalance()
    }
}