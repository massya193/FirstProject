package com.example.myfinancefinalproject.ReportGraphics
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.switchMap
import com.example.myfinancefinalproject.R
import com.example.myfinancefinalproject.viewmodel.FinanceViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class BalanceChartFragment : Fragment(R.layout.fragment_chart_balance) {

    private val sharedVm: ReportSharedViewModel by viewModels({ requireParentFragment() })
    private val financeVm: FinanceViewModel by viewModels({ requireParentFragment() })

    private val balanceLiveData by lazy {
        sharedVm.rangeLiveData.switchMap { range ->
            financeVm.observeBalancePoints(range.from, range.to)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val chart = view.findViewById<LineChart>(R.id.lineChart)
        balanceLiveData.observe(viewLifecycleOwner) { pair ->
            val entries = pair.first
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
        }
    }
}

