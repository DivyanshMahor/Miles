package com.example.miles

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var signUpButton: Button
    private lateinit var progressBar: ProgressBar
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        signUpButton = findViewById(R.id.btnSignUp)  // Ensure the correct ID is used
        progressBar = findViewById(R.id.progressBar)

        loginButton.setOnClickListener {
            loginUser()
        }

        signUpButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // Ask before exiting the app
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AlertDialog.Builder(this@LoginActivity)
                    .setTitle("Exit App")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes") { _, _ -> finishAffinity() }
                    .setNegativeButton("No", null)
                    .show()
            }
        })
    }

    private fun loginUser() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        if (email.isEmpty()) {
            showToast("Email field is empty!")
            return
        }

        if (password.isEmpty()) {
            showToast("Password field is empty!")
            return
        }

        // Disable button and show progress bar
        loginButton.isEnabled = false
        progressBar.visibility = View.VISIBLE

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                loginButton.isEnabled = true
                progressBar.visibility = View.GONE

                if (task.isSuccessful) {
                    showToast("Login Successful!")
                    val intent = Intent(this, ToDoActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    val errorMessage = task.exception?.localizedMessage ?: "Login failed. Please try again."
                    when (task.exception) {
                        is FirebaseAuthInvalidCredentialsException -> showToast("Invalid password! Try again.")
                        is FirebaseAuthInvalidUserException -> showToast("No account found with this email.")
                        else -> showToast("Login Failed: $errorMessage")
                    }
                }
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
