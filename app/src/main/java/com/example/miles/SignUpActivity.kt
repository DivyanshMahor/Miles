package com.example.miles

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var signUpButton: Button
    private lateinit var progressBar: ProgressBar
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up) // ✅ Ensure correct layout is used

        // Initialize UI elements
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput)
        signUpButton = findViewById(R.id.signUpButton)
        progressBar = findViewById(R.id.progressBar)

        signUpButton.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val email = emailInput.text?.toString()?.trim()
        val password = passwordInput.text?.toString()?.trim()
        val confirmPassword = confirmPasswordInput.text?.toString()?.trim()

        if (email.isNullOrEmpty() || password.isNullOrEmpty() || confirmPassword.isNullOrEmpty()) {
            showToast("Please fill in all fields!")
            return
        }

        if (password != confirmPassword) {
            showToast("Passwords do not match!")
            return
        }

        if (password.length < 6) {
            showToast("Password must be at least 6 characters!")
            return
        }

        // Disable button and show progress bar
        signUpButton.isEnabled = false
        progressBar.visibility = View.VISIBLE

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                // Re-enable button and hide progress bar
                signUpButton.isEnabled = true
                progressBar.visibility = View.GONE

                if (task.isSuccessful) {
                    showToast("Sign-Up Successful! Please log in.")
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    val errorMessage = task.exception?.localizedMessage ?: "Unknown error occurred"
                    showToast("Sign-Up Failed: $errorMessage")
                }
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
