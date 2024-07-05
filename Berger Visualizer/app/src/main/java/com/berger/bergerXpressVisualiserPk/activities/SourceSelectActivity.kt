package com.berger.bergerXpressVisualiserPk.activities

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
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
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
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
        if (ActivityCompat.checkSelfPermission(baseContext, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, PERMISSIONS, LoadImage.PICK_FROM_CAMERA.ordinal)
            askCameraPermission()
        } else {
            val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                photoURI =   FileProvider.getUriForFile(this, resources.getString(R.string.camera_file_provider), photoFile)

//                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(captureIntent,   LoadImage.PICK_FROM_CAMERA.ordinal)
            }
        }
    }

    private fun openGallery() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val intentForLoadingImage = Intent(Intent.ACTION_PICK)
            intentForLoadingImage.type = "image/*"
            try {
                startActivityForResult(intentForLoadingImage, LoadImage.PICK_FROM_GALLERY.ordinal)
            } catch (e: ActivityNotFoundException) { }
        } else {

            val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(i, LoadImage.PICK_FROM_GALLERY.ordinal)
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