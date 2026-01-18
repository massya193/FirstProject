package com.example.myfinancefinalproject.ReportGraphics

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import androidx.recyclerview.widget.RecyclerView
import com.example.myfinancefinalproject.R
import com.example.myfinancefinalproject.data.dto.IncomeDaySum
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfinancefinalproject.data.entity.Income

class IncomeChartFragment : Fragment(R.layout.fragment_chart_income) {

    private val sharedVm: ReportSharedViewModel by viewModels({ requireParentFragment() })
    private val financeVm: FinanceViewModel by activityViewModels()

    private val incomeLiveData: LiveData<List<IncomeDaySum>> by lazy {
        sharedVm.rangeLiveData.switchMap { range ->
            financeVm.observeIncomeChart(range.from, range.to)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chart = view.findViewById<BarChart>(R.id.barChartIncome)
        setupChart(chart)
        incomeLiveData.observe(viewLifecycleOwner) { list ->
            val entries = list.mapIndexed { index, item ->
                BarEntry(index.toFloat(), item.total.toFloat())
            }
            val dataSet = BarDataSet(entries, "Income").apply {
                color = Color.parseColor("#3AFF6B") // можешь заменить под стиль
                valueTextColor = Color.parseColor("#9AA3B2")
                valueTextSize = 10f
            }
            chart.data = BarData(dataSet).apply {
                barWidth = 0.7f
            }
            val days = list.map { it.dayStart }
            chart.xAxis.valueFormatter = object : ValueFormatter() {
                private val sdf = SimpleDateFormat("dd MMM", Locale("en"))
                override fun getFormattedValue(value: Float): String {
                    val i = value.toInt()
                    return if (i in days.indices) sdf.format(Date(days[i])) else ""
                }
            }

            chart.invalidate()
        }
        val rv = view.findViewById<RecyclerView>(R.id.rvIncomeChanges)
        val listAdapter = IncomeChangesAdapter()
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = listAdapter

        val incomeTransactionsLiveData: LiveData<List<Income>> by lazy {
            sharedVm.rangeLiveData.switchMap { range ->
                financeVm.observeIncomeTransactions(range.from, range.to)
            }
        }

        incomeTransactionsLiveData.observe(viewLifecycleOwner) { list ->
            val ui = list.map { inc ->
                IncomeChangeUi(
                    date = inc.date,
                    amount = inc.amount,
                    category = inc.category ?: "Income"
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
