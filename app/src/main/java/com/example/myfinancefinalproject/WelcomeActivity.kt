package com.example.myfinancefinalproject

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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

class WelcomeActivity : AppCompatActivity() {
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

    @SuppressLint("ResourceAsColor", "WrongViewCast", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_welcome)
        val password = findViewById<EditText>(R.id.etPassword)
        val Fpass = findViewById<TextView>(R.id.ForgotPass)
        val FailedLogin = findViewById<TextView>(R.id.FailedLogin)
        var CanEnter: Boolean = false
        val btnRegister = findViewById<TextView>(R.id.btnRegister)
        var nickname = findViewById<EditText>(R.id.etNickname)

        btnRegister.setOnClickListener {
            val intent = Intent(this@WelcomeActivity, RegActivity::class.java)
            startActivity(intent)
            finish()
        }
        val button = findViewById<Button>(R.id.btnLogin)
        button.setOnClickListener {
            userViewModel.login(
                nickname.text.toString(),
                password.text.toString()
            ) { userId ->
                if (userId != null) {
                    UserPreferences.saveUserId(this, userId)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    FailedLogin.text = "Wrong nickname or password"
                }
            }
        }
        if (CanEnter == true) {
            val user = userViewModel.user.value!!
            UserPreferences.saveUserId(this, user.userId)
            val intent = Intent(
                this@WelcomeActivity,
                MainActivity::class.java
            )//Делает переход на MainActivity
            startActivity(intent)//начинает следующее окно
            finish()//заканчивает текущее окно
        }
    }
}

