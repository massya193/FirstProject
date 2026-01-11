package com.example.myfinancefinalproject

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.myfinancefinalproject.data.database.DatabaseProvider
import com.example.myfinancefinalproject.data.repository.FinanceRepository
import com.example.myfinancefinalproject.data.repository.UserRepository
import com.example.myfinancefinalproject.viewmodel.FinanceViewModel
import com.example.myfinancefinalproject.viewmodel.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.getValue

class IncomeBottomSheet (
    private val onSave: (amount: Long, category: String,note: String?, date:Long) -> Unit
) : BottomSheetDialogFragment() {
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
    private val viewModel: FinanceViewModel by activityViewModels { factory }
    override fun onCreateView(      // Эта функция создаёт и возвращает разметку (layout) панели
        inflater: LayoutInflater,   // Нужен, чтобы "надуть" XML-файл в реальный вид(выделение памяти)
        container: ViewGroup?,      // Родитель, в котором будет отображаться панель
        savedInstanceState: Bundle? // Сохранённое состояние (пока не нужно)
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_income, container, false)//подключение xml)
        val etAmount = view.findViewById<EditText>(R.id.etAmount)
        val etNote = view.findViewById<EditText>(R.id.etNote)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val etCategory = view.findViewById<AutoCompleteTextView>(R.id.etCategory)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            mutableListOf<String>()
        )
        etCategory.setAdapter(adapter)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.incomeCategories.collect { list ->
                    adapter.clear()
                    adapter.addAll(list)
                    adapter.notifyDataSetChanged()
                }
            }
        }
        etCategory.setOnClickListener { etCategory.showDropDown() }
        etCategory.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) etCategory.showDropDown()
        }
        Log.d("CAT", "observe categories for userId=$id")
        btnSave.setOnClickListener {
            val amountText = etAmount.text.toString()
            val amount = amountText.toLongOrNull() ?: 0L
            val note = etNote.text?.toString()
            val category = etCategory.text.toString().trim().uppercase()
            Log.d("CAT", "saving category='$category'")
            val date = System.currentTimeMillis()
            onSave(amount, category, note, date)
            dismiss()
        }
        return view
    }
}