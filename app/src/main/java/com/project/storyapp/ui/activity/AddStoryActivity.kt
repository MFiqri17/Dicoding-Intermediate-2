package com.project.storyapp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.project.storyapp.databinding.ActivityAddStoryBinding
import com.project.storyapp.ui.viewModel.AddStoryViewModel
import com.project.storyapp.utils.reduceFileImage
import com.project.storyapp.utils.rotateFile
import com.project.storyapp.utils.uriToFile
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@AndroidEntryPoint
class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private val viewModel: AddStoryViewModel by viewModels()
    private var selectedFile: File? = null

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.buttonCamera.setOnClickListener { startCameraX() }
        binding.buttonGallery.setOnClickListener { startGallery() }

        binding.buttonAdd.setOnClickListener {
            if (selectedFile != null) {
                val description = binding.edAddDescription.text.toString()
                val photo = processImage()
                val lat = 0.0
                val lon = 0.0
                if (description.isNotEmpty()) {
                    submit(description, photo, lat, lon)
                } else {
                    binding.edAddDescription.error = "Description is Required"
                }
            } else {
                showToast("Please select a file first.")
            }
        }
    }

    private fun submit(
        description: String,
        photo: MultipartBody.Part,
        lat: Double?,
        lon: Double?
    ) {
        lifecycleScope.launch {
            viewModel.getToken().collect { token ->
                if (token != null) {
                    showLoading()
                    val jwt = "Bearer $token"
                    try {
                        viewModel.addStory(description, photo, lat, lon, jwt).collect { result ->
                            if (result.isSuccess) {
                                showToast("Story Uploaded")
                                navigateToMainActivity()
                            } else {
                                showToast("Add Story Failed: ${result.exceptionOrNull()?.message}")
                            }
                        }
                    } catch (e: Exception) {
                        showToast("Add Story Failed: ${e.message}")
                    } finally {
                        hideLoading()
                    }
                }
            }
        }
    }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val file = uriToFile(uri, this@AddStoryActivity)
                selectedFile = file
                binding.previewImageView.setImageURI(uri)
            }
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == CAMERA_X_RESULT) {
            val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data?.getSerializableExtra("picture") as? File
            } else {
                @Suppress("DEPRECATION")
                result.data?.getSerializableExtra("picture") as? File
            }

            val isBackCamera = result.data?.getBooleanExtra("isBackCamera", true) ?: true

            file?.let { imageFile ->
                rotateFile(imageFile, isBackCamera)
                selectedFile = imageFile
                binding.previewImageView.setImageBitmap(BitmapFactory.decodeFile(imageFile.path))
            }
        }
    }

    private fun processImage(): MultipartBody.Part {
        val file = reduceFileImage(selectedFile as File)
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
        return MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                showToast("Failed to obtain permissions.")
                finish()
            }
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.buttonAdd.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.buttonAdd.visibility = View.VISIBLE
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}
