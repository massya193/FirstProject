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
import com.example.myfinancefinalproject.viewmodel.FinanceViewModel
import com.example.myfinancefinalproject.viewmodel.FinanceViewModelFactory
import kotlinx.coroutines.launch
import kotlin.getValue

class WelcomeActivity : AppCompatActivity() {
    private val viewModel: FinanceViewModel by viewModels {
        val userId = UserPreferences.getCurrentUser(this)
        val db = DatabaseProvider.getDatabase(this)
        FinanceViewModelFactory(
            FinanceRepository(
                db.balanceDao(),
                db.expenseDao(),
                db.incomeDao(),
                db.userDao()
            )
        )
    }

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
        var nickname=findViewById<EditText>(R.id.etNickname)

        btnRegister.setOnClickListener {
            val intent = Intent(this@WelcomeActivity, RegActivity::class.java)
            startActivity(intent)
            finish()
        }
        val button = findViewById<Button>(R.id.btnLogin)
        button.setOnClickListener {lifecycleScope.launch {
            viewModel.nicknameExists(nickname.text.toString()){ exists ->
                if (exists == true) //TODO РЕШИ ЭТУ ПРОБЛЕМУ
                    viewModel.getPassword(nickname.text.toString()) { pass ->
                        if (pass == password.text.toString()) {
                            CanEnter = true
                            FailedLogin.setText("")
                        } else {
                            CanEnter = false
                            password.setTextColor(R.color.RedExp)
                            FailedLogin.setText("Wrong password")
                            if (password.isFocused)
                                password.setTextColor(R.color.textPrimary)
                        }
                        FailedLogin.setText("")
                    }
                else {
                    CanEnter = false
                    nickname.setTextColor(R.color.RedExp)
                    FailedLogin.setText("User not found")
                    if (nickname.isFocused)
                        nickname.setTextColor(R.color.textPrimary)
                }
            }
        }
            if (CanEnter == true) {
            UserPreferences.saveCurrentUser(this@WelcomeActivity, nickname.text.toString())
            val intent = Intent(
                this@WelcomeActivity,
                MainActivity::class.java
            )//Делает переход на MainActivity
            startActivity(intent)//начинает следующее окно
            finish()//заканчивает текущее окно
            }
        }
    }
}
