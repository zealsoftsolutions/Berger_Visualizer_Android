package com.berger.bergerXpressVisualiserPk.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.FileProvider.*
import com.berger.bergerXpressVisualiserPk.CameraViewActivity
import com.berger.bergerXpressVisualiserPk.utill.Constants
import com.berger.bergerXpressVisualiserPk.R
import com.berger.bergerXpressVisualiserPk.utill.Utills
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class SourceSelectActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var title: TextView
    private lateinit var back: ImageView

    private lateinit var text: TextView
    private lateinit var fromGallery: TextView
    private lateinit var fromCamera: TextView
    private lateinit var openCamera: TextView
    private lateinit var imageFilePath: String
    private var photoURI: Uri? = null

    private val REQUEST_CAMERA_PERMISSION = 1

    private val PERMISSIONS = arrayOf<String>(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )
    private var mLastClickTime: Long = 0

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                    startPainting(null)
                }
            }


    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                if (imageUri != null) {
                    startPainting(imageUri)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_source_select)

        setViews()
    }

    private fun setViews() {

        title = findViewById(R.id.title)
        title.text = resources.getText(R.string.title_select_source_screen)
        back = findViewById(R.id.back)
        back.setOnClickListener(this)

        text = findViewById(R.id.text)
        fromGallery = findViewById(R.id.from_gallery)
        fromGallery.setOnClickListener(this)
        fromCamera = findViewById(R.id.from_camera)
        fromCamera.setOnClickListener(this)
        openCamera = findViewById(R.id.open_camera)
        openCamera.setOnClickListener(this)

        Utills.changeNavigationBarColor(this, Constants.COLOR_THEME)
//        Utills.transparentToolbar(this, true)
//        Utills.transparentNavigation(this, true, false)
    }

    private fun openCamera() {
        val context = this

        // Check for camera permission
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            askCameraPermission()
            return
        }

        // Create a camera intent
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Ensure there’s a camera activity available
        if (captureIntent.resolveActivity(packageManager) != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                ex.printStackTrace()
                null
            }

            photoFile?.let {
                val authority = getString(R.string.camera_file_provider)
                photoURI = FileProvider.getUriForFile(context, authority, it)

                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

                captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                captureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                cameraLauncher.launch(captureIntent)
            }
        } else {
//            Toast.makeText(context, "No camera app found", Toast.LENGTH_SHORT).show()
        }


//        if (ActivityCompat.checkSelfPermission(baseContext, Manifest.permission.CAMERA)
//            != PackageManager.PERMISSION_GRANTED) {
////            ActivityCompat.requestPermissions(this, PERMISSIONS, LoadImage.PICK_FROM_CAMERA.ordinal)
//            askCameraPermission()
//        } else {
//            val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            var photoFile: File? = null
//            try {
//                photoFile = createImageFile()
//            } catch (ex: IOException) {
//                // Error occurred while creating the File
//            }
//            if (photoFile != null) {
//                photoURI =   getUriForFile(this, resources.getString(R.string.camera_file_provider), photoFile)
//
////                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
//                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                startActivityForResult(captureIntent,   LoadImage.PICK_FROM_CAMERA.ordinal)
//            }
//        }
    }

    private fun openGallery() {

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
                pickImageLauncher.launch(intent)
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "image/*"
                }
                pickImageLauncher.launch(intent)
            }

            // ⚠️ Android 10 and below: Legacy method (still needs READ_EXTERNAL_STORAGE)
            else -> {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 123)
                } else {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    pickImageLauncher.launch(intent)
                }
            }
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format( Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
        imageFilePath = image.absolutePath
        return image
    }

    private enum class LoadImage {
        PICK_FROM_CAMERA,
        PICK_FROM_GALLERY
    }

//    private fun loadFromGallery(data:Intent?) {
//        val selectedImage = data?.data
//        val filePathColumn: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
//        val cursor = contentResolver.query(selectedImage!!,filePathColumn, null, null, null)
//        cursor?.moveToFirst()
//        val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
//        val picturePath = cursor?.getString(columnIndex!!)
//        cursor?.close()
//        var bitmap = BitmapFactory.decodeFile(picturePath)
//        bitmap = getResizedBitmap(bitmap,bitmap.width/5,bitmap.height/5)
//        startPainting(bitmap)
//    }

    private fun startPainting(data: Uri?){
        var paintIt = Intent(this, PaintActivity::class.java)
//        paintIt.putExtra("bitmap", bitmap)
        if(photoURI != null)
            paintIt.putExtra("camera", photoURI.toString())
        if(data != null)
            paintIt.putExtra("gallery", data.toString())

        photoURI = null
        startActivity(paintIt)
    }

    private fun startCameraPainting(){
        if (ActivityCompat.checkSelfPermission(baseContext, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, LoadImage.PICK_FROM_CAMERA.ordinal)
        } else {
            var paintIt = Intent(this, CameraViewActivity::class.java)
            startActivity(paintIt)
        }
    }

    fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        val width = bm.getWidth()
        val height = bm.getHeight()
        val scaleWidth = newWidth / width.toFloat()
        val scaleHeight = newHeight / height.toFloat()
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)
        // "RECREATE" THE NEW BITMAP
        val resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,  matrix, true)
        return resizedBitmap
    }

    private fun askCameraPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            LoadImage.PICK_FROM_CAMERA.ordinal -> if (resultCode == Activity.RESULT_OK) {
                try {
//                    imageFromData.setImageURI(Uri.parse(imageFilePath))
//                    var bitmap = imageFromData.drawable.toBitmap()
//                    var bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                        ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.contentResolver, photoURI!!))
//                    } else {
//                        MediaStore.Images.Media.getBitmap(this.contentResolver, photoURI)
//                    }
////                    var bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, Uri.parse(imageFilePath))
//                    bitmap = getResizedBitmap(bitmap, bitmap.width / 5, bitmap.height / 5)
                    startPainting(null)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            LoadImage.PICK_FROM_GALLERY.ordinal -> if (resultCode == Activity.RESULT_OK) {
                startPainting(data?.data)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        var result = false
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            result = true

        when (requestCode) {

            REQUEST_CAMERA_PERMISSION ->
                if (result) {
                    openCamera()
                }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onClick(v: View?) {
        when(v!!.id){

            else -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                performClick(v)
            }
        }
    }

    private fun performClick(v: View) {
        when (v.id) {
            R.id.back ->
                finish()

            R.id.from_gallery ->
                openGallery()

            R.id.from_camera ->
                openCamera()

            R.id.open_camera ->
                startCameraPainting()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Constants.points = ArrayList()
    }
}