import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hr.tvz.android.androidproject.R
import hr.tvz.android.androidproject.model.Transaction

class TransactionAdapter(private val transactions: List<Transaction>) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name)
        val amount: TextView = view.findViewById(R.id.amount)
        val date: TextView = view.findViewById(R.id.date)
        val type: TextView = view.findViewById(R.id.type)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.name.text = transaction.description

        // Check if the transaction type is "Expense"
        if (transaction.transactionType == "Expense") {
            holder.amount.text = "-${transaction.amount} EUR"
        } else {
            holder.amount.text = "${transaction.amount} EUR"
        }

        holder.date.text = transaction.date
        holder.type.text = transaction.transactionType

        if (transaction.transactionType == "Income") {
            holder.itemView.setBackgroundColor(Color.argb(255, 200, 255, 200)) // Light green
        } else {
            holder.itemView.setBackgroundColor(Color.argb(255, 255, 200, 200)) // Light red
        }
    }

    override fun getItemCount() = transactions.size
}