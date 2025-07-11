package com.example.loginsignup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.loginsignup.databinding.ActivityMinistryUserBinding

class MinistryUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMinistryUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = ActivityMinistryUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Display a welcome message for ministry users
        binding.welcomeText.text = "Welcome, Ministry User!"

        // Set up OnClickListener for Dashboard Button
        binding.btnDashboard.setOnClickListener {
            // Start MainActivity when Dashboard button is clicked
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        // Set up OnClickListener for Dashboard Button
        binding.btnProject.setOnClickListener {
            // Start com.example.loginsignup.ProgressDashboard when btnProject is clicked
            val intent = Intent(this, ProgressDashboard::class.java)
            startActivity(intent)
        }
    }
}
