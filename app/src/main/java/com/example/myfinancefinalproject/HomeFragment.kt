package com.example.myfinancefinalproject

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.myfinancefinalproject.data.database.DatabaseProvider
import com.example.myfinancefinalproject.data.repository.FinanceRepository
import com.example.myfinancefinalproject.data.repository.UserRepository
import com.example.myfinancefinalproject.viewmodel.FinanceViewModel
import com.example.myfinancefinalproject.viewmodel.FinanceViewModelFactory
import com.example.myfinancefinalproject.viewmodel.UserViewModel
import com.example.myfinancefinalproject.viewmodel.ViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay


class HomeFragment : Fragment(R.layout.fragment_home) {

    // ---------- FACTORY ----------
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

    private val userViewModel: UserViewModel by viewModels { factory }
    private val financeViewModel: FinanceViewModel by viewModels { factory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentUserId = UserPreferences.getUserId(requireContext())
        if (currentUserId != null) {
            userViewModel.loadUserById(currentUserId)
        }
        val balText = view.findViewById<TextView>(R.id.Baltext)
        val incText = view.findViewById<TextView>(R.id.incVal)
        val expText = view.findViewById<TextView>(R.id.expValue)
        val btnIncome = view.findViewById<Button>(R.id.buttonAddIncome)
        val btnExpense = view.findViewById<Button>(R.id.buttonRemoveBalance)
        userViewModel.user.observe(viewLifecycleOwner) { user ->
            financeViewModel.setUserId(user.userId)
        }
        financeViewModel.balance.observe(viewLifecycleOwner) {
            balText.text = it.toString()
        }
        lifecycleScope.launchWhenStarted {
            financeViewModel.income.collect { list ->
                incText.text = list.sumOf { it.amount }.toString()
            }
        }

        lifecycleScope.launchWhenStarted {
            financeViewModel.expenses.collect { list ->
                expText.text = list.sumOf { it.amount }.toString()
            }
        }

        btnIncome.setOnClickListener {
            val sheet = IncomeBottomSheet { amount, note, date ->
                financeViewModel.addIncome(amount.toDouble(), date)
            }
            sheet.show(parentFragmentManager, "incomeBottomSheet")
        }

        btnExpense.setOnClickListener {
            val sheet = SpentBottomSheet { amount, category, note, date ->
                financeViewModel.addExpense(amount.toDouble(), category, date)
            }
            sheet.show(parentFragmentManager, "expenseBottomSheet")
        }
            //TODO НЕ РАБОТАЮТ КНОПКИ ДОБАВЛЕНИЯ И УБЫТКОВ,НЕ МЕНЯЮТСЯ ЗНАЧЕНИЯ ЕСЛИ БЫТЬ ТОЧНЕЕ
    }
}


