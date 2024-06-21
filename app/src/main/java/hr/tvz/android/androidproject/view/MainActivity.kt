package hr.tvz.android.androidproject.view

import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import hr.tvz.android.androidproject.R
import hr.tvz.android.androidproject.controller.MainController
import hr.tvz.android.androidproject.databinding.ActivityMainBinding
import hr.tvz.android.androidproject.databinding.ActivityNewTransactionBinding

class MainActivity : AppCompatActivity() {
    private lateinit var mainController : MainController
    private lateinit var binding: ActivityMainBinding
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
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            // The month is 0-indexed, so you may need to add 1 to get the correct month
            val selectedDate = "$dayOfMonth/${month + 1}/$year"
            onDateSelected(selectedDate)
        }
        setContentView(view)
    }

    private fun onDateSelected(date: String) {
        // Handle the selected date here
        // You can call a method in your controller to handle the selected date
        mainController.onDateSelected(date)
    }
    fun updateDateTextView(date: String) {
        val textView: TextView = binding.datum
        textView.text = date
    }
    fun addTransaction(view: View) {
        mainController.addTransaction()
    }
}