package com.project.storyapp.ui.add

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.jakewharton.rxbinding2.widget.RxTextView
import com.project.core.data.source.Resource
import com.project.storyapp.R
import com.project.storyapp.databinding.ActivityAddStoryBinding
import com.project.storyapp.ui.main.MainActivity
import com.project.storyapp.utils.*
import kotlinx.coroutines.*
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.File
import java.util.concurrent.Executors

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var currentPhotoPath: String

    private val viewModel: AddStoryViewModel by viewModel()

    private lateinit var loading: AlertDialog

    private var file: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // init
        loading = showAlertLoading(this)

        if (!allPermissionGranted()){
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        onAction()

        reactiveStream()
    }

    @SuppressLint("CheckResult")
    private fun reactiveStream() {
        binding.apply {

            val descriptionStream = RxTextView.textChanges(edtDescription)
                .skipInitialValue()
                .map { it.isNotBlank() }

            descriptionStream.subscribe {
                if (it && file != null){
                    btnUploadImage.enable()
                } else btnUploadImage.disable()
            }
        }
    }

    private fun onAction() {
        binding.apply {
            imgUploadStory.setOnClickListener { getPickerStoryImage() }
            btnUploadImage.setOnClickListener { uploadImage() }
        }
    }

    private fun uploadImage() {
        binding.apply {
            if (file != null) {

                val description = edtDescription.text.trim().toString()

                val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
                val scope = CoroutineScope(dispatcher)

                scope.launch {
                    val token = "Bearer ${viewModel.getUserToken()}"

                    withContext(Dispatchers.Main){
                        viewModel.doUploadStory(token, file as File, description).observe(this@AddStoryActivity){ resource ->
                            when(resource){
                                is Resource.Loading -> loading.show()
                                is Resource.Success -> {
                                    Intent(this@AddStoryActivity, MainActivity::class.java).also { intent ->
                                        intent.putExtra(MainActivity.SUCCESS_UPLOAD_STORY, true)
                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        startActivity(intent)
                                        loading.dismiss()
                                    }
                                }
                                is Resource.Error -> {
                                    Timber.e(resource.message.toString())
                                    loading.dismiss()
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    private fun getPickerStoryImage() {
        showBottomSheetDialogImage(this, {
            startTakePhoto()
        }){
            startGallery()
        }.show()
    }

    private fun startGallery() {
        Intent().also { intent ->
            intent.action = ACTION_GET_CONTENT
            intent.type = "image/*"

            val chooser = Intent.createChooser(intent, getString(R.string.choose_a_picture))
            launcherIntentGallery.launch(chooser)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == RESULT_OK){
            val selectedImg: Uri = it.data?.data as Uri

            val myFile = uriToFile(selectedImg, this)

            file = myFile

            binding.imgUploadStory.setImageURI(selectedImg)
        }
    }

    private fun startTakePhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(packageManager)

            createCustomTempFile(application).also {
                val photoUri: Uri = FileProvider.getUriForFile(this, "com.project.storyapp", it)
                currentPhotoPath = it.absolutePath
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

                launcherIntentCamera.launch(intent)
            }
        }
    }

    private val launcherIntentCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == RESULT_OK){
            val myFile = File(currentPhotoPath)
            file = myFile

            val result = BitmapFactory.decodeFile(file?.path)
            binding.imgUploadStory.setImageBitmap(result)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS){
            if (!allPermissionGranted()){
                this.showToast(getString(R.string.not_getting_permission))
                finish()
            }
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}