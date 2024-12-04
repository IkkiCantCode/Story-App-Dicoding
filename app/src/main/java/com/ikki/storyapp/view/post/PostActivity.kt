package com.ikki.storyapp.view.post

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.ikki.storyapp.data.ApiConfig
import com.ikki.storyapp.data.pref.UserPreference
import com.ikki.storyapp.data.pref.dataStore
import com.ikki.storyapp.databinding.ActivityPostBinding
import com.ikki.storyapp.response.UploadResponse
import com.ikki.storyapp.utils.getImageUri
import com.ikki.storyapp.utils.reduceFileImage
import com.ikki.storyapp.utils.uriToFile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class PostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostBinding
    private var currentImageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener { uploadImage() }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            currentImageUri = null
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private suspend fun getToken(): String {
        val userPreference = UserPreference.getInstance(applicationContext.dataStore)
        val userSession = userPreference.getSession().first()
        return userSession.token
    }

    private fun uploadImage() {
        val description = binding.descriptionEditText.text.toString().trim()

        if (currentImageUri == null) {
            showToast("Please select an image.")
            return
        }
        if (description.isEmpty()) {
            showToast("Please enter a description.")
            return
        }

        lifecycleScope.launch {
            try {
                val token = getToken()
                Log.d("TokenCheck", "Retrieved Token: $token")

                if (token.isEmpty()) {
                    showToast("Authentication token is missing.")
                    return@launch
                }
                val authorizationHeader = "Bearer $token"

                currentImageUri?.let { uri ->
                    val imageFile = uriToFile(uri, this@PostActivity).reduceFileImage()
                    val descriptionRequestBody = description.toRequestBody("text/plain".toMediaType())
                    val imageRequestBody = imageFile.asRequestBody("image/jpeg".toMediaType())
                    val multipartBody = MultipartBody.Part.createFormData("photo", imageFile.name, imageRequestBody)

                    showLoading(true)

                    val apiService = ApiConfig.getApiService()
                    val successResponse = apiService.uploadImage(authorizationHeader, multipartBody, descriptionRequestBody)

                    if (successResponse.error == false) {
                        showToast("Story uploaded successfully!")
                        val resultIntent = Intent().apply {
                            putExtra("story_description", description)
                        }
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    } else {
                        showToast("Failed to upload story.")
                        setResult(RESULT_CANCELED)
                    }

                    showLoading(false)
                }
            } catch (e: HttpException) {
                showLoading(false)
                val errorBody = e.response()?.errorBody()?.string()

                val errorMessage = errorBody?.let {
                    Gson().fromJson(it, UploadResponse::class.java).message
                } ?: "An error occurred."

                if (e.code() == 401) {
                    showToast("Authentication failed. Please login again.")
                } else {
                    showToast(errorMessage)
                }
                setResult(RESULT_CANCELED)

            } catch (e: Exception) {
                showLoading(false)
                showToast("An unexpected error occurred: ${e.message}")
            }

        }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}