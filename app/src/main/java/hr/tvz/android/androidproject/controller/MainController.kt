package hr.tvz.android.androidproject.controller

import android.content.Intent
import java.text.SimpleDateFormat
import hr.tvz.android.androidproject.view.MainActivity
import hr.tvz.android.androidproject.view.NewTransactionActivity
import java.util.Locale
import java.util.*

class MainController() {
    private var mainActivity: MainActivity? = null
    private var newTransactionActivity: NewTransactionActivity? = null

    constructor(mainActivity: MainActivity) : this() {
        this.mainActivity = mainActivity
    }

    constructor(newTransactionActivity: NewTransactionActivity) : this() {
        this.newTransactionActivity = newTransactionActivity
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
}