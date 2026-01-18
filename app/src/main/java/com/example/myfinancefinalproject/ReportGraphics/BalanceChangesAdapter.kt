package com.example.myfinancefinalproject.ReportGraphics


import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myfinancefinalproject.R
import java.text.SimpleDateFormat
import java.util.*
class BalanceChangesAdapter : RecyclerView.Adapter<BalanceChangesAdapter.VH>() {

    private val items = mutableListOf<BalanceChangeUi>()
    private val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale("ru"))

    fun submit(list: List<BalanceChangeUi>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }
    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvDelta: TextView = itemView.findViewById(R.id.tvDelta)
        val tvBalanceAfter: TextView = itemView.findViewById(R.id.tvBalanceAfter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_balance_change, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.tvDate.text = sdf.format(Date(item.date))

        val sign = if (item.delta >= 0) "+" else ""
        holder.tvDelta.text = "Opperation: $sign${item.delta}"

        holder.tvBalanceAfter.text = "Balance: ${item.balanceAfter}"
    }

    override fun getItemCount(): Int = items.size
}
