package hr.tvz.android.androidproject.view

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import hr.tvz.android.androidproject.R
import hr.tvz.android.androidproject.controller.MainController
import hr.tvz.android.androidproject.databinding.ActivityMainBinding
import hr.tvz.android.androidproject.databinding.DialogBalanceBinding
import hr.tvz.android.androidproject.model.Balance
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BalanceDialog(private val mainController: MainController) : DialogFragment() {
    private lateinit var binding: DialogBalanceBinding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogBalanceBinding.inflate(layoutInflater)
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            builder.setView(binding.root)
                .setPositiveButton(
                    R.string.ok,
                    DialogInterface.OnClickListener { dialog, id ->
                        val balanceEditText = binding.balance
                        val balanceValue = balanceEditText.text.toString().toDoubleOrNull()
                        if (balanceValue != null) {
                            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            val currentDate = sdf.format(Date())
                            val balance = Balance(
                                current_balance = balanceValue,
                                date = currentDate
                            )
                            mainController.setBalance(balance)
                        } else {
                            // handle invalid balance value
                        }
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}