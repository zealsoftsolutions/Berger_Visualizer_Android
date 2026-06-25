package com.berger.bergerXpressVisualiserPk.activities

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.berger.bergerXpressVisualiserPk.R
import com.berger.bergerXpressVisualiserPk.adapters.PaintResultStepsAdapter
import com.berger.bergerXpressVisualiserPk.customWidgets.CustomDialogs
import com.berger.bergerXpressVisualiserPk.customWidgets.MaskingView
import com.berger.bergerXpressVisualiserPk.models.Idea
import com.berger.bergerXpressVisualiserPk.models.MaskingPoint
import com.berger.bergerXpressVisualiserPk.models.PaintStep
import com.berger.bergerXpressVisualiserPk.models.Shade
import com.berger.bergerXpressVisualiserPk.utill.Constants
import com.berger.bergerXpressVisualiserPk.utill.Preferences
import com.berger.bergerXpressVisualiserPk.utill.Utills
import com.bumptech.glide.Glide
import org.opencv.android.*
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList
import org.opencv.core.Mat
import org.opencv.core.CvType
import org.opencv.core.Core
import org.opencv.core.Point
import org.opencv.core.Rect
import org.opencv.core.Size

import org.opencv.core.Scalar


class PaintActivity : AppCompatActivity(), View.OnClickListener, View.OnTouchListener {

    companion object {
        init {
            if (!OpenCVLoader.initDebug()) {
                Log.e("OpenCV", "OpenCV init failed")
            } else {
                Log.d("OpenCV", "OpenCV loaded successfully")
            }
//            System.loadLibrary("opencv_java3")
        }
    }

    var touchCount = 0
    lateinit var tl: Point
    lateinit var bitmap: Bitmap
    var mat = Mat()
    var maskingTapes: ArrayList<MaskingPoint>? = null
    var resizeImage = true

    lateinit var originalBitmap: Bitmap
    var chosenColor = Color.RED
    private var selectedShade: Shade? = null
    private lateinit var imageFilePath: String
    private var texture = false
    private var customPaint = false
    private val TAG = PaintActivity::class.java.simpleName
    private val PERMISSIONS = arrayOf<String>(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    )

    private lateinit var visualizerScreen: ConstraintLayout
    private lateinit var toolbarTitle: CardView

    private lateinit var cameraView: CameraBridgeViewBase

    private lateinit var imageFromData: ImageView
    private lateinit var maskingCanvas: MaskingView

    private lateinit var resultLayout: ConstraintLayout
    private lateinit var paintStepsRecycler: RecyclerView
    private lateinit var topLayout: LinearLayout
    private lateinit var inputImage: ImageView
    private lateinit var greyScaleImage: ImageView
    private lateinit var middleLayout: LinearLayout
    private lateinit var cannyEdgeImage: ImageView
    private lateinit var floodFillImage: ImageView
    private lateinit var bottomLayout: LinearLayout
    private lateinit var HSVImageView: RelativeLayout
    private lateinit var HSVImage: ImageView
    private lateinit var outputImage: ImageView
    private var paintSteps: ArrayList<PaintStep>? = null

    private lateinit var selectedColorCard: CardView
    private lateinit var selectedColorView: ConstraintLayout
    private lateinit var selectedColorLabel: TextView
    private lateinit var selectedColor: ImageView
    private lateinit var selectedColorV: View
    private lateinit var selectedColorName: TextView

    private lateinit var rotate: ImageView
    private lateinit var rotateLabel: TextView
    private lateinit var finalPicture: ImageView
    private lateinit var finalPictureLabel: TextView

    private lateinit var actionBarCard: CardView
    private lateinit var actionBar: ConstraintLayout
    private lateinit var clearPaint: ImageView
    private lateinit var clearPaintLabel: TextView
    private lateinit var undo: ImageView
    private lateinit var undoLabel: TextView
    private lateinit var originalPicture: ImageView
    private lateinit var pickColor: ImageView
    private lateinit var pickTexture: ImageView
    private lateinit var openMasking: ImageView
    private lateinit var openMaskingLabel: TextView
    private lateinit var saveImage: ImageView
    private lateinit var saveImageLabel: TextView

    private lateinit var maskingTapeView: ConstraintLayout
    private lateinit var applyMasking: ImageView
    private lateinit var applyMaskingLabel: TextView
    private lateinit var clearMasking: ImageView
    private lateinit var clearMaskingLabel: TextView

    private lateinit var performanceLow: TextView
    private lateinit var performanceMedium: TextView
    private lateinit var performanceHigh: TextView

    private lateinit var loadingProgress: ProgressBar
    private lateinit var loading: ImageView

    private var showOriginal = true

    private var historyTracking: ArrayList<Bitmap>? = null
    private var myIdea = Idea()
    private var pixelColor: Int? = null
    private var performanceLevel = Constants.PERFORMANCE_LOW
    private var progress: ProgressDialog? = null
    private var toast: Toast? = null
    private var customDialogs: CustomDialogs? = null
    private var showCamera = false

    private var drawMaskingTape = true
    private var mStartPoint = true
    private var mEndPoint = false
    private val REQUEST_READ_WRITE_PERMISSION = 1

    private var file: File? = null

    private var dX = 0f
    private var dY = 0f

    private var mLastClickTime: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paint)

        visualizerScreen = findViewById(R.id.visualizer_screen)

        toolbarTitle = findViewById(R.id.title_card)
        toolbarTitle.visibility = View.GONE

        imageFromData = findViewById(R.id.imageFromData)
        maskingCanvas = findViewById(R.id.masking_canvas)

        resultLayout = findViewById(R.id.result_layout)
        paintStepsRecycler = findViewById(R.id.paint_steps_recycler)
        topLayout = findViewById(R.id.topLayout)
        inputImage = findViewById(R.id.inputImage)
        greyScaleImage = findViewById(R.id.greyScaleImage)
        middleLayout = findViewById(R.id.middleLayout)
        cannyEdgeImage = findViewById(R.id.cannyEdgeImage)
        floodFillImage = findViewById(R.id.floodFillImage)
        bottomLayout = findViewById(R.id.bottomLayout)
        HSVImageView = findViewById(R.id.HSVImageView)
        HSVImage = findViewById(R.id.HSVImage)
        outputImage = findViewById(R.id.outputImage)

        setViews()
        Utills.changeNavigationBarColor(this, Constants.COLOR_THEME)


        if(intent.hasExtra("bitmap")) {
            originalBitmap = intent.getParcelableExtra("bitmap")!!
            bitmap = originalBitmap
            showImage()
        }

        if(intent.hasExtra(Constants.INTENT_RESIZING))
            resizeImage = intent.getBooleanExtra(Constants.INTENT_RESIZING, true)

        if(intent.hasExtra(Constants.INTENT_CAMERA)){
            var uri = intent.getStringExtra(Constants.INTENT_CAMERA)
            loadFromCamera(Uri.parse(uri))
        } else if(intent.hasExtra(Constants.INTENT_GALLERY)){
            var dat = intent.getStringExtra(Constants.INTENT_GALLERY)
            loadFromGallery(Uri.parse(dat))
        } else if(intent.hasExtra(Constants.INTENT_MY_IDEA)){
            myIdea = Idea()
            myIdea.imageUrl = intent.getStringExtra(Constants.INTENT_MY_IDEA)

            if(intent.hasExtra(Constants.INTENT_MY_IDEA_SHADES)) {
                myIdea.shadesUsed = intent.getSerializableExtra(Constants.INTENT_MY_IDEA_SHADES) as ArrayList<Shade>
                if(myIdea.shadesUsed != null && myIdea.shadesUsed?.size!! > 0)
                    selectedShade = myIdea.shadesUsed?.get(myIdea.shadesUsed?.size!!-1)
            }

            getMaskingPoints()
            loadFromMyIdeas(myIdea.imageUrl)
            displayMasking()
        }

        if(intent.hasExtra(Constants.INTENT_SHADE))
            selectedShade = intent.getSerializableExtra(Constants.INTENT_SHADE) as Shade

//        Utills.transparentToolbar(this, true)

        tl = Point()
    }

    private fun setViews(){

        selectedColorCard = findViewById(R.id.selected_colors_card)
        selectedColorView = findViewById(R.id.selected_color_view)
        selectedColorView.setOnClickListener(this)
        selectedColorLabel = findViewById(R.id.selected_color_label)
        selectedColor = findViewById(R.id.selected_color)
        selectedColor.setOnClickListener(this)
        selectedColorLabel.text = "Selected Color:"

        chosenColor = Color.parseColor(resources.getString(R.string.berger_theme_color_hex))
        selectedColorV = findViewById(R.id.shade)

        if(Constants.SELECTED_SHADE != null)
            selectedShade = Constants.SELECTED_SHADE

        if(selectedShade != null) {
            selectedColor.setColorFilter(Color.parseColor(selectedShade?.shadeCodeHex))
            selectedColorV.setBackgroundColor(Color.parseColor(selectedShade?.shadeCodeHex))
        } else {
            selectedShade = Shade()
            selectedShade?.id = "-1"
            selectedShade?.shadeCodeHex = resources.getString(R.string.berger_theme_color_hex)
            selectedColorV.setBackgroundColor(chosenColor)
        }
        selectedColorName = findViewById(R.id.shade_label)

        rotate = findViewById(R.id.rotate)
        rotate.setOnClickListener(this)
        rotateLabel = findViewById(R.id.rotate_label)
        rotateLabel.setOnClickListener(this)
        finalPicture = findViewById(R.id.final_image)
        finalPicture.setOnClickListener(this)
        finalPicture.setOnTouchListener(this)
        finalPictureLabel = findViewById(R.id.final_image_label)
        finalPictureLabel.setOnTouchListener(this)

        actionBarCard = findViewById(R.id.bottom_bar_card)
        actionBar = findViewById(R.id.bottom_bar)
        clearPaint = findViewById(R.id.clear_paint)
        clearPaint.setOnClickListener(this)
        clearPaintLabel = findViewById(R.id.clear_paint_label)
        clearPaintLabel.setOnClickListener(this)
        undo = findViewById(R.id.undo)
        undo.setOnClickListener(this)
        undoLabel = findViewById(R.id.undo_label)
        undoLabel.setOnClickListener(this)
        originalPicture = findViewById(R.id.original_image)
        originalPicture.setOnClickListener(this)
        pickColor = findViewById(R.id.color_pick)
        pickColor.setOnClickListener(this)
        pickTexture = findViewById(R.id.texture_pick)
        pickTexture.setOnClickListener(this)
        openMasking = findViewById(R.id.open_masking)
        openMasking.setOnClickListener(this)
        openMaskingLabel = findViewById(R.id.open_masking_label)
        saveImage = findViewById(R.id.save_image)
        saveImage.setOnClickListener(this)
        saveImageLabel = findViewById(R.id.save_image_label)
        saveImageLabel.setOnClickListener(this)

        maskingTapeView = findViewById(R.id.masking_tape_view)
        applyMasking = findViewById(R.id.apply_masking)
        applyMasking.setOnClickListener(this)
        applyMaskingLabel = findViewById(R.id.apply_masking_label)
        applyMaskingLabel.setOnClickListener(this)
        clearMasking = findViewById(R.id.clear_masking)
        clearMasking.setOnClickListener(this)
        clearMaskingLabel = findViewById(R.id.clear_masking_label)
        clearMaskingLabel.setOnClickListener(this)

        performanceLow = findViewById(R.id.performance_low)
        performanceLow.setOnClickListener(this)
        performanceMedium = findViewById(R.id.performance_medium)
        performanceMedium.setOnClickListener(this)
        performanceHigh = findViewById(R.id.performance_high)
        performanceHigh.setOnClickListener(this)

        loadingProgress = findViewById(R.id.loading_progress)
        loading = findViewById(R.id.loading)
        Glide
            .with(this)
            .load(R.drawable.berger_loading)
            .into(loading)

        customDialogs = CustomDialogs(this)
//        Utills.changeNavigationBarColor(this, Constants.COLOR_BLACK)

        imageFromData.setOnTouchListener(this)

//            imageFromData.setOnTouchListener { v, event ->
//                if (event.action == MotionEvent.ACTION_DOWN) {
//                    if (touchCount == 0) {
//                        if (maskingTapeView.visibility == View.GONE) {
//                            tl.x = event.x.toDouble()
//                            tl.y = event.y.toDouble()
//                            if (!showCamera) {
//                                if (texture) {
//                                    //                            Thread(Runnable { runOnUiThread { applyTexture(bitmap, tl) } }).start()
//                                    ApplyTextureTask(this@PaintActivity).execute(tl)
//                                } else {
//                                    //                            Thread(Runnable { runOnUiThread { rpPaintHSV(bitmap, tl) } }).start()
//                                    ApplyColorTask(this@PaintActivity).execute(tl)
//                                }
//                            }
//                        } else {
//
//                            if (maskingTapes == null || maskingTapes!!.size == 0) {
//                                maskingTapes = ArrayList()
//
//                                var maskingPoint = MaskingPoint()
//
//                                maskingTapes?.add(maskingPoint!!)
//                            }
//
//                            if (mStartPoint) {
//                                var maskPoint = Point()
//                                maskPoint.x = event.x.toDouble()
//                                maskPoint.y = event.y.toDouble()
//
//                                maskingTapes?.get(maskingTapes!!.size - 1)?.startPoint = maskPoint
//
//                                //                                    mStartPoint = false
//                                //                                    mEndPoint = true
//                            }
//
//                            if (mEndPoint) {
//                                var maskPoint = Point()
//                                maskPoint.x = event.x.toDouble()
//                                maskPoint.y = event.y.toDouble()
//
//                                maskingTapes?.get(maskingTapes!!.size - 1)?.endPoint = maskPoint
//
//                                //                                    mStartPoint = true
//                                //                                    mEndPoint = false
//                            }
//
//                            mStartPoint = !mStartPoint
//                            mEndPoint = !mEndPoint
//                        }
//                    }
//                }
//                true
//            }
    }

//    protected fun setDisplayOrientation(camera: JavaCameraView, angle: Int) {
//        val downPolymorphic: Method
//        try {
//            downPolymorphic = camera.javaClass.getMethod(
//                    "setDisplayOrientation", *arrayOf<Class<*>?>(
//                    Int::class.javaPrimitiveType
//            )
//            )
//            if (downPolymorphic != null) downPolymorphic.invoke(camera, arrayOf<Any>(angle))
//        } catch (e1: java.lang.Exception) {
//            e1.printStackTrace()
//        }
//    }
//
//    private fun createImageFile(): File {
//        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
//        val imageFileName = "IMG_" + timeStamp + "_"
//        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        val image = File.createTempFile(
//                imageFileName, /* prefix */
//                ".jpg", /* suffix */
//                storageDir /* directory */
//        )
//        imageFilePath = image.getAbsolutePath()
//        return image;
//    }

    private fun checkRotatedImage(uri: Uri){
        if(uri?.path != null) {
            try {
                val input: InputStream? = contentResolver.openInputStream(uri!!)

                var ei: ExifInterface? = null
                if (Build.VERSION.SDK_INT > 23 && input != null)
                    ei = ExifInterface(input)
                else
                    ei = ExifInterface(uri.path!!)

                val orientation: Int = ei.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED
                )

                var rotatedBitmap: Bitmap? = null
                rotatedBitmap = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> Utills.rotateImage(bitmap, 90)
                    ExifInterface.ORIENTATION_ROTATE_180 -> Utills.rotateImage(bitmap, 180)
                    ExifInterface.ORIENTATION_ROTATE_270 -> Utills.rotateImage(bitmap, 270)
                    ExifInterface.ORIENTATION_NORMAL -> bitmap
                    else -> bitmap
                }

                if (rotatedBitmap != null) {
                    bitmap = rotatedBitmap
                }

                originalBitmap = bitmap
                resizeImage()
            }
            catch (e: Exception) {
                var i = 0
            }
        }
    }

    private fun loadFromCamera(uri: Uri){
        try {
//                    imageFromData.setImageURI(Uri.parse(imageFilePath))
//                    var bitmap = imageFromData.drawable.toBitmap()
//            bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.contentResolver, uri))
//            } else {
//                MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
//            }
////                    var bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, Uri.parse(imageFilePath))
//            bitmap = getResizedBitmap(bitmap, bitmap.width / 5, bitmap.height / 5)
//            orignalBitmap = bitmap
//            showImage()

//                imageFromData.setImageURI(uri)

//            Glide
//                .with(this)
//                .load(uri.path)
//                .into(imageFromData)

            bitmap = uri.getBitmap(contentResolver)

//            bitmap = Utills.handleSamplingAndRotationBitmap(this, uri)

//            bitmap = imageFromData.drawable.toBitmap()
            originalBitmap = bitmap
            resizeImage()
//            checkRotatedImage(uri)

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun loadFromGallery(uri: Uri) {
        try {
//            imageFromData.setImageURI(uri)

//            bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri))
//            } else {
//                MediaStore.Images.Media.getBitmap(contentResolver, uri)
//            }

            bitmap = uri.getBitmap(contentResolver)

//            bitmap = imageFromData.drawable.toBitmap()
            originalBitmap = bitmap
            resizeImage()
        } catch (e: Exception){
            finish()
        }
    }

    private fun loadFromMyIdeas(url: String?) {
        if(url != null) {
            try {
                imageFromData.setImageURI(Uri.parse(url))
                bitmap = imageFromData.drawable.toBitmap()
                originalBitmap = bitmap
                showImage()
            } catch (e: Exception){
                finish()
            }
        } else {
            finish()
        }
    }

    fun Uri.getBitmap(resolver: ContentResolver): Bitmap {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            @Suppress("DEPRECATION")
            return MediaStore.Images.Media.getBitmap(resolver, this)
        } else {
            // https://developer.android.com/reference/android/graphics/ImageDecoder
            // CvException [org.opencv.core.CvException: OpenCV(4.1.1) /build/master_pack-android/opencv/modules/java/generator/src/cpp/utils.cpp:38: error: (-215:Assertion failed) AndroidBitmap_lockPixels(env, bitmap, &pixels) >= 0
            /*
              By default, a Bitmap created by ImageDecoder (including one that is inside a Drawable)
              will be immutable (i.e. Bitmap#isMutable returns false), and it will typically
              have Config Bitmap.Config#HARDWARE. Although these properties can be changed
              with setMutableRequired(true)
             */
            val source = ImageDecoder.createSource(resolver, this)
            return ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.isMutableRequired = true
            }
        }
    }

    private fun setImageViewRatio(bitmap: Bitmap){
//        val set = ConstraintSet()
//        set.clone(imageFromData.layoutParams)
//        set.setDimensionRatio(imageFromData.getId(), "16:9")
//        set.applyTo(imageFilePath)

        val ratio = bitmap.width.toFloat().toString() + ":" + bitmap.height.toFloat().toString()

        (imageFromData.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio = ratio
        imageFromData.invalidate()
    }

    private fun rotateImage(){
        bitmap = Utills.rotateImage(bitmap, 270)
        originalBitmap = Utills.rotateImage(originalBitmap, 270)
        showImage()
    }

    private fun resizeImage(){
        if(resizeImage) {
            bitmap = getResizedBitmap(
                originalBitmap,
                originalBitmap.width / performanceLevel,
                originalBitmap.height / performanceLevel
            )
        } else {
            bitmap = originalBitmap
        }
        historyTracking?.clear()
        drawMaskingTape = true
        blurImage(bitmap)
        showImage()
    }

    fun saveImage(image: Bitmap) {
//        val pictureFile = createImageFile()
//        if (pictureFile == null) {
//            Log.e(TAG, "Error creating media file, check storage permissions: ")
//            return
//        }
//        try {
//            val fos = FileOutputStream(pictureFile);
//            image.compress(Bitmap.CompressFormat.PNG, 90, fos)
//            fos.close()
//        } catch (e: FileNotFoundException) {
//            Log.e(TAG, "File not found: " + e.message)
//        } catch (e: IOException) {
//            Log.e(TAG, "Error accessing file: " + e.message)
//        }
    }

    fun updateBitmap(image: Mat) {
        try {
            val mBitmap = Bitmap.createBitmap(image.cols(), image.rows(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(image, mBitmap)
            bitmap = mBitmap
            addToHistory(mBitmap)
        } catch (e: Exception){
            var s = 0
        }
    }

    fun showImage(image: Mat, view: ImageView) {
        try {
            val mBitmap = Bitmap.createBitmap(image.cols(), image.rows(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(image, mBitmap)
            view.setImageBitmap(mBitmap)
//            view.adjustViewBounds = true
//            view.invalidate()
            bitmap = mBitmap
            saveImage(bitmap)
        } catch (e: Exception){}
    }
    fun showResultLayouts() {
        imageFromData.visibility = View.VISIBLE
    }
    fun showImage() {
        imageFromData.visibility = View.VISIBLE
        resultLayout.visibility = View.GONE

        showOriginal = true
        try {

            setImageViewRatio(bitmap)
            imageFromData.setImageBitmap(bitmap)
//            imageFromData.adjustViewBounds = true
//            imageFromData.invalidate()

            finalPicture.setImageResource(R.drawable.icon_original)
            finalPictureLabel.text = resources.getString(R.string.paint_button_original_picture)
        } catch (e: Exception) {
            Toast.makeText(this@PaintActivity, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }
    private fun showOriginalImage() {
        imageFromData.visibility = View.VISIBLE
//        resultLayout.visibility = View.VISIBLE

        showOriginal = false
        finalPicture.setImageResource(R.drawable.icon_painted)
        finalPictureLabel.text = resources.getString(R.string.paint_button_painted_picture)
        try {
            imageFromData.setImageBitmap(originalBitmap)
//            imageFromData.adjustViewBounds = true
//            imageFromData.invalidate()
        } catch (e: Exception) {
            Toast.makeText(this@PaintActivity, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }
    private fun chooseColor() {
        texture = false
//        val colorPicker = AmbilWarnaDialog(
//                this@PaintActivity,
//                chosenColor,
//                object : AmbilWarnaDialog.OnAmbilWarnaListener {
//                    override fun onCancel(dialog: AmbilWarnaDialog) {
//                    }
//
//                    override fun onOk(dialog: AmbilWarnaDialog, color: Int) {
//                        chosenColor = color
//                        selectedColorLabel.text = "Selected Color:"
//                        selectedColor.setColorFilter(chosenColor)
//                        selectedColorV.setBackgroundColor(chosenColor)
////                bitmap = orignalBitmap
//                    }
//                })
//        colorPicker.show()

        var shades = Intent(this, ColorShadesDisplayActivity::class.java)
        shades.putExtra("select", true)
//        shades.putExtra(Constants.INTENT_PRODUCTS, true)
        shades.putExtra(Constants.INTENT_FROM_VISUALIZER, true)
        if(Constants.SELECTED_PRODUCT_ID != null)
            shades.putExtra(Constants.INTENT_PRODUCT_ID, Constants.SELECTED_PRODUCT_ID)
        startActivity(shades)
    }

    private fun setSelectedColor(){
        if(selectedShade != null) {
            chosenColor = Color.parseColor(selectedShade?.shadeCodeHex)
            var name = ""

            if(!selectedShade?.shadeName.isNullOrEmpty())
                name = selectedShade?.shadeName!!

            if(!selectedShade?.shadeCode.isNullOrEmpty())
                name += " " + selectedShade?.shadeCode

            selectedColorName.text = name
            selectedColorV.setBackgroundColor(chosenColor)
        }
    }

    private fun chooseTexture() {
        texture = true
        selectedColorLabel.text = "Selected Texture:"
        selectedColor.clearColorFilter()
        selectedColorV.setBackgroundColor(Color.WHITE)
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
        val resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true)
        return resizedBitmap
    }
    fun getTextureImage(): Mat {
        var textureImage = BitmapFactory.decodeResource(
                getResources(),
            R.drawable.texture_small_brick_red
        )
        textureImage = getResizedBitmap(textureImage, bitmap.width, bitmap.height)
        val texture = Mat()
        Utils.bitmapToMat(textureImage, texture)
        Imgproc.cvtColor(texture, texture, Imgproc.COLOR_RGBA2RGB)
        return texture
    }

//    private fun elementToPoints(element: Element): List<MatOfPoint>? {
//        val points: MutableList<Point> = ArrayList()
//        points.add(Point(element.x, element.y))
//        points.add(Point(element.x + element.w, element.y))
//        points.add(Point(element.x + element.w, element.y + element.h))
//        points.add(Point(element.x, element.y + element.h))
//        val mPoints = MatOfPoint()
//        mPoints.fromList(points)
//        val finalPoints: MutableList<MatOfPoint> = ArrayList()
//        finalPoints.add(mPoints)
//        return finalPoints
//    }

//    fun drawMaskings(drawOn: Mat): Mat{
//
//        if(maskingTapes == null)
//            maskingTapes = ArrayList()
//
//        for(tape in maskingTapes!!) {
//
//            if(tape.startPoint != null && tape.endPoint != null) {
//                val startPoint = getCorrectPoint(tape.startPoint!!.clone(), drawOn)
//                val endPoint = getCorrectPoint(tape.endPoint!!.clone(), drawOn)
//
//                Imgproc.line(drawOn, startPoint, endPoint, Scalar(255.0, 255.0, 255.0), 2)
////                Imgproc.line(drawOn, startPoint, endPoint, Scalar(255.0, 255.0, 255.0), 1, Core.LINE_AA,0)
//            }
//        }
//
//        return drawOn
//    }

    fun drawMaskings(drawOn: Mat): Mat{

        if(maskingTapes == null)
            maskingTapes = ArrayList()

        var startP: Point? = null
        var endP: Point? = null

        for (i in maskingTapes!!.indices) {

            if (maskingTapes?.get(i)?.startPoint != null) {
                val endPoint = getCorrectPoint(maskingTapes?.get(i)?.startPoint!!.clone(), drawOn)

                if (i > 0)
                    Imgproc.line(drawOn, startP, endPoint, Scalar(255.0, 255.0, 255.0, 255.0), 2)
//                Imgproc.line(drawOn, startPoint, endPoint, Scalar(255.0, 255.0, 255.0), 1, Core.LINE_AA,0)
                startP = endPoint
            }
        }

//        drawMaskingTape = false

        return drawOn
    }

    fun drawCircle(p: Point){
        var cP = getCorrectPoint(p, mat)
//        var paint = Color.parseColor(selectedShade?.shadeCodeHex)

//        Imgproc.circle(mat, cP, 5, Scalar(255.0,0.0,0.0), FILLED)
//        Imgproc.circle(mat, cP, 5, Scalar(255.0,0.0,0.0), FILLED, LINE_8)
//        Imgproc.circle(mat, cP, 63, Scalar(0.0, 255.0, 0.0, 255.0), -1, 8, 0)

        Imgproc.circle(mat, cP, 10, Scalar(
            Color.red(chosenColor).toDouble(),
            Color.green(chosenColor).toDouble(),
            Color.blue(chosenColor).toDouble(),
            255.0),-1, 8, 0)

//        Imgproc.circle(mat, cP, 10, Scalar(
//            Integer.valueOf(selectedShade?.shadeCodeHex!!.substring(1, 3), 16).toDouble(),
//            Integer.valueOf(selectedShade?.shadeCodeHex!!.substring(3, 5), 16).toDouble(),
//            Integer.valueOf(selectedShade?.shadeCodeHex!!.substring(5, 7), 16).toDouble(), 255.0)
//            ,-1, 8, 0)

//        Imgproc.drawMarker(mat, cP, Scalar(150.0,150.0,255.0), MARKER_SQUARE)
//        Imgproc.circle(mat, cP, 5, Scalar(
//            Integer.valueOf(selectedShade?.shadeCodeHex!!.substring(5, 7), 16).toDouble(),
//            Integer.valueOf(selectedShade?.shadeCodeHex!!.substring(3, 5), 16).toDouble(),
//            Integer.valueOf(selectedShade?.shadeCodeHex!!.substring(1, 3), 16).toDouble())
//            ,10)
        showImage(mat, imageFromData)
    }

    private fun getCorrectPoint(point: Point, drawOn: Mat): Point {
//        val displayMetrics = DisplayMetrics()
//        windowManager.defaultDisplay.getMetrics(displayMetrics)
//        var height = displayMetrics.heightPixels
//        var width = displayMetrics.widthPixels

//        val location = IntArray(2)
//        imageFromData.getLocationOnScreen(location)
//        val x = location[0]
//        val y = location[1]
//
//        height -= x
//        width -= y

//        var Measuredwidth = 0
//        var Measuredheight = 0
//        val size = android.graphics.Point()
//        val w = windowManager
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            w.defaultDisplay.getSize(size)
//            Measuredwidth = size.x.toInt()
//            Measuredheight = size.y.toInt()
//        } else {
//            val d = w.defaultDisplay
//            Measuredwidth = d.width
//            Measuredheight = d.height
//        }

//        Measuredheight -= Measuredheight%20


//        Measuredheight -= (getDP(selectedColorCard.height) + getDP(actionBarCard.height)) // + getDP(30)
//        Measuredwidth -= getDP(20)

//        val wid = getDP(imageFromData.width)
//        val hgt = getDP(imageFromData.height)

        val widthPercent = (point.x * 100) / imageFromData.width
        val heightPercent = (point.y * 100) / imageFromData.height

        point.x = (widthPercent * drawOn.width()) / 100
        point.y = (heightPercent * drawOn.height()) / 100

//        //---------------------------------------------------------------
//        var cols = drawOn.cols()
//        var rows = drawOn.rows()
//
//        var xOffset = (imageFromData.width - cols) / 2
//        var yOffset = (imageFromData.height - rows) / 2
//
//        point.x = point.x - xOffset
//        point.y = point.y - yOffset
//        //---------------------------------------------------------------

//        point.x = (point.x.times(drawOn.width())) / imageFromData.width
//        point.y = (point.y.times(drawOn.height())) / imageFromData.height

//        point.x = point.x.times((drawOn.width() / width.toDouble()))
//        point.y = point.y.times((drawOn.height() / height.toDouble()))

        return point
    }

    private fun getCorrectPoint(point: Point, drawOn: Bitmap): Point {

        val widthPercent = (point.x * 100) / imageFromData.width
        val heightPercent = (point.y * 100) / imageFromData.height

        point.x = (widthPercent * drawOn.width) / 100
        point.y = (heightPercent * drawOn.height) / 100

        return point
    }

    private fun getDP(px: Int): Int {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            px.toFloat(),
            displayMetrics
        ).toInt()
    }

    private fun blurImage(bitmap: Bitmap){
        val mRgbMat = Mat()
        Utils.bitmapToMat(bitmap, mRgbMat)
        Imgproc.medianBlur(mRgbMat, mRgbMat, 3)
        updateBitmap(mRgbMat)
    }

    fun paintWall(
        bitmap: Bitmap,
        p: Point
    ): Mat? {
        val cannyMinThres = 30.0
        val ratio = 2.5

        var mRgbMat = Mat()
        Utils.bitmapToMat(bitmap, mRgbMat)

        Imgproc.cvtColor(mRgbMat, mRgbMat, Imgproc.COLOR_RGBA2RGB)
        val mask = Mat(
            Size(
                (mRgbMat.cols() / 8.0f).toDouble(),
                (mRgbMat.rows() / 8.0f).toDouble()
            ), CvType.CV_8UC1, Scalar(0.0)
        )
        val img = Mat()
        mRgbMat.copyTo(img)
        //grayscale
        val mGreyScaleMat = Mat()
        Imgproc.cvtColor(mRgbMat, mGreyScaleMat, Imgproc.COLOR_RGB2GRAY, 3)
        Imgproc.medianBlur(mGreyScaleMat, mGreyScaleMat, 3)
        val cannyGreyMat = Mat()
        Imgproc.Canny(mGreyScaleMat, cannyGreyMat, cannyMinThres, cannyMinThres * ratio, 3)
        //ShowImage(cannyGreyMat);
        //hsv
        val hsvImage = Mat()
        Imgproc.cvtColor(img, hsvImage, Imgproc.COLOR_RGB2HSV)
        //got the hsv values
        val list = ArrayList<Mat>(3)
        Core.split(hsvImage, list)
        val sChannelMat = Mat()
        val slist = ArrayList<Mat>()
        slist.add(list[1])
        Core.merge(slist, sChannelMat)
        Imgproc.medianBlur(sChannelMat, sChannelMat, 3)
        // canny
        val cannyMat = Mat()
        Imgproc.Canny(sChannelMat, cannyMat, cannyMinThres, cannyMinThres * ratio, 3)
        //ShowImage(cannyMat);
        Core.addWeighted(cannyMat, 0.5, cannyGreyMat, 0.5, 0.0, cannyMat)
        Imgproc.dilate(cannyMat, cannyMat, mask, Point(0.0, 0.0), 5)
        // Make sure to resize the cannyMat or it'll throw an error
        Imgproc.resize(cannyMat, cannyMat, Size(cannyMat.width() + 2.0, cannyMat.height() + 2.0))
        Imgproc.medianBlur(mRgbMat, mRgbMat, 15)
        //ShowImage(mRgbMat);
        val p1 = getCorrectPoint(p, mRgbMat)

        val seedPoint = Point(p1.x, p1.y)
        val floodFillFlag = 4


        Imgproc.floodFill(
            mRgbMat,
            cannyMat,
            seedPoint,
            Scalar(
                Color.red(chosenColor).toDouble(),
                Color.green(chosenColor).toDouble(),
                Color.blue(chosenColor).toDouble(),
                255.0
            ),
            Rect(0, 0, 0, 0),
            Scalar(2.0, 2.0, 2.0, 2.0),
            Scalar(2.0, 2.0, 2.0, 2.0),
            floodFillFlag
        )
        Imgproc.dilate(mRgbMat, mRgbMat, mask, Point(0.0, 0.0), 5)
        //got the hsv of the mask image
        val rgbHsvImage = Mat()
        Imgproc.cvtColor(mRgbMat, rgbHsvImage, Imgproc.COLOR_RGB2HSV)
        val list1 = ArrayList<Mat>(3)
        Core.split(rgbHsvImage, list1)
        //merged the “v” of original image with mRgb mat
        val result = Mat()

        Core.merge(listOf(list1[0], list1[1], list[2]), result)
        // converted to rgb
        Imgproc.cvtColor(result, result, Imgproc.COLOR_HSV2RGB)
//        Core.addWeighted(result, 0.7, img, 0.3, 0.0, result)
        updateBitmap(result)
        return result
    }

    fun paintWall2(bitmap: Bitmap, p: Point): Mat {
        val cannyMinThres = 30.0
        val ratio = 2.5

        var mRgbMat = Mat()
        Utils.bitmapToMat(bitmap, mRgbMat)

        var m = Mat(mRgbMat.rows(), mRgbMat.cols(), CvType.CV_8UC1)
        var r = Rect()

        val list = ArrayList<Mat>(3)
        Core.split(mRgbMat, list)
        val sChannelMat = Mat()
        Core.merge(listOf(list[1]), sChannelMat)
        Imgproc.medianBlur(sChannelMat, sChannelMat, 3)

        addPaintStep(list[0], "List0")
        addPaintStep(list[1], "List1")
        addPaintStep(list[2], "List2")

        addPaintStep(sChannelMat, "Flood Fill Image")
        // canny
        val cannyMat = Mat()
        Imgproc.Canny(sChannelMat, cannyMat, cannyMinThres, cannyMinThres * ratio, 3)

//        Imgproc.floodFill(
//            mRgbMat,
//            m,
//            new Point (60, 250
//        ),
//        Scalar (255),//to fill the designated area with color(255)
//        r,//the rect that will encompass the designated area
//        new Scalar (216),//lower bound
//        new Scalar (218),//upper bound
//        Imgproc.FLOODFILL_FIXED_RANGE);
        val p1 = getCorrectPoint(p, mRgbMat)

        Imgproc.floodFill(
            mRgbMat,
            m,
            p1,
            Scalar(Color.red(chosenColor).toDouble(),
                Color.green(chosenColor).toDouble(),
                Color.blue(chosenColor).toDouble()),
            r,
            Scalar(5.0, 5.0, 5.0),
            Scalar(5.0, 5.0, 5.0),
            Imgproc.FLOODFILL_FIXED_RANGE
        )

        updateBitmap(mRgbMat)

        return mRgbMat
    }

    fun paintDynamic(bitmap: Bitmap, p: Point): Mat {
        val cannyMinThres = 30.0
        val ratio = 2.5
        paintSteps = ArrayList()

        // show intermediate step results
        // grid created here to do that
        showResultLayouts()

        var mRgbMat = Mat()
        Utils.bitmapToMat(bitmap, mRgbMat)

//        if(drawMaskingTape) {
//            try {
////            drawMaskingTape = false
//                mRgbMat = drawMaskings(mRgbMat)
//            } catch (e: Exception) {}
//        }

        addPaintStep(mRgbMat.clone(), "Input Image")
        Imgproc.cvtColor(mRgbMat, mRgbMat, Imgproc.COLOR_RGBA2RGB)

        addPaintStep(mRgbMat.clone(), "Image")
        val mask = Mat(
            Size(mRgbMat.width() + 2.0, mRgbMat.height() + 2.0), CvType.CV_8UC1,
            Scalar(0.0)
        )

        addPaintStep(mask.clone(), "Mask")
        // Imgproc.dilate(mRgbMat, mRgbMat,mask, Point(0.0,0.0), 5)
        val img = Mat()
        mRgbMat.copyTo(img)
        // grayscale
        var mGreyScaleMat = Mat()
        Imgproc.cvtColor(mRgbMat, mGreyScaleMat, Imgproc.COLOR_RGB2GRAY, 3)

        addPaintStep(mGreyScaleMat.clone(), "GreyS")

        if(drawMaskingTape) {
            try {
//            drawMaskingTape = false
                mGreyScaleMat = drawMaskings(mGreyScaleMat)
            } catch (e: Exception) {}
        }

        addPaintStep(mGreyScaleMat.clone(), "GreyScaleMasking")

        Imgproc.medianBlur(mGreyScaleMat, mGreyScaleMat, 3)

        addPaintStep(mGreyScaleMat.clone(), "GreyScaleBlur")

        val cannyGreyMat = Mat()
        Imgproc.Canny(mGreyScaleMat, cannyGreyMat, cannyMinThres, cannyMinThres * ratio, 3)
        addPaintStep(cannyGreyMat, "GreyScaleMatCanny")
        //hsv
        val hsvImage = Mat()
        Imgproc.cvtColor(img, hsvImage, Imgproc.COLOR_RGB2HSV)

        addPaintStep(hsvImage, "HSVImage")

        //got the hsv values
        val list = ArrayList<Mat>(3)
        Core.split(hsvImage, list)
        val sChannelMat = Mat()
        Core.merge(listOf(list[1]), sChannelMat)
        Imgproc.medianBlur(sChannelMat, sChannelMat, 3)

        addPaintStep(list[0], "List0")
        addPaintStep(list[1], "List1")
        addPaintStep(list[2], "List2")

        addPaintStep(sChannelMat, "Flood Fill Image")
        // canny
        val cannyMat = Mat()
        Imgproc.Canny(sChannelMat, cannyMat, cannyMinThres, cannyMinThres * ratio, 3)

        addPaintStep(cannyMat, "HSVImage")
        Core.addWeighted(cannyMat, 0.5, cannyGreyMat, 0.5, 0.0, cannyMat)
        Imgproc.dilate(cannyMat, cannyMat, mask, Point(0.0, 0.0), 5)

        addPaintStep(mask, "Mask1")

        addPaintStep(cannyMat, "cannyEdgeImage")

        val p1 = getCorrectPoint(p, mRgbMat)

        val seedPoint = Point(p1.x, p1.y)

        Imgproc.resize(cannyMat, cannyMat, Size(cannyMat.width() + 2.0, cannyMat.height() + 2.0))
        Imgproc.medianBlur(mRgbMat, mRgbMat, 15)

        addPaintStep(mRgbMat, "mRgbMat")
        addPaintStep(cannyMat, "cannyMat")

        val floodFillFlag = Imgproc.CV_WARP_FILL_OUTLIERS
//        val floodFillFlag = 8

//        val wallMask = Mat(mRgbMat.size(),mRgbMat.type())
//
//        val cannyMat1 = Mat()
//        cannyMat.copyTo(cannyMat1)
//
//        Imgproc.floodFill(
//            wallMask,
//            cannyMat1,
//            seedPoint,
//            Scalar(Color.red(chosenColor).toDouble(),
//                Color.green(chosenColor).toDouble(),
//                Color.blue(chosenColor).toDouble()),
//            Rect(),
//            Scalar(5.0, 5.0, 5.0),
//            Scalar(5.0, 5.0, 5.0),
//            floodFillFlag
//        )

        Imgproc.floodFill(
            mRgbMat,
            cannyMat,
            seedPoint,
            Scalar(
                Color.red(chosenColor).toDouble(),
                Color.green(chosenColor).toDouble(),
                Color.blue(chosenColor).toDouble(),
                255.0
            ),
            Rect(50, 50, 500, 500),
            Scalar(5.0, 5.0, 5.0,  5.0),
            Scalar(5.0, 5.0, 5.0, 5.0),
            floodFillFlag
        )
        addPaintStep(mRgbMat, "floodFillImage2")
        addPaintStep(cannyMat, "cannyMat2")
        Imgproc.dilate(mRgbMat, mRgbMat, mask, Point(0.0, 0.0), 5)
        addPaintStep(mask, "Mask1")

        addPaintStep(mRgbMat, "mRgbMat")

        //got the hsv of the mask image
//        val rgbHsvImage = mRgbMat.clone()
        val rgbHsvImage = Mat()
        Imgproc.cvtColor(mRgbMat, rgbHsvImage, Imgproc.COLOR_RGB2HSV)

        addPaintStep(rgbHsvImage, "rgbHsvImage")

        val list1 = ArrayList<Mat>(3)
        Core.split(rgbHsvImage, list1)

        addPaintStep(list1[0], "list10")
        addPaintStep(list1[1], "list11")
        addPaintStep(list1[2], "list12")

        //merged the “v” of original image with mRgb mat
//        val result = mRgbMat
        val result = Mat()
        Core.merge(listOf(list1[0], list1[1], list[2]), result)
//
        addPaintStep(list1[0], "list10")
        addPaintStep(list1[1], "list11")
        addPaintStep(list1[2], "list12")

        // converted to rgb
        Imgproc.cvtColor(result, result, Imgproc.COLOR_HSV2RGB)

//        addPaintStep(result, "Result")
//        Imgproc.threshold(result, result, 0.0, 255.0, Imgproc.THRESH_OTSU)
        addPaintStep(result, "Result")
        populatePaintSteps()
        updateBitmap(result)

        addShadeInIdea()

        getCurrentPixelColor(p)
//        getPixelColor(p1, result)

        return result
    }

    fun paintStatic(bitmap: Bitmap, p: Point): Mat {
        val cannyMinThres = 30.0
        val ratio = 2.5
        paintSteps = ArrayList()

        // show intermediate step results
        // grid created here to do that
        showResultLayouts()

        var mRgbMat = Mat()
        Utils.bitmapToMat(bitmap, mRgbMat)

//        if(drawMaskingTape) {
//            try {
////            drawMaskingTape = false
//                mRgbMat = drawMaskings(mRgbMat)
//            } catch (e: Exception) {}
//        }

        addPaintStep(mRgbMat.clone(), "Input Image")
        Imgproc.cvtColor(mRgbMat, mRgbMat, Imgproc.COLOR_RGBA2RGB)

        addPaintStep(mRgbMat.clone(), "Image")
        val mask = Mat(
            Size(mRgbMat.width() + 2.0, mRgbMat.height() + 2.0), CvType.CV_8UC1,
            Scalar(0.0)
        )

        addPaintStep(mask.clone(), "Mask")
        // Imgproc.dilate(mRgbMat, mRgbMat,mask, Point(0.0,0.0), 5)
        val img = Mat()
        mRgbMat.copyTo(img)
        // grayscale
        var mGreyScaleMat = Mat()
        Imgproc.cvtColor(mRgbMat, mGreyScaleMat, Imgproc.COLOR_RGB2GRAY, 3)

        addPaintStep(mGreyScaleMat.clone(), "GreyS")

        if(drawMaskingTape) {
            try {
//            drawMaskingTape = false
                mGreyScaleMat = drawMaskings(mGreyScaleMat)
            } catch (e: Exception) {}
        }

        addPaintStep(mGreyScaleMat.clone(), "GreyScaleMasking")

        Imgproc.medianBlur(mGreyScaleMat, mGreyScaleMat, 3)

        addPaintStep(mGreyScaleMat.clone(), "GreyScaleBlur")

        val cannyGreyMat = Mat()
        Imgproc.Canny(mGreyScaleMat, cannyGreyMat, cannyMinThres, cannyMinThres * ratio, 3)
        addPaintStep(cannyGreyMat, "GreyScaleMatCanny")
        //hsv
        val hsvImage = Mat()
        Imgproc.cvtColor(img, hsvImage, Imgproc.COLOR_RGB2HSV)

        addPaintStep(hsvImage, "HSVImage")

        //got the hsv values
        val list = ArrayList<Mat>(3)
        Core.split(hsvImage, list)
        val sChannelMat = Mat()
        Core.merge(listOf(list[1]), sChannelMat)
        Imgproc.medianBlur(sChannelMat, sChannelMat, 3)

        addPaintStep(list[0], "List0")
        addPaintStep(list[1], "List1")
        addPaintStep(list[2], "List2")

        addPaintStep(sChannelMat, "Flood Fill Image")
        // canny
        val cannyMat = Mat()
        Imgproc.Canny(sChannelMat, cannyMat, cannyMinThres, cannyMinThres * ratio, 3)

        addPaintStep(cannyMat, "HSVImage")
        Core.addWeighted(cannyMat, 0.5, cannyGreyMat, 0.5, 0.0, cannyMat)
        Imgproc.dilate(cannyMat, cannyMat, mask, Point(0.0, 0.0), 5)

        addPaintStep(mask, "Mask1")

        addPaintStep(cannyMat, "cannyEdgeImage")

        val p1 = getCorrectPoint(p, mRgbMat)

        val seedPoint = Point(p1.x, p1.y)

        Imgproc.resize(cannyMat, cannyMat, Size(cannyMat.width() + 2.0, cannyMat.height() + 2.0))
        Imgproc.medianBlur(mRgbMat, mRgbMat, 1)

        addPaintStep(mRgbMat, "mRgbMat")
        addPaintStep(cannyMat, "cannyMat")

        val floodFillFlag = Imgproc.CV_WARP_FILL_OUTLIERS
//        val floodFillFlag = 8

//        val wallMask = Mat(mRgbMat.size(),mRgbMat.type())
//
//        val cannyMat1 = Mat()
//        cannyMat.copyTo(cannyMat1)
//
//        Imgproc.floodFill(
//            wallMask,
//            cannyMat1,
//            seedPoint,
//            Scalar(Color.red(chosenColor).toDouble(),
//                Color.green(chosenColor).toDouble(),
//                Color.blue(chosenColor).toDouble()),
//            Rect(),
//            Scalar(5.0, 5.0, 5.0),
//            Scalar(5.0, 5.0, 5.0),
//            floodFillFlag
//        )

        Imgproc.floodFill(
            mRgbMat,
            cannyMat,
            seedPoint,
            Scalar(
                Color.red(chosenColor).toDouble(),
                Color.green(chosenColor).toDouble(),
                Color.blue(chosenColor).toDouble(),
                255.0
            ),
            Rect(50, 50, 500, 500),
            Scalar(5.0, 5.0, 5.0,  5.0),
            Scalar(5.0, 5.0, 5.0, 5.0),
            floodFillFlag
        )
        addPaintStep(mRgbMat, "floodFillImage2")
        addPaintStep(cannyMat, "cannyMat2")
        Imgproc.dilate(mRgbMat, mRgbMat, mask, Point(0.0, 0.0), 5)
        addPaintStep(mask, "Mask1")

        addPaintStep(mRgbMat, "mRgbMat")

        //got the hsv of the mask image
//        val rgbHsvImage = mRgbMat.clone()
        val rgbHsvImage = Mat()
        Imgproc.cvtColor(mRgbMat, rgbHsvImage, Imgproc.COLOR_RGB2HSV)

        addPaintStep(rgbHsvImage, "rgbHsvImage")

        val list1 = ArrayList<Mat>(3)
        Core.split(rgbHsvImage, list1)

        addPaintStep(list1[0], "list10")
        addPaintStep(list1[1], "list11")
        addPaintStep(list1[2], "list12")

        //merged the “v” of original image with mRgb mat
        val result = mRgbMat
//        val result = Mat()
//        Core.merge(listOf(list1[0], list1[1], list[2]), result)
//
//        addPaintStep(list1[0], "list10")
//        addPaintStep(list1[1], "list11")
//        addPaintStep(list1[2], "list12")

        // converted to rgb
//        Imgproc.cvtColor(result, result, Imgproc.COLOR_HSV2RGB)

//        addPaintStep(result, "Result")
//        Imgproc.threshold(result, result, 0.0, 255.0, Imgproc.THRESH_OTSU)
        addPaintStep(result, "Result")
        populatePaintSteps()
        updateBitmap(result)

        addShadeInIdea()

        getCurrentPixelColor(p)
//        getPixelColor(p1, result)

        return result
    }

    fun paintDynamicNew(bitmap: Bitmap, p: Point): Mat {
        val cannyMinThres = 30.0
        val ratio = 2.5
        paintSteps = ArrayList()

        showResultLayouts()

        // Convert Bitmap → Mat
        val rgbMat = Mat()
        Utils.bitmapToMat(bitmap, rgbMat)
        Imgproc.cvtColor(rgbMat, rgbMat, Imgproc.COLOR_RGBA2RGB)
        addPaintStep(rgbMat.clone(), "Input Image")

        // Convert to grayscale for edge detection
        val grayMat = Mat()
        Imgproc.cvtColor(rgbMat, grayMat, Imgproc.COLOR_RGB2GRAY)
        Imgproc.medianBlur(grayMat, grayMat, 3)

        // Optional masking tape logic
        if (drawMaskingTape) {
            try {
                drawMaskings(grayMat).copyTo(grayMat)
            } catch (_: Exception) {}
        }

        // Edge detection
        val edges = Mat()
        Imgproc.Canny(grayMat, edges, cannyMinThres, cannyMinThres * ratio, 3)
        Imgproc.dilate(edges, edges, Mat(), Point(0.0, 0.0), 2)
        addPaintStep(edges.clone(), "Edges")

        // Prepare mask (must be 1 pixel larger than image)
        val mask = Mat(
            Size(rgbMat.width().toDouble() + 2.0, rgbMat.height().toDouble() + 2.0),
            CvType.CV_8UC1,
            Scalar(0.0)
        )

        // Seed point for fill
        val seedPoint = getCorrectPoint(p, rgbMat)

        // Prepare target color (convert ARGB → Scalar)
        val targetColor = Scalar(
            Color.red(chosenColor).toDouble(),
            Color.green(chosenColor).toDouble(),
            Color.blue(chosenColor).toDouble()
        )

        // Flood fill flags
        val floodFlags = Imgproc.FLOODFILL_MASK_ONLY or Imgproc.FLOODFILL_FIXED_RANGE or (255 shl 8)

        // Run flood fill to get mask region (not coloring yet)
        Imgproc.floodFill(
            rgbMat.clone(), // temp copy for detection
            mask,
            seedPoint,
            Scalar(255.0, 255.0, 255.0),
            Rect(),
            Scalar(5.0, 5.0, 5.0),
            Scalar(5.0, 5.0, 5.0),
            floodFlags
        )

        // Crop mask back to image size
        val paintedMask = mask.submat(1, rgbMat.rows() + 1, 1, rgbMat.cols() + 1)

        // Apply paint only to masked area
        val colorMat = Mat(rgbMat.size(), rgbMat.type(), targetColor)
        colorMat.copyTo(rgbMat, paintedMask)

        // Light blend with original image to keep texture
        val blended = Mat()
        Core.addWeighted(rgbMat, 0.7, bitmapToMat(bitmap), 0.3, 0.0, blended)

        addPaintStep(blended.clone(), "Painted Result")

        populatePaintSteps()
        updateBitmap(blended)
        addShadeInIdea()
        getCurrentPixelColor(p)

        return blended
    }

    // Helper to convert bitmap → Mat for blending
    private fun bitmapToMat(bitmap: Bitmap): Mat {
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2RGB)
        return mat
    }




    fun rpPaintHSV(bitmap: Bitmap, p: Point): Mat {
        val cannyMinThres = 30.0
        val ratio = 2.5
        paintSteps = ArrayList()

        // show intermediate step results
        // grid created here to do that
        showResultLayouts()

        var mRgbMat = Mat()
        Utils.bitmapToMat(bitmap, mRgbMat)

//        if(drawMaskingTape) {
//            try {
////            drawMaskingTape = false
//                mRgbMat = drawMaskings(mRgbMat)
//            } catch (e: Exception) {}
//        }

        addPaintStep(mRgbMat.clone(), "Input Image")
        Imgproc.cvtColor(mRgbMat, mRgbMat, Imgproc.COLOR_RGBA2RGB)

        addPaintStep(mRgbMat.clone(), "Image")
        val mask = Mat(
                Size(mRgbMat.width() + 2.0, mRgbMat.height() + 2.0), CvType.CV_8UC1,
                Scalar(0.0)
        )

        addPaintStep(mask.clone(), "Mask")
        // Imgproc.dilate(mRgbMat, mRgbMat,mask, Point(0.0,0.0), 5)
        val img = Mat()
        mRgbMat.copyTo(img)
        // grayscale
        var mGreyScaleMat = Mat()
        Imgproc.cvtColor(mRgbMat, mGreyScaleMat, Imgproc.COLOR_RGB2GRAY, 3)

        addPaintStep(mGreyScaleMat.clone(), "GreyS")

        if(drawMaskingTape) {
            try {
//            drawMaskingTape = false
                mGreyScaleMat = drawMaskings(mGreyScaleMat)
            } catch (e: Exception) {}
        }

        addPaintStep(mGreyScaleMat.clone(), "GreyScaleMasking")

        Imgproc.medianBlur(mGreyScaleMat, mGreyScaleMat, 3)

        addPaintStep(mGreyScaleMat.clone(), "GreyScaleBlur")

        val cannyGreyMat = Mat()
        Imgproc.Canny(mGreyScaleMat, cannyGreyMat, cannyMinThres, cannyMinThres * ratio, 3)
        addPaintStep(cannyGreyMat, "GreyScaleMatCanny")
        //hsv
        val hsvImage = Mat()
        Imgproc.cvtColor(img, hsvImage, Imgproc.COLOR_RGB2HSV)

        addPaintStep(hsvImage, "HSVImage")

        //got the hsv values
        val list = ArrayList<Mat>(3)
        Core.split(hsvImage, list)
        val sChannelMat = Mat()
        Core.merge(listOf(list[1]), sChannelMat)
        Imgproc.medianBlur(sChannelMat, sChannelMat, 3)

        addPaintStep(list[0], "List0")
        addPaintStep(list[1], "List1")
        addPaintStep(list[2], "List2")

        addPaintStep(sChannelMat, "Flood Fill Image")
        // canny
        val cannyMat = Mat()
        Imgproc.Canny(sChannelMat, cannyMat, cannyMinThres, cannyMinThres * ratio, 3)

        addPaintStep(cannyMat, "HSVImage")
        Core.addWeighted(cannyMat, 0.5, cannyGreyMat, 0.5, 0.0, cannyMat)
        Imgproc.dilate(cannyMat, cannyMat, mask, Point(0.0, 0.0), 5)

        addPaintStep(mask, "Mask1")

        addPaintStep(cannyMat, "cannyEdgeImage")

        val p1 = getCorrectPoint(p, mRgbMat)

        val seedPoint = Point(p1.x, p1.y)

        Imgproc.resize(cannyMat, cannyMat, Size(cannyMat.width() + 2.0, cannyMat.height() + 2.0))
        Imgproc.medianBlur(mRgbMat, mRgbMat, 1)

        addPaintStep(mRgbMat, "mRgbMat")
        addPaintStep(cannyMat, "cannyMat")

        val floodFillFlag = Imgproc.CV_WARP_FILL_OUTLIERS
//        val floodFillFlag = 8

//        val wallMask = Mat(mRgbMat.size(),mRgbMat.type())
//
//        val cannyMat1 = Mat()
//        cannyMat.copyTo(cannyMat1)
//
//        Imgproc.floodFill(
//            wallMask,
//            cannyMat1,
//            seedPoint,
//            Scalar(Color.red(chosenColor).toDouble(),
//                Color.green(chosenColor).toDouble(),
//                Color.blue(chosenColor).toDouble()),
//            Rect(),
//            Scalar(5.0, 5.0, 5.0),
//            Scalar(5.0, 5.0, 5.0),
//            floodFillFlag
//        )

        Imgproc.floodFill(
            mRgbMat,
            cannyMat,
            seedPoint,
            Scalar(
                Color.red(chosenColor).toDouble(),
                Color.green(chosenColor).toDouble(),
                Color.blue(chosenColor).toDouble(),
                255.0
            ),
            Rect(50, 50, 500, 500),
            Scalar(5.0, 5.0, 5.0,  5.0),
            Scalar(5.0, 5.0, 5.0, 5.0),
            floodFillFlag
        )
        addPaintStep(mRgbMat, "floodFillImage2")
        addPaintStep(cannyMat, "cannyMat2")
        Imgproc.dilate(mRgbMat, mRgbMat, mask, Point(0.0, 0.0), 5)
        addPaintStep(mask, "Mask1")

        addPaintStep(mRgbMat, "mRgbMat")

        //got the hsv of the mask image
//        val rgbHsvImage = mRgbMat.clone()
        val rgbHsvImage = Mat()
        Imgproc.cvtColor(mRgbMat, rgbHsvImage, Imgproc.COLOR_RGB2HSV)

        addPaintStep(rgbHsvImage, "rgbHsvImage")

        val list1 = ArrayList<Mat>(3)
        Core.split(rgbHsvImage, list1)

        addPaintStep(list1[0], "list10")
        addPaintStep(list1[1], "list11")
        addPaintStep(list1[2], "list12")

        //merged the “v” of original image with mRgb mat
        val result = mRgbMat
//        val result = Mat()
//        Core.merge(listOf(list1[0], list1[1], list[2]), result)
//
//        addPaintStep(list1[0], "list10")
//        addPaintStep(list1[1], "list11")
//        addPaintStep(list1[2], "list12")

        // converted to rgb
//        Imgproc.cvtColor(result, result, Imgproc.COLOR_HSV2RGB)

//        addPaintStep(result, "Result")
//        Imgproc.threshold(result, result, 0.0, 255.0, Imgproc.THRESH_OTSU)
        addPaintStep(result, "Result")
        populatePaintSteps()
        updateBitmap(result)

        addShadeInIdea()

        getCurrentPixelColor(p)
//        getPixelColor(p1, result)

        return result
    }

    fun staticPaintHSV(bitmap: Bitmap, p: Point): Mat {
        val cannyMinThres = 30.0
        val ratio = 2.5
        paintSteps = ArrayList()

        // show intermediate step results
        // grid created here to do that
        showResultLayouts()

        var mRgbMat = Mat()
        Utils.bitmapToMat(bitmap, mRgbMat)

//        if(drawMaskingTape) {
//            try {
////            drawMaskingTape = false
//                mRgbMat = drawMaskings(mRgbMat)
//            } catch (e: Exception) {}
//        }

        addPaintStep(mRgbMat.clone(), "Input Image")
        Imgproc.cvtColor(mRgbMat, mRgbMat, Imgproc.COLOR_RGBA2RGB)

        addPaintStep(mRgbMat.clone(), "Image")
        val mask = Mat(
            Size(mRgbMat.width() + 2.0, mRgbMat.height() + 2.0), CvType.CV_8UC1,
            Scalar(0.0)
        )

        addPaintStep(mask.clone(), "Mask")
        // Imgproc.dilate(mRgbMat, mRgbMat,mask, Point(0.0,0.0), 5)
        val img = Mat()
        mRgbMat.copyTo(img)
        // grayscale
        var mGreyScaleMat = Mat()
        Imgproc.cvtColor(mRgbMat, mGreyScaleMat, Imgproc.COLOR_RGB2GRAY, 3)

        addPaintStep(mGreyScaleMat.clone(), "GreyS")

        if(drawMaskingTape) {
            try {
//            drawMaskingTape = false
                mGreyScaleMat = drawMaskings(mGreyScaleMat)
            } catch (e: Exception) {}
        }

        addPaintStep(mGreyScaleMat.clone(), "GreyScaleMasking")

        Imgproc.medianBlur(mGreyScaleMat, mGreyScaleMat, 3)

        addPaintStep(mGreyScaleMat.clone(), "GreyScaleBlur")

        val cannyGreyMat = Mat()
        Imgproc.Canny(mGreyScaleMat, cannyGreyMat, cannyMinThres, cannyMinThres * ratio, 3)
        addPaintStep(cannyGreyMat, "GreyScaleMatCanny")
        //hsv
        val hsvImage = Mat()
        Imgproc.cvtColor(img, hsvImage, Imgproc.COLOR_RGB2HSV)

        addPaintStep(hsvImage, "HSVImage")

        //got the hsv values
        val list = ArrayList<Mat>(3)
        Core.split(hsvImage, list)
        val sChannelMat = Mat()
        Core.merge(listOf(list[1]), sChannelMat)
        Imgproc.medianBlur(sChannelMat, sChannelMat, 3)

        addPaintStep(list[0], "List0")
        addPaintStep(list[1], "List1")
        addPaintStep(list[2], "List2")

        addPaintStep(sChannelMat, "Flood Fill Image")
        // canny
        val cannyMat = Mat()
        Imgproc.Canny(sChannelMat, cannyMat, cannyMinThres, cannyMinThres * ratio, 3)

        addPaintStep(cannyMat, "HSVImage")
        Core.addWeighted(cannyMat, 0.5, cannyGreyMat, 0.5, 0.0, cannyMat)
        Imgproc.dilate(cannyMat, cannyMat, mask, Point(0.0, 0.0), 5)

        addPaintStep(mask, "Mask1")

        addPaintStep(cannyMat, "cannyEdgeImage")

        val p1 = getCorrectPoint(p, mRgbMat)

        val seedPoint = Point(p1.x, p1.y)

        Imgproc.resize(cannyMat, cannyMat, Size(cannyMat.width() + 2.0, cannyMat.height() + 2.0))
        Imgproc.medianBlur(mRgbMat, mRgbMat, 1)

        addPaintStep(mRgbMat, "mRgbMat")
        addPaintStep(cannyMat, "cannyMat")

        val floodFillFlag = Imgproc.CV_WARP_FILL_OUTLIERS
//        val floodFillFlag = 8

//        val wallMask = Mat(mRgbMat.size(),mRgbMat.type())
//
//        val cannyMat1 = Mat()
//        cannyMat.copyTo(cannyMat1)
//
//        Imgproc.floodFill(
//            wallMask,
//            cannyMat1,
//            seedPoint,
//            Scalar(Color.red(chosenColor).toDouble(),
//                Color.green(chosenColor).toDouble(),
//                Color.blue(chosenColor).toDouble()),
//            Rect(),
//            Scalar(5.0, 5.0, 5.0),
//            Scalar(5.0, 5.0, 5.0),
//            floodFillFlag
//        )

        Imgproc.floodFill(
            mRgbMat,
            cannyMat,
            seedPoint,
            Scalar(
                Color.red(chosenColor).toDouble(),
                Color.green(chosenColor).toDouble(),
                Color.blue(chosenColor).toDouble(),
                255.0
            ),
            Rect(50, 50, 500, 500),
            Scalar(5.0, 5.0, 5.0,  5.0),
            Scalar(5.0, 5.0, 5.0, 5.0),
            floodFillFlag
        )
        addPaintStep(mRgbMat, "floodFillImage2")
        addPaintStep(cannyMat, "cannyMat2")
        Imgproc.dilate(mRgbMat, mRgbMat, mask, Point(0.0, 0.0), 5)
        addPaintStep(mask, "Mask1")

        addPaintStep(mRgbMat, "mRgbMat")

        //got the hsv of the mask image
//        val rgbHsvImage = mRgbMat.clone()
        val rgbHsvImage = Mat()
        Imgproc.cvtColor(mRgbMat, rgbHsvImage, Imgproc.COLOR_RGB2HSV)

        addPaintStep(rgbHsvImage, "rgbHsvImage")

        val list1 = ArrayList<Mat>(3)
        Core.split(rgbHsvImage, list1)

        addPaintStep(list1[0], "list10")
        addPaintStep(list1[1], "list11")
        addPaintStep(list1[2], "list12")

        //merged the “v” of original image with mRgb mat
        val result = mRgbMat
//        val result = Mat()
//        Core.merge(listOf(list1[0], list1[1], list[2]), result)
//
//        addPaintStep(list1[0], "list10")
//        addPaintStep(list1[1], "list11")
//        addPaintStep(list1[2], "list12")

        // converted to rgb
//        Imgproc.cvtColor(result, result, Imgproc.COLOR_HSV2RGB)

//        addPaintStep(result, "Result")
//        Imgproc.threshold(result, result, 0.0, 255.0, Imgproc.THRESH_OTSU)
        addPaintStep(result, "Result")
        populatePaintSteps()
        updateBitmap(result)

        addShadeInIdea()

        getCurrentPixelColor(p)
//        getPixelColor(p1, result)

        return result
    }

    fun rgbPaintHSV(bitmap: Bitmap, p: Point): Mat {
        val cannyMinThres = 30.0
        val ratio = 2.5
        // show intermediate step results
        // grid created here to do that
        showResultLayouts()
        val mRgbMat = Mat()
        Utils.bitmapToMat(bitmap, mRgbMat)

        Imgproc.cvtColor(mRgbMat,mRgbMat,Imgproc.COLOR_RGBA2RGB)
        val mask = Mat(Size(mRgbMat.width()/8.0, mRgbMat.height()/8.0), CvType.CV_8UC1, Scalar(0.0))
        // Imgproc.dilate(mRgbMat, mRgbMat,mask, Point(0.0,0.0), 5)
        val img = Mat()
        mRgbMat.copyTo(img)
        // grayscale
        var mGreyScaleMat = Mat()
        Imgproc.cvtColor(mRgbMat, mGreyScaleMat, Imgproc.COLOR_RGB2GRAY, 3)

        if(drawMaskingTape) {
            try {
//            drawMaskingTape = false
                mGreyScaleMat = drawMaskings(mGreyScaleMat)
            } catch (e: Exception) {}
        }

        Imgproc.medianBlur(mGreyScaleMat,mGreyScaleMat,3)
        val cannyGreyMat = Mat()
        Imgproc.Canny(mGreyScaleMat, cannyGreyMat, cannyMinThres, cannyMinThres*ratio, 3)
        showImage(cannyGreyMat,greyScaleImage)
        //hsv
        val hsvImage = Mat()
        Imgproc.cvtColor(img,hsvImage,Imgproc.COLOR_RGB2HSV)
        //got the hsv values
        val list = ArrayList<Mat>(3)
        Core.split(hsvImage, list)
        val sChannelMat = Mat()
        Core.merge(listOf(list[1]), sChannelMat)
        Imgproc.medianBlur(sChannelMat,sChannelMat,3)
        showImage(sChannelMat,floodFillImage)
        // canny
        val cannyMat = Mat()
        Imgproc.Canny(sChannelMat, cannyMat, cannyMinThres, cannyMinThres*ratio, 3)
        Core.addWeighted(cannyMat,0.5, cannyGreyMat,0.5 ,0.0,cannyMat)
        Imgproc.dilate(cannyMat, cannyMat,mask, Point(0.0,0.0), 5)
//        val displayMetrics = DisplayMetrics()
//        windowManager.defaultDisplay.getMetrics(displayMetrics)
//        val height = displayMetrics.heightPixels
//        val width = displayMetrics.widthPixels
//        val seedPoint = Point(p.x*(mRgbMat.width()/width.toDouble()), p.y*(mRgbMat.height()/height.toDouble()))
        val seedPoint = getCorrectPoint(p, mRgbMat)
// Make sure to resize the cannyMat or it'll throw an error
        Imgproc.resize(cannyMat, cannyMat, Size(cannyMat.width() + 2.0, cannyMat.height() + 2.0))
        Imgproc.medianBlur(mRgbMat,mRgbMat,15)
        val floodFillFlag = 8
        Imgproc.floodFill(
            mRgbMat,
            cannyMat,
            seedPoint,
            Scalar(Color.red(chosenColor).toDouble(),
                Color.green(chosenColor).toDouble(),
                Color.blue(chosenColor).toDouble(),
            255.0),
            Rect(),
            Scalar(5.0, 5.0, 5.0, 5.0),
            Scalar(5.0, 5.0, 5.0, 5.0),
            floodFillFlag
        )
        // showImage(mRgbMat,floodFillImage)
        Imgproc.dilate(mRgbMat, mRgbMat, mask, Point(0.0,0.0), 5)
        //got the hsv of the mask image
        val rgbHsvImage = Mat()
        Imgproc.cvtColor(mRgbMat,rgbHsvImage,Imgproc.COLOR_RGB2HSV)
        val list1 = ArrayList<Mat>(3)
        Core.split(rgbHsvImage, list1)
        //merged the “v” of original image with mRgb mat
        val result = Mat()
        Core.merge(listOf(list1[0], list1[1], list[2]), result)
        // converted to rgb
        Imgproc.cvtColor(result, result, Imgproc.COLOR_HSV2RGB)
        Core.addWeighted(result,0.7, img,0.3 ,0.0,result )
        addShadeInIdea()
        updateBitmap(result)
        return result
    }

    fun rpPaintHSV2(bitmap: Bitmap, p: Point): Mat {
        val cannyMinThres = 30.0
        val ratio = 2.5
        paintSteps = ArrayList()
        // show intermediate step results
        // grid created here to do that
        showResultLayouts()
        var mRgbMat = Mat()
        Utils.bitmapToMat(bitmap, mRgbMat)

        if(drawMaskingTape) {
            try {
//                Imgproc.line(mRgbMat, pt1, pt2, Scalar(0.0, 0.0, 0.0), 1)
//            drawMaskingTape = false
                mRgbMat = drawMaskings(mRgbMat)
            } catch (e: Exception) {}
        }

        addPaintStep(mRgbMat, "Input Image")
        Imgproc.cvtColor(mRgbMat, mRgbMat, Imgproc.COLOR_RGBA2RGB)

        addPaintStep(mRgbMat, "Image")
        val mask = Mat(
            Size(mRgbMat.width() / 8.0, mRgbMat.height() / 8.0), CvType.CV_8UC1,
            Scalar(0.0)
        )

        addPaintStep(mask, "Mask")
        // Imgproc.dilate(mRgbMat, mRgbMat,mask, Point(0.0,0.0), 5)
        val img = Mat()
        mRgbMat.copyTo(img)
        // grayscale
        val mGreyScaleMat = Mat()
        Imgproc.cvtColor(mRgbMat, mGreyScaleMat, Imgproc.COLOR_RGB2GRAY, 3)

        addPaintStep(mGreyScaleMat, "GreyS")

//-------------------------------------------------------------
//        if(drawMaskingTape) {
//            try {
////                Imgproc.line(mRgbMat, pt1, pt2, Scalar(0.0, 0.0, 0.0), 1)
////            drawMaskingTape = false
//                mGreyScaleMat = drawMaskings(mGreyScaleMat)
//            } catch (e: Exception) {}
//        }
//-------------------------------------------------------------


        addPaintStep(mGreyScaleMat, "GreyScaleMasking")

        Imgproc.medianBlur(mGreyScaleMat, mGreyScaleMat, 3)

        addPaintStep(mGreyScaleMat, "GreyScaleBlur")

        val cannyGreyMat = Mat()
        Imgproc.Canny(mGreyScaleMat, cannyGreyMat, cannyMinThres, cannyMinThres * ratio, 3)
        addPaintStep(cannyGreyMat, "GreyScaleMatCanny")
        //hsv
        val hsvImage = Mat()
        Imgproc.cvtColor(img, hsvImage, Imgproc.COLOR_RGB2HSV)

        addPaintStep(hsvImage, "HSVImage")

        //got the hsv values
        val list = ArrayList<Mat>(3)
        Core.split(hsvImage, list)
        val sChannelMat = Mat()
        Core.merge(listOf(list[1]), sChannelMat)
        Imgproc.medianBlur(sChannelMat, sChannelMat, 3)

        addPaintStep(list[0], "List0")
        addPaintStep(list[1], "List1")
        addPaintStep(list[2], "List2")

        addPaintStep(sChannelMat, "Flood Fill Image")
        // canny
        val cannyMat = Mat()
        Imgproc.Canny(sChannelMat, cannyMat, cannyMinThres, cannyMinThres * ratio, 3)

        addPaintStep(cannyMat, "HSVImage")
//        Core.addWeighted(cannyMat, 0.5, cannyGreyMat, 0.5, 0.0, cannyMat)
//        Imgproc.dilate(cannyMat, cannyMat, mask, Point(0.0, 0.0), 5)

        addPaintStep(mask, "Mask1")

        addPaintStep(cannyMat, "cannyEdgeImage")

        val p1 = getCorrectPoint(p, mRgbMat)

        val seedPoint = Point(p1.x, p1.y)

        Imgproc.resize(cannyMat, cannyMat, Size(cannyMat.width() + 2.0, cannyMat.height() + 2.0))
//        Imgproc.medianBlur(mRgbMat, mRgbMat, 15)

        addPaintStep(mRgbMat, "mRgbMat")

        var canny = Mat()

//        val floodFillFlag = Imgproc.WARP_FILL_OUTLIERS
        val floodFillFlag = 4

//        if(drawMaskingTape) {
//            try {
////                Imgproc.line(mRgbMat, pt1, pt2, Scalar(0.0, 0.0, 0.0), 1)
////            drawMaskingTape = false
//                mRgbMat = drawMaskings(mRgbMat)
//            } catch (e: Exception) {}
//        }

//        val canny = cannyMat.clone()
//
//        Imgproc.floodFill(
//                mRgbMat,
//                canny,
//                seedPoint,
//                Scalar(
//                    255.0,
//                    255.0,
//                    255.0,
//                ),
//                Rect(50, 50, 500, 500),
//                Scalar(5.0,  5.0, 5.0),
//                Scalar(5.0, 5.0, 5.0),
//                floodFillFlag
//        )
//
//        addPaintStep(mRgbMat, "floodFillImage1")
//        addPaintStep(cannyMat, "cannyMat1")

        Imgproc.floodFill(
            mRgbMat,
            cannyMat,
            seedPoint,
            Scalar(
                Color.red(chosenColor).toDouble(),
                Color.green(chosenColor).toDouble(),
                Color.blue(chosenColor).toDouble(),
                255.0
            ),
            Rect(50, 50, 500, 500),
            Scalar(5.0, 5.0, 5.0,  5.0),
            Scalar(5.0, 5.0, 5.0, 5.0),
            floodFillFlag
        )
        addPaintStep(mRgbMat, "floodFillImage2")
        addPaintStep(cannyMat, "cannyMat2")
        Imgproc.dilate(mRgbMat, mRgbMat, mask, Point(0.0, 0.0), 5)
        addPaintStep(mask, "Mask1")

        addPaintStep(mRgbMat, "mRgbMat")

        //got the hsv of the mask image
        val rgbHsvImage = Mat()
        Imgproc.cvtColor(mRgbMat, rgbHsvImage, Imgproc.COLOR_RGB2HSV)

        addPaintStep(rgbHsvImage, "rgbHsvImage")

        val list1 = ArrayList<Mat>(3)
        Core.split(rgbHsvImage, list1)

        addPaintStep(list1[0], "list10")
        addPaintStep(list1[1], "list11")
        addPaintStep(list1[2], "list12")

        //merged the “v” of original image with mRgb mat
        val result = Mat()
        Core.merge(listOf(list1[0], list1[1], list[2]), result)

        addPaintStep(list1[0], "list10")
        addPaintStep(list1[1], "list11")
        addPaintStep(list1[2], "list12")

        // converted to rgb
        Imgproc.cvtColor(result, result, Imgproc.COLOR_HSV2RGB)
//        Core.addWeighted(result, 0.7, img, 0.3, 0.0, result)
        addPaintStep(result, "Result")
        populatePaintSteps()
        updateBitmap(result)

        addShadeInIdea()

        getCurrentPixelColor(p)
//        getPixelColor(p1, result)

        return result
    }

    fun rpPaintHSV3(bitmap: Bitmap, p: Point): Mat {
        val cannyMinThres = 30.0
        val ratio = 2.5
        paintSteps = ArrayList()

        // show intermediate step results
        // grid created here to do that
        showResultLayouts()

        var mRgbMat = Mat()
        Utils.bitmapToMat(bitmap, mRgbMat)

        if(drawMaskingTape) {
            try {
//            drawMaskingTape = false
                mRgbMat = drawMaskings(mRgbMat)
            } catch (e: Exception) {}
        }

        addPaintStep(mRgbMat, "Input Image")
//        Imgproc.cvtColor(mRgbMat, mRgbMat, Imgproc.COLOR_RGBA2RGB)

        addPaintStep(mRgbMat, "Image")
        val mask = Mat(
            Size(mRgbMat.width() / 8.0, mRgbMat.height() / 8.0), CvType.CV_8UC1,
            Scalar(0.0)
        )

        addPaintStep(mask, "Mask")
        // Imgproc.dilate(mRgbMat, mRgbMat,mask, Point(0.0,0.0), 5)
        val img = Mat()
        mRgbMat.copyTo(img)
        Imgproc.cvtColor(img, img, Imgproc.COLOR_RGBA2RGB)
        // grayscale
        val mGreyScaleMat = Mat()
        Imgproc.cvtColor(img, mGreyScaleMat, Imgproc.COLOR_RGB2GRAY, 3)

        addPaintStep(mGreyScaleMat, "GreyS")

        addPaintStep(mGreyScaleMat, "GreyScaleMasking")

        Imgproc.medianBlur(mGreyScaleMat, mGreyScaleMat, 3)

        addPaintStep(mGreyScaleMat, "GreyScaleBlur")

        val cannyGreyMat = Mat()
        Imgproc.Canny(mGreyScaleMat, cannyGreyMat, cannyMinThres, cannyMinThres * ratio, 3)
        addPaintStep(cannyGreyMat, "GreyScaleMatCanny")
        //hsv
        val hsvImage = Mat()
        Imgproc.cvtColor(img, hsvImage, Imgproc.COLOR_RGB2HSV)

        addPaintStep(hsvImage, "HSVImage")

        //got the hsv values
        val list = ArrayList<Mat>(3)
        Core.split(hsvImage, list)
        val sChannelMat = Mat()
        Core.merge(listOf(list[1]), sChannelMat)
        Imgproc.medianBlur(sChannelMat, sChannelMat, 3)

        addPaintStep(list[0], "List0")
        addPaintStep(list[1], "List1")
        addPaintStep(list[2], "List2")

        addPaintStep(sChannelMat, "Flood Fill Image")
        // canny
        val cannyMat = Mat()
        Imgproc.Canny(sChannelMat, cannyMat, cannyMinThres, cannyMinThres * ratio, 3)

        addPaintStep(cannyMat, "HSVImage")
        Core.addWeighted(cannyMat, 0.5, cannyGreyMat, 0.5, 0.0, cannyMat)
        Imgproc.dilate(cannyMat, cannyMat, mask, Point(0.0, 0.0), 5)

        addPaintStep(mask, "Mask1")

        addPaintStep(cannyMat, "cannyEdgeImage")

        val p1 = getCorrectPoint(p, mRgbMat)

        val seedPoint = Point(p1.x, p1.y)

        Imgproc.resize(cannyMat, cannyMat, Size(cannyMat.width() + 2.0, cannyMat.height() + 2.0))
        Imgproc.medianBlur(mRgbMat, mRgbMat, 15)

        addPaintStep(mRgbMat, "mRgbMat")

        val floodFillFlag = Imgproc.FLOODFILL_MASK_ONLY

        Imgproc.floodFill(
            mRgbMat,
            cannyMat,
            seedPoint,
            Scalar(
                Color.red(chosenColor).toDouble(),
                Color.green(chosenColor).toDouble(),
                Color.blue(chosenColor).toDouble(),
                255.0
            ),
            Rect(50, 50, 500, 500),
            Scalar(5.0, 5.0, 5.0,  5.0),
            Scalar(5.0, 5.0, 5.0, 5.0),
            floodFillFlag
        )
        addPaintStep(mRgbMat, "floodFillImage2")
        addPaintStep(cannyMat, "cannyMat2")
        Imgproc.dilate(mRgbMat, mRgbMat, mask, Point(0.0, 0.0), 5)
        addPaintStep(mask, "Mask1")

        addPaintStep(mRgbMat, "mRgbMat")

        //got the hsv of the mask image
        val rgbHsvImage = Mat()
        Imgproc.cvtColor(mRgbMat, rgbHsvImage, Imgproc.COLOR_RGB2HSV)

        addPaintStep(rgbHsvImage, "rgbHsvImage")

        val list1 = ArrayList<Mat>(3)
        Core.split(rgbHsvImage, list1)

        addPaintStep(list1[0], "list10")
        addPaintStep(list1[1], "list11")
        addPaintStep(list1[2], "list12")

        //merged the “v” of original image with mRgb mat
        val result = Mat()
        Core.merge(listOf(list1[0], list1[1], list[2]), result)

        addPaintStep(list1[0], "list10")
        addPaintStep(list1[1], "list11")
        addPaintStep(list1[2], "list12")

        // converted to rgb
        Imgproc.cvtColor(result, result, Imgproc.COLOR_HSV2RGB)
        addPaintStep(result, "Result")
        populatePaintSteps()
        updateBitmap(mRgbMat)

        addShadeInIdea()

        getCurrentPixelColor(p)
//        getPixelColor(p1, result)

        return result
    }

    fun rpPaintHSV1(bitmap: Bitmap, p: Point): Mat {
        val cannyMinThres = 30.0
        val ratio = 2.5
        paintSteps = ArrayList()
        // show intermediate step results
        // grid created here to do that
        showResultLayouts()
        var mRgbMat = Mat()
        Utils.bitmapToMat(bitmap, mRgbMat)

        var p1 = getCorrectPoint(p, mRgbMat)

        val trainGray = Mat()
        Imgproc.cvtColor(mRgbMat, trainGray, Imgproc.COLOR_RGBA2GRAY)

        val floodFilled = Mat.zeros(mRgbMat.rows() + 2, mRgbMat.cols() + 2, CvType.CV_8U)

        var sChannelMat = Mat()
//        sChannelMat = mRgbMat
        Imgproc.medianBlur(sChannelMat, sChannelMat, 3)

        val cannyMat = Mat()
        Imgproc.Canny(sChannelMat, cannyMat, cannyMinThres, cannyMinThres * ratio, 3)


        Imgproc.floodFill(
            mRgbMat,
            cannyMat,
            p1,
            Scalar(
                Color.red(chosenColor).toDouble(),
                Color.green(chosenColor).toDouble(),
                Color.blue(chosenColor).toDouble(),
                255.0
            ),
            Rect(50, 50, 500, 500),
            Scalar(5.0, 5.0, 5.0,  5.0),
            Scalar(5.0, 5.0, 5.0, 5.0))

        updateBitmap(mRgbMat)

        return mRgbMat
    }

        private fun getCurrentPixelColor(p: Point){
        var p1 = p.clone()
        p1 = getCorrectPoint(p1, bitmap)
        pixelColor = bitmap.getPixel(p1.x.toInt(), p1.y.toInt())
        var r = Color.red(pixelColor!!)
        var b = Color.blue(pixelColor!!)
        var g = Color.green(pixelColor!!)
    }

    private fun checkPixelColorSame(p: Point): Boolean{
        if(pixelColor != null) {
            var p1 = p.clone()
            p1 = getCorrectPoint(p1, bitmap)

            val cPixel = bitmap.getPixel(p1.x.toInt(), p1.y.toInt())
            return Color.red(cPixel) == Color.red(pixelColor!!) && Color.blue(cPixel) == Color.blue(pixelColor!!) &&
                    Color.green(cPixel) == Color.green(pixelColor!!)
//
//            if (bitmap.getPixel(p1.x.toInt(), p1.y.toInt()) == pixelColor) {
//                return true
//            } else {
//                return false
//            }
        } else {
            return false
        }
    }

    private fun getPixelColor(p: Point, image: Mat){
//        var blue = image.get(p.y, p.x, 0)

        val imgData = ByteArray(((image.total() * image.channels()).toInt()))
        image.get(0, 0, imgData)
        val intensity = imgData[p.y.toInt() * image.cols() + p.x.toInt()]
//        val i = 0
//        val intensity: Scalar = image.at<uchar>(Point(x.toDouble(), y.toDouble()))


        val A = image.clone()
        A.convertTo(A, CvType.CV_64FC3) // New line added.

        val size = (A.total() * A.channels()).toInt()
        val temp = DoubleArray(size) // use double[] instead of byte[]

        A[0, 0, temp]
        temp
    }

    private fun addShadeInIdea(){
        if(myIdea == null)
            myIdea = Idea()

        if(myIdea.shadesUsed == null)
            myIdea.shadesUsed = ArrayList()

        var alreadyAdded = false

        if(selectedShade != null) {

            for (shade in myIdea.shadesUsed!!) {
                if (selectedShade?.shadeCodeHex == shade.shadeCodeHex) {
                    alreadyAdded = true
                    break
                }
            }

            if (!alreadyAdded)
                myIdea.shadesUsed?.add(selectedShade!!)
        }
    }

    private fun clearShadesInIdea(){
        if(myIdea != null && myIdea.shadesUsed != null){
            myIdea.shadesUsed = ArrayList()
        }
        if(intent.hasExtra(Constants.INTENT_MY_IDEA_SHADES))
            myIdea.shadesUsed = intent.getSerializableExtra(Constants.INTENT_MY_IDEA_SHADES) as ArrayList<Shade>
    }

    private fun applyTexture(bitmap: Bitmap, p: Point) {
        val cannyMinThres = 30.0
        val ratio = 2.5
        // show intermediate step results
        // grid created here to do that
        showResultLayouts()
        val mRgbMat = Mat()
        Utils.bitmapToMat(bitmap, mRgbMat)
//        showImage(mRgbMat, inputImage)
        Imgproc.cvtColor(mRgbMat, mRgbMat, Imgproc.COLOR_RGBA2RGB)
        val mask = Mat(
                Size(mRgbMat.width() / 8.0, mRgbMat.height() / 8.0), CvType.CV_8UC1, Scalar(
                0.0
        )
        )
        // Imgproc.dilate(mRgbMat, mRgbMat,mask, Point(0.0,0.0), 5)
        val img = Mat()
        mRgbMat.copyTo(img)
        // grayscale
        val mGreyScaleMat = Mat()
        Imgproc.cvtColor(mRgbMat, mGreyScaleMat, Imgproc.COLOR_RGB2GRAY, 3)
        Imgproc.medianBlur(mGreyScaleMat, mGreyScaleMat, 3)
        val cannyGreyMat = Mat()
        Imgproc.Canny(mGreyScaleMat, cannyGreyMat, cannyMinThres, cannyMinThres * ratio, 3)
//        showImage(cannyGreyMat, greyScaleImage)
        //hsv
        val hsvImage = Mat()
        Imgproc.cvtColor(img, hsvImage, Imgproc.COLOR_RGB2HSV)
        //got the hsv values
        val list = ArrayList<Mat>(3)
        Core.split(hsvImage, list)
        val sChannelMat = Mat()
        Core.merge(listOf(list.get(1)), sChannelMat)
        Imgproc.medianBlur(sChannelMat, sChannelMat, 3)
//        showImage(sChannelMat, floodFillImage)
        // canny
        val cannyMat = Mat()
        Imgproc.Canny(sChannelMat, cannyMat, cannyMinThres, cannyMinThres * ratio, 3)
//        showImage(cannyMat, HSVImage)
        Core.addWeighted(cannyMat, 0.5, cannyGreyMat, 0.5, 0.0, cannyMat)
        Imgproc.dilate(cannyMat, cannyMat, mask, Point(0.0, 0.0), 5)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        val seedPoint = Point(
                p.x * (mRgbMat.width() / width.toDouble()),
                p.y * (mRgbMat.height() / height.toDouble())
        )
        Imgproc.resize(cannyMat, cannyMat, Size(cannyMat.width() + 2.0, cannyMat.height() + 2.0))
        val cannyMat1 = Mat()
        cannyMat.copyTo(cannyMat1)
        val wallMask = Mat(mRgbMat.size(), mRgbMat.type())
        val floodFillFlag = 8
        Imgproc.floodFill(
                wallMask,
                cannyMat,
                seedPoint,
                Scalar(255.0, 255.0, 255.0),
                Rect(),
                Scalar(5.0, 5.0, 5.0),
                Scalar(5.0, 5.0, 5.0),
                floodFillFlag
        )
//        showImage(wallMask, greyScaleImage)
//        showImage(cannyMat, cannyEdgeImage)
        Imgproc.floodFill(
                mRgbMat,
                cannyMat1,
                seedPoint,
                Scalar(0.0, 0.0, 0.0),
                Rect(),
                Scalar(5.0, 5.0, 5.0),
                Scalar(5.0, 5.0, 5.0),
                floodFillFlag
        )
//        showImage(mRgbMat, HSVImage)
        val texture = getTextureImage()
        val textureImgMat = Mat()
        Core.bitwise_and(wallMask, texture, textureImgMat)
//        showImage(textureImgMat, floodFillImage)
        val resultImage = Mat()
        Core.bitwise_or(textureImgMat, mRgbMat, resultImage)
        showImage(resultImage, imageFromData)
        ////alpha blending
        //got the hsv of the mask image
        val rgbHsvImage = Mat()
        Imgproc.cvtColor(resultImage, rgbHsvImage, Imgproc.COLOR_RGB2HSV)
        val list1 = ArrayList<Mat>(3)
        Core.split(rgbHsvImage, list1)
        //merged the “v” of original image with mRgb mat
        val result = Mat()
        Core.merge(listOf(list1[0], list1.get(1), list.get(2)), result)
        // converted to rgb
        Imgproc.cvtColor(result, result, Imgproc.COLOR_HSV2RGB)
        Core.addWeighted(result, 0.8, img, 0.2, 0.0, result)
        showImage(result, imageFromData)
    }

    private enum class LoadImage {
        PICK_FROM_CAMERA,
        PICK_FROM_GALLERY
    }

    private fun setPerformanceLevelIndication(){
        if(performanceLevel == Constants.PERFORMANCE_LOW){
            performanceLow.setBackgroundResource(R.drawable.background_button)
            performanceMedium.setBackgroundResource(R.drawable.background_button_disabled)
            performanceHigh.setBackgroundResource(R.drawable.background_button_disabled)
        }

        if(performanceLevel == Constants.PERFORMANCE_MEDIUM){
            performanceLow.setBackgroundResource(R.drawable.background_button_disabled)
            performanceMedium.setBackgroundResource(R.drawable.background_button)
            performanceHigh.setBackgroundResource(R.drawable.background_button_disabled)
        }

        if(performanceLevel == Constants.PERFORMANCE_HIGH){
            performanceLow.setBackgroundResource(R.drawable.background_button_disabled)
            performanceMedium.setBackgroundResource(R.drawable.background_button_disabled)
            performanceHigh.setBackgroundResource(R.drawable.background_button)
        }
    }

    private fun openMasking(){
        if(maskingTapes != null)
            maskingTapes = ArrayList()

        var maskingPoint = MaskingPoint()

        maskingTapes?.add(maskingPoint)

        mStartPoint = true
        mEndPoint = false

        maskingCanvas.visibility = View.VISIBLE
        maskingTapeView.visibility = View.VISIBLE
        maskingCanvas.invalidate()
        maskingTapeView.invalidate()
        visualizerScreen.invalidate()

        Handler(Looper.getMainLooper()).postDelayed({
            maskingCanvas.visibility = View.VISIBLE
            maskingTapeView.visibility = View.VISIBLE
            maskingCanvas.invalidate()
            maskingTapeView.invalidate()
        }, 500)

//        applyMasking.visibility = View.VISIBLE
//        clearMasking.visibility = View.VISIBLE
        selectedColorCard.visibility = View.INVISIBLE
        actionBar.visibility = View.GONE
        rotate.visibility = View.GONE
        rotateLabel.visibility = View.GONE
        finalPicture.visibility = View.GONE
        finalPictureLabel.visibility = View.GONE


        showToast(resources.getString(R.string.paint_toast_masking_start))
    }

    private fun getMaskingPoints(){
        if(Constants.points != null) {

            var pointCV = MaskingPoint()

            for (point in Constants.points){
                pointCV.startPoint = Point()
                pointCV.startPoint?.x = point.x.toDouble()
                pointCV.startPoint?.y = point.y.toDouble()

                maskingTapes?.add(pointCV)
                pointCV = MaskingPoint()
            }
        }
    }

    private fun closeMasking(clearMasking: Boolean){
        maskingTapes = ArrayList()

        toast?.cancel()

        getMaskingPoints()

        if (clearMasking) {
            maskingTapes = ArrayList()
            Constants.points = ArrayList()
            maskingCanvas.clear()
        }

        if(historyTracking == null || historyTracking?.size == 0){
            resizeImage()
        }

        mStartPoint = true
        mEndPoint = false

        maskingCanvas.visibility = View.INVISIBLE
        maskingTapeView.visibility = View.INVISIBLE
        selectedColorCard.visibility = View.VISIBLE
        rotate.visibility = View.VISIBLE
        rotateLabel.visibility = View.VISIBLE
        finalPicture.visibility = View.VISIBLE
        finalPictureLabel.visibility = View.VISIBLE
        actionBar.visibility = View.VISIBLE

        displayMasking()
    }

    private fun displayMasking(){
//        if(drawMaskingTape) {
//            try {
//                val mat = Mat()
//                Utils.bitmapToMat(bitmap, mat)
//                showImage(drawMaskings(mat), imageFromData)
//            } catch (e: Exception) {}
//        }
    }

//    private fun closeMasking(clearMasking: Boolean){
//        maskingTapes = ArrayList()
//
//        if(Constants.points != null) {
//
//            var startP = true
//
//            var pointCV = MaskingPoint()
//
//            for (point in Constants.points){
//
//                if(startP) {
//                    pointCV.startPoint = Point()
//                    pointCV.startPoint?.x = point.x.toDouble()
//                    pointCV.startPoint?.y = point.y.toDouble()
//                    startP = false
//                } else {
//                    pointCV.endPoint = Point()
//                    pointCV.endPoint?.x = point.x.toDouble()
//                    pointCV.endPoint?.y = point.y.toDouble()
//
//                    maskingTapes?.add(pointCV)
//                    pointCV = MaskingPoint()
//                    startP = true
//                }
//            }
//
//                if (clearMasking) {
//                    maskingTapes = ArrayList()
//                    Constants.points = ArrayList()
//                    maskingCanvas.clear()
//                } else {
//                    if (maskingTapes?.size!! > 0) {
//                        if (maskingTapes?.get(maskingTapes!!.size - 1)?.startPoint != null &&
//                            maskingTapes?.get(maskingTapes!!.size - 1)?.endPoint != null
//                        ) {
//
//                        } else {
//                            maskingTapes?.removeAt(maskingTapes!!.size - 1)
//                        }
//                    }
//                }
//        }
//
//        mStartPoint = true
//        mEndPoint = false
//
//        maskingCanvas.visibility = View.INVISIBLE
//        maskingTapeView.visibility = View.INVISIBLE
//        selectedColorCard.visibility = View.VISIBLE
//        rotate.visibility = View.VISIBLE
//        actionBar.visibility = View.VISIBLE
//
//        if(drawMaskingTape) {
//            try {
////                Imgproc.line(mRgbMat, pt1, pt2, Scalar(0.0, 0.0, 0.0), 1)
////            drawMaskingTape = false
//                val mat = Mat()
//                Utils.bitmapToMat(bitmap, mat)
//                showImage(drawMaskings(mat), imageFromData)
//            } catch (e: Exception) {}
//        }
//    }

    private fun applyMask(){
        drawMaskingTape = true
    }

    private class ApplyColorTask internal constructor(activity: PaintActivity)  : AsyncTask<Point, Int?, String?>() {
        private val activityR = activity

        override fun onPreExecute() {
//            activityR.showLoading()
//            activityR.customLoading?.showLoadingDialogue()
            activityR.imageFromData.isEnabled = true
            activityR.loading.visibility = View.VISIBLE
            super.onPreExecute()
        }

        override fun doInBackground(vararg p: Point): String? {
//            val cannyMinThres = 30.0
//            val ratio = 2.5
//            // show intermediate step results
//            // grid created here to do that
//            activityR.showResultLayouts()
//            val mRgbMat = Mat()
//            Utils.bitmapToMat(activityR.bitmap, mRgbMat)
//            activityR.showImage(mRgbMat, activityR.inputImage)
//            Imgproc.cvtColor(mRgbMat, mRgbMat, Imgproc.COLOR_RGBA2RGB)
//            val mask = Mat(
//                Size(mRgbMat.width() / 8.0, mRgbMat.height() / 8.0), CvType.CV_8UC1, Scalar(
//                    0.0
//                )
//            )
//            // Imgproc.dilate(mRgbMat, mRgbMat,mask, Point(0.0,0.0), 5)
//            val img = Mat()
//            mRgbMat.copyTo(img)
//            // grayscale
//            val mGreyScaleMat = Mat()
//            Imgproc.cvtColor(mRgbMat, mGreyScaleMat, Imgproc.COLOR_RGB2GRAY, 3)
//            Imgproc.medianBlur(mGreyScaleMat, mGreyScaleMat, 3)
//            val cannyGreyMat = Mat()
//            Imgproc.Canny(mGreyScaleMat, cannyGreyMat, cannyMinThres, cannyMinThres * ratio, 3)
//            activityR.showImage(cannyGreyMat, activityR.greyScaleImage)
//            //hsv
//            val hsvImage = Mat()
//            Imgproc.cvtColor(img, hsvImage, Imgproc.COLOR_RGB2HSV)
//            //got the hsv values
//            val list = ArrayList<Mat>(3)
//            Core.split(hsvImage, list)
//            val sChannelMat = Mat()
//            Core.merge(listOf(list.get(1)), sChannelMat)
//            Imgproc.medianBlur(sChannelMat, sChannelMat, 3)
//            activityR.showImage(sChannelMat, activityR.floodFillImage)
//            // canny
//            val cannyMat = Mat()
//            Imgproc.Canny(sChannelMat, cannyMat, cannyMinThres, cannyMinThres * ratio, 3)
//            activityR.showImage(cannyMat, activityR.HSVImage)
//            Core.addWeighted(cannyMat, 0.5, cannyGreyMat, 0.5, 0.0, cannyMat)
//            Imgproc.dilate(cannyMat, cannyMat, mask, Point(0.0, 0.0), 5)
//            activityR.showImage(cannyMat, activityR.cannyEdgeImage)
//            val displayMetrics = DisplayMetrics()
//            activityR.windowManager.defaultDisplay.getMetrics(displayMetrics)
//            val height = displayMetrics.heightPixels
//            val width = displayMetrics.widthPixels
//            val seedPoint = Point(
//                p[0].x * (mRgbMat.width() / width.toDouble()),
//                p[0].y * (mRgbMat.height() / height.toDouble())
//            )
//            Imgproc.resize(cannyMat, cannyMat, Size(cannyMat.width() + 2.0, cannyMat.height() + 2.0))
//            Imgproc.medianBlur(mRgbMat, mRgbMat, 15)
//            val floodFillFlag = 8
//            Imgproc.floodFill(
//                mRgbMat,
//                cannyMat,
//                seedPoint,
//                Scalar(
//                    Color.red(activityR.chosenColor).toDouble(), Color.green(activityR.chosenColor).toDouble(), Color.blue(
//                        activityR.chosenColor
//                    ).toDouble()
//                ),
//                Rect(),
//                Scalar(5.0, 5.0, 5.0),
//                Scalar(5.0, 5.0, 5.0),
//                floodFillFlag
//            )
//            // showImage(mRgbMat,floodFillImage)
//            Imgproc.dilate(mRgbMat, mRgbMat, mask, Point(0.0, 0.0), 5)
//            //got the hsv of the mask image
//            val rgbHsvImage = Mat()
//            Imgproc.cvtColor(mRgbMat, rgbHsvImage, Imgproc.COLOR_RGB2HSV)
//            val list1 = ArrayList<Mat>(3)
//            Core.split(rgbHsvImage, list1)
//            //merged the “v” of original image with mRgb mat
//            val result = Mat()
//            Core.merge(listOf(list1.get(0), list1.get(1), list.get(2)), result)
//            // converted to rgb
//            Imgproc.cvtColor(result, result, Imgproc.COLOR_HSV2RGB)
//            Core.addWeighted(result, 0.7, img, 0.3, 0.0, result)
//            activityR.showImage(result, activityR.imageFromData)

            try {
                if(activityR.selectedShade?.shadeCodeHex!! == "#FFFFFF" || Utills.setLabelColorAccordingToBackground(activityR, activityR.selectedShade?.shadeCodeHex!!) == activityR.getString(R.string.label_color_with_dark_background))
                    activityR.paintStatic(activityR.bitmap, p[0])
                else
                    activityR.paintDynamic(activityR.bitmap, p[0])
//                    activityR.rpPaintHSV(activityR.bitmap, p[0])
//                activityR.rgbPaintHSV(activityR.bitmap, p[0])
//                activityR.paintWall2(activityR.bitmap, p[0])
//                activityR.rpPaintHSV3(activityR.bitmap, p[0])
//                activityR.paintWall(activityR.bitmap, p[0])
//                activityR.rpPaintHSV2(activityR.bitmap, p[0])
//                activityR.rpPaintHSV1(activityR.bitmap, p[0])
//                activityR.convertBitmapToMat(activityR.bitmap, p[0])
//            activityR.rpPaintHSV(activityR.bitmap, p[0])
//                activityR.showImage()
            } catch (e: java.lang.Exception){
//                Utills.showToast(activityR, e.message)
            }
            return ""
        }

        override fun onPostExecute(result: String?) {

//            if(result != null) {
//                activityR.dismissLoading()
//                activityR.customLoading?.dismissLoadingDialogue()
            activityR.showImage()
            activityR.finalPicture.setImageResource(R.drawable.icon_original)
            activityR.finalPictureLabel.text = activityR.resources.getString(R.string.paint_button_original_picture)
                activityR.imageFromData.isEnabled = true
                activityR.loading.visibility = View.GONE
//            }
            super.onPostExecute("")
        }
    }

    private class ApplyTextureTask internal constructor(activity: PaintActivity)  : AsyncTask<Point, Int?, String?>() {
        private val activityR = activity

        override fun onPreExecute() {
//            activityR.showLoading()
            activityR.imageFromData.isEnabled = false
            activityR.loading.visibility = View.VISIBLE
//            activityR.customLoading?.showLoadingDialogue()
            super.onPreExecute()
        }

        override fun doInBackground(vararg p: Point): String? {
            val cannyMinThres = 30.0
            val ratio = 2.5
            // show intermediate step results
            // grid created here to do that
            activityR.showResultLayouts()
            val mRgbMat = Mat()
            Utils.bitmapToMat(activityR.bitmap, mRgbMat)
//            activityR.showImage(mRgbMat, activityR.inputImage)
            Imgproc.cvtColor(mRgbMat, mRgbMat, Imgproc.COLOR_RGBA2RGB)
            val mask = Mat(
                    Size(mRgbMat.width() / 8.0, mRgbMat.height() / 8.0), CvType.CV_8UC1, Scalar(
                    0.0
            )
            )
            // Imgproc.dilate(mRgbMat, mRgbMat,mask, Point(0.0,0.0), 5)
            val img = Mat()
            mRgbMat.copyTo(img)
            // grayscale
            val mGreyScaleMat = Mat()
            Imgproc.cvtColor(mRgbMat, mGreyScaleMat, Imgproc.COLOR_RGB2GRAY, 3)
            Imgproc.medianBlur(mGreyScaleMat, mGreyScaleMat, 3)
            val cannyGreyMat = Mat()
            Imgproc.Canny(mGreyScaleMat, cannyGreyMat, cannyMinThres, cannyMinThres * ratio, 3)
//            activityR.showImage(cannyGreyMat, activityR.greyScaleImage)
            //hsv
            val hsvImage = Mat()
            Imgproc.cvtColor(img, hsvImage, Imgproc.COLOR_RGB2HSV)
            //got the hsv values
            val list = ArrayList<Mat>(3)
            Core.split(hsvImage, list)
            val sChannelMat = Mat()
            Core.merge(listOf(list.get(1)), sChannelMat)
            Imgproc.medianBlur(sChannelMat, sChannelMat, 3)
//            activityR.showImage(sChannelMat, activityR.floodFillImage)
            // canny
            val cannyMat = Mat()
            Imgproc.Canny(sChannelMat, cannyMat, cannyMinThres, cannyMinThres * ratio, 3)
//            activityR.showImage(cannyMat, activityR.HSVImage)
            Core.addWeighted(cannyMat, 0.5, cannyGreyMat, 0.5, 0.0, cannyMat)
            Imgproc.dilate(cannyMat, cannyMat, mask, Point(0.0, 0.0), 5)
            val displayMetrics = DisplayMetrics()
            activityR.windowManager.defaultDisplay.getMetrics(displayMetrics)
            val height = displayMetrics.heightPixels
            val width = displayMetrics.widthPixels

            var p1 = activityR.getCorrectPoint(p[0], mRgbMat)

            val seedPoint = Point(p1.x, p1.y)

            Imgproc.resize(
                    cannyMat,
                    cannyMat,
                    Size(cannyMat.width() + 2.0, cannyMat.height() + 2.0)
            )
            val cannyMat1 = Mat()
            cannyMat.copyTo(cannyMat1)
            val wallMask = Mat(mRgbMat.size(), mRgbMat.type())
            val floodFillFlag = 8
            Imgproc.floodFill(
                    wallMask,
                    cannyMat,
                    seedPoint,
                    Scalar(255.0, 255.0, 255.0),
                    Rect(),
                    Scalar(5.0, 5.0, 5.0),
                    Scalar(5.0, 5.0, 5.0),
                    floodFillFlag
            )
//            activityR.showImage(wallMask, activityR.greyScaleImage)
//            activityR.showImage(cannyMat, activityR.cannyEdgeImage)
            Imgproc.floodFill(
                    mRgbMat,
                    cannyMat1,
                    seedPoint,
                    Scalar(0.0, 0.0, 0.0),
                    Rect(),
                    Scalar(5.0, 5.0, 5.0),
                    Scalar(5.0, 5.0, 5.0),
                    floodFillFlag
            )
//            activityR.showImage(mRgbMat, activityR.HSVImage)
            val texture = activityR.getTextureImage()
            val textureImgMat = Mat()
            Core.bitwise_and(wallMask, texture, textureImgMat)
//            activityR.showImage(textureImgMat, activityR.floodFillImage)
            val resultImage = Mat()
            Core.bitwise_or(textureImgMat, mRgbMat, resultImage)
            activityR.showImage(resultImage, activityR.imageFromData)
            ////alpha blending
            //got the hsv of the mask image
            val rgbHsvImage = Mat()
            Imgproc.cvtColor(resultImage, rgbHsvImage, Imgproc.COLOR_RGB2HSV)
            val list1 = ArrayList<Mat>(3)
            Core.split(rgbHsvImage, list1)
            //merged the “v” of original image with mRgb mat
            val result = Mat()
            Core.merge(listOf(list1[0], list1.get(1), list.get(2)), result)
            // converted to rgb
            Imgproc.cvtColor(result, result, Imgproc.COLOR_HSV2RGB)
            Core.addWeighted(result, 0.8, img, 0.2, 0.0, result)
            activityR.showImage(result, activityR.imageFromData)
            return ""
        }

        override fun onPostExecute(result: String?) {

//            if(result != null) {
//                activityR.dismissLoading()
//                activityR.customLoading?.dismissLoadingDialogue()
                activityR.imageFromData.isEnabled = true
                activityR.loading.visibility = View.GONE
//            }
            super.onPostExecute("")
        }
    }

    private fun createDirectoryAndSaveFile(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val direct =
                File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path + "/Berger")
            if (!direct.exists()) {
                val mainDirectory =
                    File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path + "/Berger")
                mainDirectory.mkdirs()
            }

            val subDirect = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path + "/Berger/MyIdeas"
            )
            if (!subDirect.exists()) {
                val subDirectory =
                    File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path + "/Berger/MyIdeas")
                subDirectory.mkdirs()
            }

            file = File(
                File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path + "/Berger/MyIdeas"),
                Utills.getMyIdeaFileName()
            )
            if (file!!.exists()) {
                file!!.delete()
            }

            if(file != null)
                saveFiles(file!!)
        } else {
            val direct = File(Environment.getExternalStorageDirectory().toString() + "/Berger")
            if (!direct.exists()) {
                val mainDirectory = File("/sdcard/Berger/")
                mainDirectory.mkdirs()
            }

            val subDirect =
                File(Environment.getExternalStorageDirectory().toString() + "/Berger/MyIdeas")
            if (!subDirect.exists()) {
                val subDirectory = File("/sdcard/Berger/MyIdeas")
                subDirectory.mkdirs()
            }

            file = File(File("/sdcard/Berger/MyIdeas"), Utills.getMyIdeaFileName())
            if (file!!.exists()) {
                file!!.delete()
            }

            if(file != null)
                saveFiles(file!!)
        }
    }

    private fun saveFiles(file: File){
        try {
//            val out = FileOutputStream(file)
//            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageToSave)
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
//            out.flush()
//            out.close()

            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.flush()
            out.close()

            myIdea.imageUrl = file?.absolutePath

            var myIdeas = Preferences.getMyIdeasFromSharedPreferences(this)
            if(myIdeas == null)
                myIdeas = ArrayList()

            myIdea.maskingTapes = Constants.points
            myIdeas.add(myIdea)

            Preferences.addMyIdeasToSharedPreferences(this, myIdeas)

            showToast(resources.getString(R.string.paint_toast_saved))

        } catch (e: Exception) {
            e.printStackTrace()
            showToast(resources.getString(R.string.paint_toast_save_failed))
        }
    }

    fun showToast(text: String) {
        if (toast != null) {
            toast?.cancel()
        }
        toast = Toast.makeText(this, text, Toast.LENGTH_LONG)

        val layout = layoutInflater.inflate(
            R.layout.custom_toast_design,
            findViewById<ViewGroup>(R.id.toast_root)
        )

        val toastTextView = layout.findViewById(R.id.toast_text) as TextView
        toastTextView.text = text
        toast?.setGravity(Gravity.CENTER, 0, 0)
        toast?.duration = Toast.LENGTH_LONG
        toast?.view = layout
        toast?.show()
    }

    private fun openCustomPaintMode(){
        if(customPaint){
            customPaint = false
        } else {
            customPaint = true
            Utils.bitmapToMat(bitmap, mat)
        }
    }

    private fun closeCustomPaintMode(){
        customPaint = false
    }

    private fun askCallPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_READ_WRITE_PERMISSION)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            LoadImage.PICK_FROM_CAMERA.ordinal -> if (resultCode == Activity.RESULT_OK) {
                try {
                    imageFromData.setImageURI(Uri.parse(imageFilePath))
                    bitmap = imageFromData.drawable.toBitmap()
                    bitmap = getResizedBitmap(bitmap, bitmap.width / Constants.PERFORMANCE_HIGH, bitmap.height / Constants.PERFORMANCE_HIGH)
                    showImage()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            LoadImage.PICK_FROM_GALLERY.ordinal -> if (resultCode == Activity.RESULT_OK) {
//                loadFromGallery(data?.data)
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        var result = false
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            result = true

        when (requestCode) {

            REQUEST_READ_WRITE_PERMISSION ->
                if (result) {
                    createDirectoryAndSaveFile()
                }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onClick(v: View?) {
        when(v!!.id){

            R.id.performance_low -> {
                performanceLevel = Constants.PERFORMANCE_LOW
                showToast("Quality will be Low but result would be fast")
                setPerformanceLevelIndication()
                resizeImage()
            }

            R.id.performance_medium -> {
                performanceLevel = Constants.PERFORMANCE_MEDIUM
                showToast("Quality will be Better but result will take a bit time")
                setPerformanceLevelIndication()
                resizeImage()
            }

            R.id.performance_high -> {
                performanceLevel = Constants.PERFORMANCE_HIGH
                showToast("Quality will be High but result will take time")
                setPerformanceLevelIndication()
                resizeImage()
            }

            R.id.final_image -> {
                if (showOriginal) {
                    showOriginalImage()
                } else {
                    showImage()
                }
            }

            R.id.original_image ->
                showOriginalImage()

            R.id.color_pick ->
                chooseColor()

            R.id.selected_color_view ->
                chooseColor()

            R.id.selected_color ->
                chooseColor()

            R.id.texture_pick ->
                chooseTexture()

            R.id.open_masking ->
                openMasking()

            R.id.open_masking_label ->
                openMasking()

            R.id.apply_masking ->
                closeMasking(false)

            R.id.apply_masking_label ->
                closeMasking(false)

            R.id.clear_masking ->
                closeMasking(true)

            R.id.clear_masking_label ->
                closeMasking(true)

            R.id.save_image -> {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    askCallPermission()
                } else {
                    createDirectoryAndSaveFile()
                }
            }

            R.id.save_image_label -> {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    askCallPermission()
                } else {
                    createDirectoryAndSaveFile()
                }
            }

            R.id.rotate ->
                rotateImage()
//                showResult()

            R.id.rotate_label ->
                rotateImage()

            R.id.undo ->
                undo()
//                showResult()

            R.id.undo_label ->
                undo()

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
            R.id.clear_paint -> {
//                maskingTapes = ArrayList()
                resizeImage()
                displayMasking()
                clearShadesInIdea()
            }

            R.id.clear_paint_label -> {
//                maskingTapes = ArrayList()
                resizeImage()
                displayMasking()
                clearShadesInIdea()
            }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {

        when(v?.id){
            R.id.final_image -> {
                if(event?.action == MotionEvent.ACTION_DOWN){
                    showOriginalImage()
                } else if (event?.action == MotionEvent.ACTION_UP){
                    showImage()
                }
            }

            R.id.final_image_label -> {
                if(event?.action == MotionEvent.ACTION_DOWN){
                    showOriginalImage()
                } else if (event?.action == MotionEvent.ACTION_UP){
                    showImage()
                }
            }

            R.id.imageFromData -> {
                if (event?.action == MotionEvent.ACTION_DOWN) {
                    if (touchCount == 0) {
                        if (maskingTapeView.visibility == View.GONE || maskingTapeView.visibility == View.INVISIBLE) {
//                            tl.x = (v!!.x - event.rawX).toDouble()
//                            tl.y = (v!!.y - event.rawY).toDouble()

                            tl.x = event.x.toDouble()
                            tl.y = event.y.toDouble()

                            if (!showCamera) {
                                if (texture) {
                                    //                            Thread(Runnable { runOnUiThread { applyTexture(bitmap, tl) } }).start()
                                    ApplyTextureTask(this@PaintActivity).execute(tl)
                                }
//                                else if(customPaint){
//                                    drawCircle(tl)
//                                }
                                else {
                                    //                            Thread(Runnable { runOnUiThread { rpPaintHSV(bitmap, tl) } }).start()
//                                        if(!checkPixelColorSame(tl))
                                            ApplyColorTask(this@PaintActivity).execute(tl)
                                }
                            }
                        } else {

//                            if (maskingTapes == null || maskingTapes!!.size == 0) {
//                                maskingTapes = ArrayList()
//
//                                var maskingPoint = MaskingPoint()
//
//                                maskingTapes?.add(maskingPoint!!)
//                            }
//
//                            if (mStartPoint) {
//                                var maskPoint = Point()
//                                maskPoint.x = event.x.toDouble()
//                                maskPoint.y = event.y.toDouble()
//
//                                maskingTapes?.get(0)?.startPoint = maskPoint
////                                maskingTapes?.get(maskingTapes!!.size - 1)?.startPoint = maskPoint
//
//                                //                                    mStartPoint = false
//                                //                                    mEndPoint = true
//                            }
//
//                            if (mEndPoint) {
//                                var maskPoint = Point()
//                                maskPoint.x = event.x.toDouble()
//                                maskPoint.y = event.y.toDouble()
//
//                                maskingTapes?.get(0)?.endPoint = maskPoint
////                                maskingTapes?.get(maskingTapes!!.size - 1)?.endPoint = maskPoint
//
//                                drawMaskingTape = true
//                                //                                    mStartPoint = true
//                                //                                    mEndPoint = false
//                            }
//
//                            mStartPoint = !mStartPoint
//                            mEndPoint = !mEndPoint
                        }
                    }
                }

//                if (event?.action == MotionEvent.ACTION_MOVE) {
//                    if (touchCount == 0) {
//                        if (maskingTapeView.visibility == View.GONE || maskingTapeView.visibility == View.INVISIBLE) {
////                            tl.x = (v!!.x - event.rawX).toDouble()
////                            tl.y = (v!!.y - event.rawY).toDouble()
//
//                            tl.x = event.x.toDouble()
//                            tl.y = event.y.toDouble()
//
//                            if (!showCamera) {
//                                if (customPaint) {
//                                    drawCircle(tl)
//                                }
//                            }
//                        }
//                    }
//                }
            }

            else -> {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        dX = v!!.x - event.rawX
                        dY = v!!.y - event.rawY;
                    }

                    MotionEvent.ACTION_MOVE -> {
                        var displacementX = event.rawX + dX
                        var displacementY = event.rawY + dY

                        v!!.animate()
                            .x(displacementX)
                            .y(displacementY)
                            .setDuration(0)
                            .start()
                    }
                    else -> { // Note the block
                        return false
                    }
                }
            }
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        if(Constants.SELECTED_SHADE != null){
            selectedShade = Constants.SELECTED_SHADE
            setSelectedColor()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        maskingCanvas.clear()
        Constants.points = ArrayList()
    }

    private fun addToHistory(bitmap: Bitmap){
        if(historyTracking == null)
            historyTracking = ArrayList()

        historyTracking?.add(bitmap)
    }

    private fun undo(){
        if(historyTracking != null && historyTracking?.size!! > 1){
            historyTracking?.removeAt(historyTracking?.size!! - 1)
            bitmap = historyTracking?.get(historyTracking?.size!! - 1)!!
            showImage()
        } else {
            resizeImage()
        }
    }


    private fun addPaintStep(image: Mat, outputName: String){
        if(paintSteps == null)
            paintSteps = ArrayList()

        var paintStep = PaintStep()
        paintStep.image = image.clone()
        paintStep.outputName = outputName

        paintSteps?.add(paintStep)
    }

    private fun populatePaintSteps(){
        if (paintSteps == null || paintSteps?.size == 0) {

        } else {
            paintStepsRecycler.visibility = View.VISIBLE
            paintStepsRecycler.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 2)
            var paintStepsAdapter = PaintResultStepsAdapter(this, paintSteps!!)
            paintStepsRecycler.adapter = paintStepsAdapter
            paintStepsRecycler.invalidate()
        }
    }

    private fun showResult(){
        if(resultLayout.visibility == View.VISIBLE){
            resultLayout.visibility = View.GONE
        } else {
            resultLayout.visibility = View.VISIBLE
        }
    }
}