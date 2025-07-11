package com.example.loginsignup

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.HttpUrl
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import com.google.gson.Gson
import android.graphics.BitmapFactory
import android.widget.ProgressBar
import android.util.Base64
import android.graphics.Bitmap
import okhttp3.ResponseBody
import java.net.HttpURLConnection
import java.net.URL


data class FlaskResponse(
    val ssim_score: Float,
    val change_percentage: Float,
    val progress_estimation: Float,
    val image: String,
    val llava_description: String,
    val construction_level: String,
    val construction_stage: String
)

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var tvStatus: TextView
    private lateinit var btnUploadImage: Button
    private lateinit var btnCaptureImage: Button
    private var currentPhotoPath: String? = null
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    companion object {
        private const val CAMERA_PERMISSION_CODE = 101
        private const val CAMERA_REQUEST_CODE = 102
        private const val CLOUD_NAME = "dl2nwcls0"
        private const val API_KEY = "754326796448752"
        private const val API_SECRET = "plRV1kDCcodCcXRm-iHJhdqj2qE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        tvStatus = findViewById(R.id.tv_status)
        btnUploadImage = findViewById(R.id.upload_image_button)
        btnCaptureImage = findViewById(R.id.btnCaptureImage)

        val config = mapOf(
            "cloud_name" to CLOUD_NAME,
            "api_key" to API_KEY,
            "api_secret" to API_SECRET
        )
        MediaManager.init(this, config)

        btnCaptureImage.setOnClickListener { requestCameraPermission() }
        btnUploadImage.setOnClickListener { uploadImageToCloudinary() }
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            Log.e("MainActivity", "Error creating file", ex)
            null
        }
        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", it)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imageUri = Uri.fromFile(File(currentPhotoPath))
            imageView.setImageURI(imageUri)
        }
    }

    private fun uploadImageToCloudinary() {
        if (currentPhotoPath.isNullOrEmpty()) {
            Toast.makeText(this, "No image captured to upload!", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated!", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = currentUser.uid
        val username = currentUser.displayName ?: "Unknown"  // If displayName is null, use "Unknown"

        MediaManager.get().upload(currentPhotoPath!!)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {
                    tvStatus.text = "Uploading..."
                }

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                    tvStatus.text = "Uploading... ($bytes/$totalBytes)"
                }

                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val imageUrl = resultData["url"] as String
                    val uploadTime =
                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())

                    tvStatus.text = "Upload successful: $imageUrl"
                    Toast.makeText(
                        this@MainActivity,
                        "Image uploaded successfully!",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Save to Firestore under the user's collection
                    val imageData = hashMapOf(
                        "imageUrl" to imageUrl,
                        "uploadTime" to uploadTime,
                        "username" to username  // Add the username here
                    )
                    db.collection("users")
                        .document(userId)
                        .collection("images")
                        .add(imageData)
                        .addOnSuccessListener {
                            tvStatus.text = "Image saved to Firestore for user $userId"
                        }
                        .addOnFailureListener { e ->
                            tvStatus.text = "Error saving to Firestore: ${e.message}"
                            Log.e("MainActivity", "Firestore error", e)
                        }
                }

                override fun onError(requestId: String, error: ErrorInfo) {
                    tvStatus.text = "Error: ${error.description}"
                }

                override fun onReschedule(requestId: String, error: ErrorInfo) {
                    tvStatus.text = "Rescheduled: ${error.description}"
                }
            })
            .dispatch()
    }
    private fun fetchImagesFromFirestore() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated!", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = currentUser.uid

        // Fetch the latest image
        db.collection("users")
            .document(userId)
            .collection("images")
            .orderBy("uploadTime", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val latestImageUrl = documents.first().getString("imageUrl") ?: ""
                    Log.d("MainActivity", "Latest Image: $latestImageUrl")
                    sendImageUrlToFlask(latestImageUrl, "latest")
                }
            }

        // Fetch the oldest image
        db.collection("users")
            .document(userId)
            .collection("images")
            .orderBy("uploadTime", com.google.firebase.firestore.Query.Direction.ASCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val oldestImageUrl = documents.first().getString("imageUrl") ?: ""
                    Log.d("MainActivity", "Oldest Image: $oldestImageUrl")
                    sendImageUrlToFlask(oldestImageUrl, "oldest")
                }
            }
    }
    private fun sendImageUrlToFlask(imageUrl: String, imageType: String) {
        val thread = Thread {
            try {
                // Download the image from the URL
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val imageBytes = inputStream.readBytes()
                    inputStream.close()

                    // Convert the image to a ByteArray
                    val imageFile = File.createTempFile("uploaded_image_", ".jpg")
                    imageFile.writeBytes(imageBytes)

                    // Prepare the image for upload to Flask
                    val requestBody = MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("image1", imageFile.name, RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageFile))
                        .addFormDataPart("imageType", imageType)
                        .build()

                    // Make the HTTP request to Flask server
                    val request = Request.Builder()
                        .url("http://127.0.0.1:5000/upload")
                        .post(requestBody)
                        .build()

                    val client = OkHttpClient()
                    val response = client.newCall(request).execute()

                    if (response.isSuccessful) {
                        val responseData = response.body?.string()

                        // Parse the response JSON into a FlaskResponse object using Gson
                        val gson = Gson()
                        val flaskResponse = gson.fromJson(responseData, FlaskResponse::class.java)

                        // Update the UI with the parsed data
                        runOnUiThread {
                            // Set the SSIM score
                            findViewById<TextView>(R.id.ssimScoreTextView).text = "SSIM Score: ${flaskResponse.ssim_score}"

                            // Set the change percentage
                            findViewById<TextView>(R.id.changePercentageTextView).text = "Change Percentage: ${flaskResponse.change_percentage}%"

                            // Set the description (LLaVA description)
                            //findViewById<TextView>(R.id.llava_description).text = "Description: ${flaskResponse.llava_description}"

                            // Set the construction level and stage
                            findViewById<TextView>(R.id.constructionLevelTextView).text = "Construction Level: ${flaskResponse.construction_level}"
                            findViewById<TextView>(R.id.constructionStageTextView).text = "Construction Stage: ${flaskResponse.construction_stage}"

                            // Set the image preview from base64
                            val decodedString = Base64.decode(flaskResponse.image, Base64.DEFAULT)
                            val decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                            findViewById<ImageView>(R.id.resultImageView).setImageBitmap(decodedBitmap)

                            // Set the progress text (if necessary)
                            findViewById<TextView>(R.id.tv_progress_percentage).text = "Progress: ${flaskResponse.progress_estimation}%"

                            // Optionally update progress bar if you need to track it visually
                            findViewById<ProgressBar>(R.id.progressBar).progress = flaskResponse.progress_estimation.toInt()

                            // Update status
                            findViewById<TextView>(R.id.tv_status).text = "Status: Completed"
                        }
                    } else {
                        Log.e("MainActivity", "Failed to send image to Flask: ${response.code}")
                    }
                } else {
                    Log.e("MainActivity", "Failed to download image from URL. Response code: ${connection.responseCode}")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error in sending image to Flask", e)
            }
        }
        thread.start()
    }


}
