package com.example.myfinancefinalproject.ReportGraphics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myfinancefinalproject.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class IncomeChangesAdapter : RecyclerView.Adapter<IncomeChangesAdapter.VH>() {

    private val items = mutableListOf<IncomeChangeUi>()
    private val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale("ru"))

    fun submit(list: List<IncomeChangeUi>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_income_change, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.tvDate.text = sdf.format(Date(item.date))
        holder.tvAmount.text = "+${item.amount}"
        holder.tvCategory.text = "Категория: ${item.category}"
    }

    override fun getItemCount(): Int = items.size
}
