package com.example.loginsignup

import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class ProgressDashboard : AppCompatActivity() {

    // Define the ImageData data class inside com.example.loginsignup.ProgressDashboard
    data class ImageData(
        val imageUrl: String,  // URL of the image to be displayed
        val uploadTime: String // Date and time when the image was uploaded
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress_dashboard)  // Assuming this is your layout file

        val listView: ListView = findViewById(R.id.lvImages)  // Assuming you have a ListView in your layout

        // Fetching images from Firestore
        FirebaseFirestore.getInstance()
            .collection("users")
            .document("userId")  // Replace with your current user ID
            .collection("images")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val imageList = mutableListOf<ImageData>()
                for (document in querySnapshot) {
                    val imageUrl = document.getString("imageUrl") ?: ""
                    val uploadTime = document.getString("uploadTime") ?: "Unknown Time"

                    // Add the image data to the list
                    val imageData = ImageData(imageUrl, uploadTime)
                    imageList.add(imageData)
                }

                // Set up the adapter
                val adapter = ImageAdapter(this, imageList)
                listView.adapter = adapter
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading images: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
