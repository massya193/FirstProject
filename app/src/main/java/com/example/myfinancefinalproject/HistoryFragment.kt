package com.example.myfinancefinalproject

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfinancefinalproject.HistoryClasses.Operation
import com.example.myfinancefinalproject.HistoryClasses.OperationType
import com.example.myfinancefinalproject.HistoryClasses.OperationsAdapter
import com.example.myfinancefinalproject.data.database.DatabaseProvider
import com.example.myfinancefinalproject.data.repository.FinanceRepository
import com.example.myfinancefinalproject.data.repository.UserRepository
import com.example.myfinancefinalproject.databinding.FragmentHistoryBinding
import com.example.myfinancefinalproject.viewmodel.FinanceViewModel
import com.example.myfinancefinalproject.viewmodel.ViewModelFactory


class HistoryFragment : Fragment(R.layout.fragment_history) {
    private val factory by lazy {
        val db = DatabaseProvider.getDatabase(requireContext())
        ViewModelFactory(
            userRepository = UserRepository(db.userDao()),
            financeRepository = FinanceRepository(
                db.balanceDao(),
                db.expenseDao(),
                db.incomeDao(),
            )
        )
    }
    private val viewModel: FinanceViewModel by viewModels { factory }
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val items = mutableListOf<Operation>()
    private lateinit var adapter: OperationsAdapter
    private var lastIncomeCount=0.0
    private var lastExpenseCount=0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHistoryBinding.bind(view)
        setupRecycler()
        viewModel.history.observe(viewLifecycleOwner) { list ->
            items.clear()
            items.addAll(list)
            adapter.notifyDataSetChanged()
        }
        viewModel.expenseCount.observe(viewLifecycleOwner) { expense->
            lastExpenseCount = expense
            updateBars()
        }
        viewModel.incomeCount.observe(viewLifecycleOwner) { income->
            lastIncomeCount = income
            updateBars()
        }
    }
    private fun setupRecycler() {
        adapter = OperationsAdapter(items)
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun animateProgress(pb: ProgressBar, tv: TextView, target: Int) {
        val safe = target.coerceIn(0, 100)
        ObjectAnimator.ofInt(pb, "progress", pb.progress, safe).apply {
            duration = 700
            start()
        }

        tv.text = "$safe%"
    }
    private fun updateBars() {
        val income = lastIncomeCount
        val expense = lastExpenseCount

        if (income <= 0.0) {
            animateProgress(binding.pbIncome, binding.tvIncomePercent, 0)
            animateProgress(binding.pbExpenses, binding.tvExpensePercent, 0)
            return
        }

        val expensePercent = ((expense / income) * 100).toInt().coerceIn(0, 100)
        val remainPercent = 100 - expensePercent

        animateProgress(binding.pbExpenses, binding.tvExpensePercent, expensePercent)
        animateProgress(binding.pbIncome, binding.tvIncomePercent, remainPercent)
    }


    //TODO ДЕЛАЙ 3 СТРАНИЦА БРООООО
}

