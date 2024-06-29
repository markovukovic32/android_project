package hr.tvz.android.androidproject.view

import TransactionAdapter
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import hr.tvz.android.androidproject.controller.MainController
import hr.tvz.android.androidproject.databinding.ActivityTransactionOverviewBinding

class TransactionOverviewActivity: AppCompatActivity() {
    private lateinit var mainController : MainController
    private lateinit var binding: ActivityTransactionOverviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        mainController = MainController(this)
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionOverviewBinding.inflate(layoutInflater)
        val view = binding.root
        mainController.initializeAdapter()
        setContentView(view)
    }
    fun setAdapter(transactionAdapter: TransactionAdapter) {
        binding.transactionRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.transactionRecyclerView.adapter = transactionAdapter
    }
}