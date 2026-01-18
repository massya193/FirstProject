package com.example.myfinancefinalproject.ReportGraphics

import android.content.Context
import android.widget.TextView
import com.example.myfinancefinalproject.R
import com.example.myfinancefinalproject.data.dto.BalanceEvent
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionMarkerView(
    context: Context,
    private val chart: Chart<*>
) : MarkerView(context, R.layout.marker_transaction) {

    private val tvDate = findViewById<TextView>(R.id.tvDate)
    private val tvDelta = findViewById<TextView>(R.id.tvDelta)
    private val tvBalance = findViewById<TextView>(R.id.tvBalance)
    private val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale("ru"))

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        if (e != null) {
            val event = e.data as? BalanceEvent
            if (event != null) {
                tvDate.text = sdf.format(Date(event.date))
                val sign = if (event.delta >= 0) "+" else ""
                tvDelta.text = "Opperation: $sign${event.delta}"
                tvBalance.text = "Balance: ${e.y}"
            }
        }
        super.refreshContent(e, highlight)
    }
    override fun getOffsetForDrawingAtPoint(posX: Float, posY: Float): MPPointF {
        var offsetX = -(width / 2f)
        var offsetY = -height.toFloat()

        if (posX + offsetX < 0f) {
            offsetX = -posX
        } else if (posX + width + offsetX > chart.width) {
            offsetX = chart.width - posX - width
        }

        if (posY + offsetY < 0f) {
            offsetY = 0f
        } else if (posY + height + offsetY > chart.height) {
            offsetY = chart.height - posY - height
        }
        return MPPointF(offsetX, offsetY)
    }
}
