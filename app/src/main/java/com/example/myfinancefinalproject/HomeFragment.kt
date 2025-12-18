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
import com.example.myfinancefinalproject.viewmodel.FinanceViewModel
import com.example.myfinancefinalproject.viewmodel.FinanceViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay


class HomeFragment : Fragment(R.layout.fragment_home) {
    private val viewModel: FinanceViewModel by viewModels {
        val userId=UserPreferences.getCurrentUser(requireContext())
        val db=DatabaseProvider.getDatabase(requireContext())
        FinanceViewModelFactory(
            FinanceRepository(
                db.balanceDao(),
                db.expenseDao(),
                db.incomeDao(),
                db.userDao()
            )
        )
    } // хуйня для вызова репозитория,для работы с ним


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val nickname = UserPreferences.getCurrentUser(requireContext())
        val Baltext=view.findViewById<TextView>(R.id.Baltext)
        val incval=view.findViewById<TextView>(R.id.incVal)
        val expval=view.findViewById<TextView>(R.id.expValue)
        viewModel.getUserIdByNickname(nickname.toString()) { userId ->
            if (userId == null) return@getUserIdByNickname
            viewModel.balance.observe(viewLifecycleOwner) { balance ->
                Baltext.text = balance.toString()
            }

            viewModel.expenses.observe(viewLifecycleOwner) { expenses ->
                expval.text = expenses.toString()
            }
            viewModel.income.observe(viewLifecycleOwner) { incomes ->
                incval.text = incomes.toString()
            }
            viewModel.loadBalance(userId)
            viewModel.observeExpense(userId)
            viewModel.observeIncome(userId)
            Log.d("DEBUG_USER", "Current userId = $userId")
            lifecycleScope.launch {
                viewModel.loadBalance(userId)
            }
        }
        val btnSpent = view.findViewById<Button>(R.id.buttonRemoveBalance)
        val btnIncome=view.findViewById<Button>(R.id.buttonAddIncome)
        val BalText=view.findViewById<TextView>(R.id.Baltext)
        val Extext=view.findViewById<TextView>(R.id.expValue)
        val Intext=view.findViewById<TextView>(R.id.incVal)
        btnSpent.setOnClickListener {
            // Создаём панель и передаём, что делать при сохранении
            val sheet = SpentBottomSheet { amount, category, note, data ->
                val currentbalance = viewModel.balance.value ?: 0.0
                if (currentbalance >= amount) {
                    viewModel.getUserIdByNickname(nickname.toString()) { userId ->
                        if (userId == null) return@getUserIdByNickname
                        viewModel.addExpense(userId, amount.toDouble(), category, data.toString())
                        lifecycleScope.launch {
                            viewModel.updateBalance(userId, -amount.toDouble())
                            viewModel.observeExpense(userId)
                                .observe(viewLifecycleOwner) { totalExpense ->
                                    Extext.text = totalExpense?.toString()
                                }
                            viewModel.observeBalance(userId).observe(viewLifecycleOwner) { total ->
                                BalText.text = total?.toString()
                            }
                        }
                    }
                }
                else{
                    Toast.makeText(requireContext(), "You dont have enought money", Toast.LENGTH_SHORT).show()
                }
                Toast.makeText(requireContext(), "Expenses: $amount₽  ($category)", Toast.LENGTH_SHORT).show()
            }
            // Показываем панель на экране
            sheet.show(parentFragmentManager, "spentBottomSheet")
        }
        btnIncome.setOnClickListener {
            // Создаём панель и передаём, что делать при сохранении
            val sheet = IncomeBottomSheet { amount, note, date ->
                viewModel.getUserIdByNickname(nickname.toString()) { userId ->
                    if (userId == null) return@getUserIdByNickname
                    viewModel.addIncome(userId, amount.toDouble(), date.toString())
                    lifecycleScope.launch {
                        viewModel.updateBalance(userId, +amount.toDouble())
                        viewModel.observeIncome(userId).observe(viewLifecycleOwner) { totalIncome ->
                            Log.d("TEST", "INCOME = $totalIncome")
                            Intext.text = totalIncome?.toString()
                        }
                        viewModel.observeBalance(userId).observe(viewLifecycleOwner) { total ->
                            BalText.text = total?.toString()
                        }
                    }
                }
                Toast.makeText(requireContext(), "Added to balance: $amount₽", Toast.LENGTH_SHORT)
                    .show()
            }
            // Показываем панель на экране
            sheet.show(parentFragmentManager, "incometBottomSheet")
        }
    }
}

