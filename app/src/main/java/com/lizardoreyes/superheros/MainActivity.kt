package com.lizardoreyes.superheros

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.lizardoreyes.superheros.databinding.ActivityMainBinding
import com.lizardoreyes.superheros.models.SuperHero
import com.lizardoreyes.superheros.utils.Constants.MY_CAMERA_REQUEST_CODE
import com.lizardoreyes.superheros.utils.Constants.SUPERHERO_NAME
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var image: Bitmap? = null
    private var picturePath = ""

    private val getContent = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if(it && picturePath.isNotEmpty()) {
            image = BitmapFactory.decodeFile(picturePath)
            binding.ivPhoto.setImageBitmap(image)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSave.setOnClickListener { openDetails() }
        binding.ivPhoto.setOnClickListener { openCameraPermission() }
    }

    private fun openCamera() {
        val file = createImageFile()
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(this, "$packageName.provider", file)
        } else {
            Uri.fromFile(file)
        }
        getContent.launch(uri)
    }

    private fun createImageFile(): File {
        val fileName = "superhero_image"
        val fileDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile(fileName, ".jpg", fileDirectory)
        picturePath = file.absolutePath
        return file
    }

    private fun openCameraPermission() {
        when {
            checkPermission(android.Manifest.permission.CAMERA) -> {
                openCamera()
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA) -> {
                requestPermissions(arrayOf(android.Manifest.permission.CAMERA), MY_CAMERA_REQUEST_CODE)
            }
            else -> {
                Toast.makeText(this, getString(R.string.permission), Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                intent.data = Uri.parse("package:$packageName")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                startActivity(intent)
            }
        }
    }

    private fun checkPermission(permission: String): Boolean {
        val result = this.checkCallingOrSelfPermission(permission)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun openDetails() = with(binding) {
        val name = edtSuperName.text.toString()
        val alterEgo = edtAlterEgo.text.toString()
        val bio = edtBio.text.toString()
        val power = rbPower.rating

        if(name.isNotEmpty() && alterEgo.isNotEmpty() && bio.isNotEmpty()) {
            val superHero = SuperHero(name, alterEgo, bio, power)
            val intent = Intent(applicationContext, DetailsActivity::class.java).apply {
                putExtra(SUPERHERO_NAME, superHero)
                putExtra("image", picturePath)
            }
            startActivity(intent)
        } else {
            Toast.makeText(applicationContext, getString(R.string.no_complete_fields), Toast.LENGTH_SHORT).show()
        }
    }
}