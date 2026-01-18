package com.example.myfinancefinalproject

import android.R.color.white
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.myfinancefinalproject.ReportGraphics.BalanceChartFragment
import com.example.myfinancefinalproject.ReportGraphics.ExpenseChartFragment
import com.example.myfinancefinalproject.ReportGraphics.IncomeChartFragment
import com.example.myfinancefinalproject.ReportGraphics.ReportSharedViewModel
import com.example.myfinancefinalproject.data.database.DatabaseProvider
import com.example.myfinancefinalproject.data.repository.FinanceRepository
import com.example.myfinancefinalproject.data.repository.UserRepository
import com.example.myfinancefinalproject.viewmodel.FinanceViewModel
import com.example.myfinancefinalproject.viewmodel.ViewModelFactory

class ReportFragment : Fragment(R.layout.fragment_report) {
    private val sharedVm: ReportSharedViewModel by viewModels()//общий диапозон для дат
    private val factory by lazy {
        val db = DatabaseProvider.getDatabase(requireContext())
        ViewModelFactory(
            userRepository = UserRepository(db.userDao()),
            financeRepository = FinanceRepository(
                incomeDao = db.incomeDao(),
                expenseDao = db.expenseDao(),
                balanceDao = db.balanceDao()
            )
        )
    }
    private val financeVm: FinanceViewModel by activityViewModels { factory }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnBalance = view.findViewById<TextView>(R.id.btnBalance)
        val btnIncome = view.findViewById<TextView>(R.id.btnIncome)
        val btnExpense = view.findViewById<TextView>(R.id.btnExpense)
        if (savedInstanceState == null) {
            showChart(BalanceChartFragment())
            selectTab(btnBalance, listOf(btnIncome, btnExpense))
        }
        btnBalance.setOnClickListener {
            showChart(BalanceChartFragment())
            selectTab(btnBalance, listOf(btnIncome, btnExpense))
        }
        btnIncome.setOnClickListener {
            showChart(IncomeChartFragment())
            selectTab(btnIncome, listOf(btnBalance, btnExpense))
        }
        btnExpense.setOnClickListener {
            showChart(ExpenseChartFragment())
            selectTab(btnExpense, listOf(btnBalance, btnIncome))
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun selectTab(selected: TextView, others: List<TextView>) {
        selected.setBackgroundResource(R.drawable.bg_segment_selected)
        selected.setTextColor(Color.WHITE)
        for (other in others) {
            other.setBackgroundResource(R.drawable.bg_segment_unselected)
            other.setTextColor(Color.parseColor("#9AA3B2"))
        }
    }
    private fun showChart(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.chartContainer, fragment)
            .commit()
    }
}
