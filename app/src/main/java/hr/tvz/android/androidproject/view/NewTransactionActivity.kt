package hr.tvz.android.androidproject.view

import android.os.Bundle
import android.widget.CalendarView
import android.widget.CompoundButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import hr.tvz.android.androidproject.R
import hr.tvz.android.androidproject.controller.MainController
import hr.tvz.android.androidproject.databinding.ActivityNewTransactionBinding

class NewTransactionActivity : AppCompatActivity() {
    private lateinit var mainController : MainController
    private lateinit var binding: ActivityNewTransactionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        mainController = MainController(this)
        super.onCreate(savedInstanceState)
        binding = ActivityNewTransactionBinding.inflate(layoutInflater)
        val view = binding.root
        binding.type.text = "Income "
        binding.type.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            binding.type.text = if (isChecked) "Expense" else "Income"
        }
        setContentView(view)
    }

}