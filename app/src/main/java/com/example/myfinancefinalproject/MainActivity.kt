package com.example.myfinancefinalproject

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.example.myfinancefinalproject.data.database.DatabaseProvider
import com.example.myfinancefinalproject.data.repository.FinanceRepository
import com.example.myfinancefinalproject.viewmodel.FinanceViewModel
import com.example.myfinancefinalproject.viewmodel.FinanceViewModelFactory
import kotlin.getValue

class MainActivity : AppCompatActivity() {
    private lateinit var tabHome: View
    private lateinit var tabHistory: View
    private lateinit var tabReport: View
    private lateinit var tabSettings: View
    private val viewModel: FinanceViewModel by viewModels {
        val userId=UserPreferences.getCurrentUser(this)

        val db=DatabaseProvider.getDatabase(this)
        FinanceViewModelFactory(
            FinanceRepository(
                db.balanceDao(),
                db.expenseDao(),
                db.incomeDao(),
                db.userDao()
            )
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val userId=UserPreferences.getCurrentUser(this)
        tabHome = findViewById(R.id.tabHome)
        tabHistory = findViewById(R.id.tabHistory)
        tabReport = findViewById(R.id.tabReport)
        tabSettings = findViewById(R.id.tabSettings)
        highlightTab(tabHome)
        if(savedInstanceState==null)
        {
            replaceFragment(HomeFragment())
        }
        tabHome.setOnClickListener {
            replaceFragment(HomeFragment())
            highlightTab(tabHome)
        }
        tabHistory.setOnClickListener {
            replaceFragment(HistoryFragment())
            highlightTab(tabHistory)
        }
        tabReport.setOnClickListener {
            replaceFragment(ReportFragment())
            highlightTab(tabReport)
        }
        tabSettings.setOnClickListener {
            replaceFragment(SettingsFragment())
            highlightTab(tabSettings)
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment)
            .commit()
    }
    private fun highlightTab(selectedTab: View) {
        val allTabs = listOf(tabHome, tabHistory, tabReport, tabSettings)
        for (tab in allTabs) {
            if (tab == selectedTab) {
                tab.setBackgroundColor(getColor(R.color.colorPrimary)) // активная
            } else {
                tab.setBackgroundColor(getColor(R.color.colorSurface)) // обычная
            }
        }
    }
}