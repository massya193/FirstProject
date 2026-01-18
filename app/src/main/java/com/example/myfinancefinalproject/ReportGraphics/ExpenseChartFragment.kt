package com.example.myfinancefinalproject.ReportGraphics

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfinancefinalproject.R
import com.example.myfinancefinalproject.data.dto.ExpenseDaySum
import com.example.myfinancefinalproject.data.entity.Expense
import com.example.myfinancefinalproject.viewmodel.FinanceViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class ExpenseChartFragment : Fragment(R.layout.fragment_chart_expense) {

    private val sharedVm: ReportSharedViewModel by viewModels({ requireParentFragment() })
    private val financeVm: FinanceViewModel by activityViewModels()

    private val expenseChartLiveData: LiveData<List<ExpenseDaySum>> by lazy {
        sharedVm.rangeLiveData.switchMap { range ->
            financeVm.observeExpenseChart(range.from, range.to)
        }
    }

    private val expenseTransactionsLiveData: LiveData<List<Expense>> by lazy {
        sharedVm.rangeLiveData.switchMap { range ->
            financeVm.observeExpenseTransactions(range.from, range.to)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chart = view.findViewById<BarChart>(R.id.barChartExpense)
        setupChart(chart)

        val rv = view.findViewById<RecyclerView>(R.id.rvExpenseChanges)
        val listAdapter = ExpenseChangesAdapter()
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = listAdapter

        // --- график ---
        expenseChartLiveData.observe(viewLifecycleOwner) { list ->
            val days = list.map { it.dayStart }
            val entries = list.mapIndexed { index, item ->
                // если total отрицательный — рисуем величину расходов
                BarEntry(index.toFloat(), abs(item.total).toFloat())
            }

            val dataSet = BarDataSet(entries, "Расходы").apply {
                color = Color.parseColor("#FF5A5A")
                valueTextColor = Color.parseColor("#9AA3B2")
                valueTextSize = 10f
            }

            chart.data = BarData(dataSet).apply { barWidth = 0.7f }

            chart.xAxis.valueFormatter = object : ValueFormatter() {
                private val sdf = SimpleDateFormat("dd MMM", Locale("ru"))
                override fun getFormattedValue(value: Float): String {
                    val i = value.toInt()
                    return if (i in days.indices) sdf.format(Date(days[i])) else ""
                }
            }

            chart.invalidate()
        }

        // --- список операций ---
        expenseTransactionsLiveData.observe(viewLifecycleOwner) { list ->
            val ui = list.map { ex ->
                ExpenseChangeUi(
                    date = ex.date,
                    amount = abs(ex.amount),
                    category = ex.category ?: "Расход"
                )
            }
            listAdapter.submit(ui)
        }
    }

    private fun setupChart(chart: BarChart) {
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.axisRight.isEnabled = false

        chart.axisLeft.apply {
            isEnabled = true
            textColor = Color.parseColor("#9AA3B2")
            gridColor = Color.parseColor("#2A2F3A")
        }

        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            textColor = Color.parseColor("#9AA3B2")
            setDrawGridLines(false)
            granularity = 1f
            isGranularityEnabled = true
        }

        chart.setTouchEnabled(true)
        chart.setPinchZoom(true)
        chart.setScaleEnabled(true)
    }
}
