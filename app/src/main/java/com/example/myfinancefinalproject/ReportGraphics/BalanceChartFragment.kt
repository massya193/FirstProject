package com.example.myfinancefinalproject.ReportGraphics
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfinancefinalproject.R
import com.example.myfinancefinalproject.data.dto.BalanceEvent
import com.example.myfinancefinalproject.viewmodel.FinanceViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class BalanceChartFragment : Fragment(R.layout.fragment_chart_balance) {

    private val sharedVm: ReportSharedViewModel by viewModels({ requireParentFragment() })
    private val financeVm: FinanceViewModel by activityViewModels()
    private val balanceLiveData by lazy {
        sharedVm.rangeLiveData.switchMap { range ->
            financeVm.observeBalancePoints(range.from, range.to)
        }
    }
    private val balanceEventsLiveData: LiveData<List<BalanceEvent>> by lazy {
        sharedVm.rangeLiveData.switchMap { range ->
            financeVm.observeBalanceEvents(range.from, range.to) // нужно сделать/добавить
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rv = view.findViewById<RecyclerView>(R.id.rvBalanceChanges)
        val adapter = BalanceChangesAdapter()
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter
        rv.setHasFixedSize(true)
        val chart = view.findViewById<LineChart>(R.id.lineChart)
        chart.axisLeft.apply {
            isEnabled = true
            textColor = Color.GRAY
            textSize = 12f
        }
        chart.axisRight.isEnabled=false
        val marker= TransactionMarkerView(requireContext(),chart)
        chart.isHighlightPerTapEnabled=true
        chart.setTouchEnabled(true)
        chart.marker=marker
        balanceLiveData.observe(viewLifecycleOwner) { (entries, dates) ->
            val dataSet = LineDataSet(entries, "Баланс").apply {
                mode = LineDataSet.Mode.STEPPED
                setDrawCircles(true)
                circleRadius = 3.5f
                setDrawValues(false)
                lineWidth = 2.5f
                color = Color.GREEN
                setCircleColor(Color.GREEN)
            }
            chart.data = LineData(dataSet)
            chart.invalidate()

            val changes = entries.map { entry ->
                val e = entry.data as BalanceEvent
                BalanceChangeUi(
                    date = e.date,
                    delta = e.delta,
                    balanceAfter = entry.y.toDouble()
                )
            }.asReversed()
            Log.d("LIST", "entries=${entries.size}, changes=${changes.size}")
            adapter.submit(changes)
        }

    }
}

