package com.example.myfinancefinalproject.HistoryClasses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myfinancefinalproject.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OperationsAdapter(
    private val items: List<Operation>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_EXPENSE = 0
        private const val TYPE_INCOME = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position].type == OperationType.INCOME)
            TYPE_INCOME
        else
            TYPE_EXPENSE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return if (viewType == TYPE_EXPENSE) {
            val view = inflater.inflate(
                R.layout.item_operation_expense,
                parent,
                false
            )
            ExpenseViewHolder(view)
        } else {
            val view = inflater.inflate(
                R.layout.item_operation_income,
                parent,
                false
            )
            IncomeViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]

        when (holder) {
            is ExpenseViewHolder -> holder.bind(item)
            is IncomeViewHolder -> holder.bind(item)
        }
    }

    override fun getItemCount(): Int = items.size

    // ---------- EXPENSE ----------
    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        private val tvComment: TextView = itemView.findViewById(R.id.tvComment)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)

        fun bind(item: Operation) {
            tvAmount.text = "- ${item.amount}"
            tvCategory.text = item.category
            tvDate.text = formatDate(item.date)

            if (item.comment.isNullOrEmpty()) {
                tvComment.visibility = View.GONE
            } else {
                tvComment.visibility = View.VISIBLE
                tvComment.text = item.comment
            }
        }
    }

    // ---------- INCOME ----------
    class IncomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)

        fun bind(item: Operation) {
            tvAmount.text = "+ ${item.amount}"
            tvCategory.text = item.category
            tvDate.text = formatDate(item.date)
        }
    }
}

/* -------- util -------- */
private fun formatDate(time: Long): String {
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return sdf.format(Date(time))
}
