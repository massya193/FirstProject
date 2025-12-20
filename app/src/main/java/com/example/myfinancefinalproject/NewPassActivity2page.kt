package com.example.myfinancefinalproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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

class NewPassActivity2page : AppCompatActivity() {
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
        setContentView(R.layout.activity_new_pass_activity2page)
        val newpass=findViewById<EditText>(R.id.etnewpass)
        val button=findViewById<Button>(R.id.btnResetPassword)
        val userId=intent.getIntExtra("userID",-1)
        button.setOnClickListener {
            lifecycleScope.launch {
                if (userId != -1) {
                    userViewModel.updatePassword(newpass.text.toString())
                }
            }
            val intent= Intent(this@NewPassActivity2page, WelcomeActivity::class.java)
            intent.putExtra("userID",userId)
            startActivity(intent)
            finish()
        }
    }
}