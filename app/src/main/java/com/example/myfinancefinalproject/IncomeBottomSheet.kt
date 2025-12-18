package com.example.myfinancefinalproject

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class IncomeBottomSheet (
    private val onSave: (amount: Long,note: String?,date:String) -> Unit
) : BottomSheetDialogFragment() {
    override fun onCreateView(      // Эта функция создаёт и возвращает разметку (layout) панели
        inflater: LayoutInflater,   // Нужен, чтобы "надуть" XML-файл в реальный вид(выделение памяти)
        container: ViewGroup?,      // Родитель, в котором будет отображаться панель
        savedInstanceState: Bundle? // Сохранённое состояние (пока не нужно)
        ):  View? {
        val view = inflater.inflate(R.layout.bottom_sheet_income, container, false)//подключение xml)
        val etAmount = view.findViewById<EditText>(R.id.etAmount)
        val etNote= view.findViewById<EditText>(R.id.etNote)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val etdate=view.findViewById<EditText>(R.id.etDate)
        btnSave.setOnClickListener {
            val amountText = etAmount.text.toString()
            val amount = amountText.toLongOrNull() ?: 0L
            val note=etNote.text?.toString()
            val date=etdate.text.toString()
            onSave(amount,note,date)
            dismiss()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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