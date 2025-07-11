package com.example.loginsignup

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.loginsignup.databinding.ActivityLoginactivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class loginactivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginactivityBinding
    private lateinit var auth: FirebaseAuth

    override fun onStart() {
        super.onStart()
        // Check if the user is already logged in
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            navigateToAppropriateActivity(currentUser)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = ActivityLoginactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Set up the login button click listener
        binding.loginButton.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            } else {
                signIn(email, password)
            }
        }

        // Set up the sign-up button click listener
        binding.signUpButton.setOnClickListener {
            // Navigate to SignupActivity
            val intent = Intent(this, signupactivity::class.java)
            startActivity(intent)
        }

        // Set up the forgot password button click listener
        binding.forgotPassword.setOnClickListener {
            // Navigate to ResetPasswordActivity
            val intent = Intent(this, resertpassword::class.java)
            startActivity(intent)
        }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login successful
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        navigateToAppropriateActivity(currentUser)
                    }
                } else {
                    // Login failed
                    Toast.makeText(
                        this,
                        "Login failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun navigateToAppropriateActivity(user: FirebaseUser) {
        val email = user.email ?: ""
        val intent: Intent

        // Determine user type based on radio button selection
        val selectedRadioButtonId = binding.userTypeToggle.checkedRadioButtonId
        val selectedRadioButton: RadioButton? = findViewById(selectedRadioButtonId)

        if (selectedRadioButton != null) {
            when (selectedRadioButton.id) {
                R.id.normalUserRadioButton -> {
                    // Normal User
                    intent = Intent(this, NormalUserActivity::class.java)
                }
                R.id.ministryUserRadioButton -> {
                    // Ministry User
                    intent = Intent(this, MinistryUserActivity::class.java)
                }
                else -> {
                    // If no radio button is selected, show a message
                    Toast.makeText(this, "Please select a user type", Toast.LENGTH_SHORT).show()
                    return
                }
            }

            startActivity(intent)
            finish()  // Close the current activity
        }
    }
}
