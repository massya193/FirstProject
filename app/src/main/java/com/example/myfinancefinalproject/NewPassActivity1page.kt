package com.example.myfinancefinalproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.myfinancefinalproject.data.database.DatabaseProvider
import com.example.myfinancefinalproject.data.repository.FinanceRepository
import com.example.myfinancefinalproject.data.repository.UserRepository
import com.example.myfinancefinalproject.viewmodel.FinanceViewModel
import com.example.myfinancefinalproject.viewmodel.FinanceViewModelFactory
import com.example.myfinancefinalproject.viewmodel.UserViewModel
import com.example.myfinancefinalproject.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import kotlin.getValue

class NewPassActivity1page : AppCompatActivity() {
    private val factory by lazy {
        val db = DatabaseProvider.getDatabase(this)
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_pass)
        val userId = findViewById<EditText>(R.id.etUserId)
        val button = findViewById<Button>(R.id.btnResetPassword)
        val FailedId = findViewById<TextView>(R.id.failId)
        button.setOnClickListener {
            lifecycleScope.launch {
                userViewModel.userExists(userId.text.toString().toInt()) { exists ->
                    if (exists) {
                        val intent = Intent(
                            this@NewPassActivity1page,
                            NewPassActivity2page::class.java
                        )
                        intent.putExtra("userID",userId.text.toString().toInt())
                        startActivity(intent)
                        finish()
                    } else {
                        userId.setTextColor(ContextCompat.getColor(this as Context, R.color.RedExp))
                        FailedId.setText("Wrong password")
                        if (userId.isFocused)
                            userId.setTextColor(ContextCompat.getColor(this, R.color.textPrimary))

                    }
                }
            }
        }
    }
}