package com.example.myfinancefinalproject

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.AutoCompleteTextView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment



class SpentBottomSheet(
    private val onSave: (amount: Long, category: String, note: String?,date: String) -> Unit
) : BottomSheetDialogFragment() {
    override fun onCreateView(      // Эта функция создаёт и возвращает разметку (layout) панели
        inflater: LayoutInflater,   // Нужен, чтобы "надуть" XML-файл в реальный вид
        container: ViewGroup?,      // Родитель, в котором будет отображаться панель
        savedInstanceState: Bundle? // Сохранённое состояние (пока не нужно)
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_spent, container, false)//подключение xml
        val etAmount = view.findViewById<EditText>(R.id.etAmount)
        val etCategory = view.findViewById<AutoCompleteTextView>(R.id.etCategory)
        val etNote = view.findViewById<EditText>(R.id.etNote)
        val etdate=view.findViewById<EditText>(R.id.etDate)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        btnSave.setOnClickListener {
            val amountText = etAmount.text.toString()
            val amount = amountText.toLongOrNull() ?: 0L // если пользователь ничего не ввёл — будет 0
            val category = etCategory.text.toString()
            val note = etNote.text?.toString()
            val date=etdate.text.toString()
            onSave(amount, category, note,date)
            dismiss()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val autoCompleteTextView = // то самое предложение категорий при вводе(как в поиске браузера)
            view.findViewById<AutoCompleteTextView>(R.id.etCategory)
        val categories = listOf("Food", "Transport", "Health", "Entertainments","Clothes", "Other")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            categories
        )
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.threshold = 0 //количество символов для ввода чтобы появился список
        autoCompleteTextView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) autoCompleteTextView.showDropDown()
        }
        autoCompleteTextView.setOnClickListener {
            autoCompleteTextView.showDropDown()
        }
        val date=view.findViewById<EditText>(R.id.etDate)
        date.setOnClickListener{
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val datePicker= DatePickerDialog(requireContext(),{_,y,m,d ->
                val selectedDay="$d/${m+1}/$y"
                date.setText(selectedDay)
            },year,month,day)
            datePicker.show()
        }
    }

}


