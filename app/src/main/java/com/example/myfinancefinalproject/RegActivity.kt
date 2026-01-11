package com.example.myfinancefinalproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.myfinancefinalproject.data.database.DatabaseProvider
import com.example.myfinancefinalproject.data.entity.Balance
import com.example.myfinancefinalproject.data.entity.User
import com.example.myfinancefinalproject.data.repository.FinanceRepository
import com.example.myfinancefinalproject.data.repository.UserRepository
import com.example.myfinancefinalproject.viewmodel.FinanceViewModel
import com.example.myfinancefinalproject.viewmodel.FinanceViewModelFactory
import com.example.myfinancefinalproject.viewmodel.UserViewModel
import com.example.myfinancefinalproject.viewmodel.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import kotlin.getValue

class RegActivity : AppCompatActivity() {

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
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>

    private var imageUri: Uri? = null
    private var avatarPath: String = ""   //добавляем переменную для хранения пути к фото

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reg)

        val password = findViewById<EditText>(R.id.etPassword)
        val nickname = findViewById<EditText>(R.id.etNickname)
        val imageView = findViewById<ImageView>(R.id.imageView)
        val btnSetPhoto = findViewById<TextView>(R.id.setPhoto)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        // выбор фото
        btnSetPhoto.setOnClickListener { showPhotoPickerDialog() }

        // выбор из галереи
        galleryLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri != null) {
                imageUri = uri
                imageView.setImageURI(uri)
                avatarPath = saveImageToInternalStorage(uri) // сохраняем путь
            }
        }

        // фото с камеры
        cameraLauncher = registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { success ->
            if (success && imageUri != null) {
                imageView.setImageURI(imageUri)
                avatarPath = saveImageToInternalStorage(imageUri!!) // сохраняем путь
            }
        }

        btnRegister.setOnClickListener {
            val newUser = User(
                password = password.text.toString(),
                nickname = nickname.text.toString(),
                avatarPath = avatarPath
            )
            userViewModel.register(newUser) { userId ->
                UserPreferences.saveUserId(this, userId.toInt())
                Toast.makeText(this, "Регистрация успешна", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }
    }

    //сохраняем фото во внутреннее хранилище
    private fun saveImageToInternalStorage(uri: Uri): String {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return ""
            val file = File(filesDir, "user_avatar_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun openCamera() {
        val file = File(filesDir, "temp_photo.jpg")
        imageUri = FileProvider.getUriForFile(
            this,
            "${packageName}.provider",
            file
        )
        imageUri?.let { uri ->
            cameraLauncher.launch(uri)
        }
    }

    private fun showPhotoPickerDialog() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_photo_picker, null)
        val btnCamera = view.findViewById<Button>(R.id.btnCamera)
        val btnGallery = view.findViewById<Button>(R.id.btnGallery)

        btnCamera.setOnClickListener {
            dialog.dismiss()
            openCamera()
        }

        btnGallery.setOnClickListener {
            dialog.dismiss()
            openGallery()
        }

        dialog.setContentView(view)
        dialog.show()
    }
}
