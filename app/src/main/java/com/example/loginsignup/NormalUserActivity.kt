package com.example.loginsignup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.loginsignup.databinding.ActivityNormalUserBinding

class NormalUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNormalUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = ActivityNormalUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Display a welcome message for normal users
        binding.welcomeText.text = "Welcome, Normal User!"

        // Set up OnClickListener for Dashboard Button
        binding.dashboardButton.setOnClickListener {
            // Start MainActivity when Dashboard button is clicked
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
