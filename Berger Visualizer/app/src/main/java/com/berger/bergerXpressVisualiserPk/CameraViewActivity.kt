package com.berger.bergerXpressVisualiserPk

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import org.opencv.android.CameraBridgeViewBase
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import yuku.ambilwarna.AmbilWarnaDialog
import java.util.ArrayList

class CameraViewActivity : AppCompatActivity(), CameraBridgeViewBase.CvCameraViewListener, View.OnClickListener{

    companion object {
        init {
            System.loadLibrary("opencv_java3")
        }
    }

    lateinit var tl: Point
    private lateinit var cameraView: CameraBridgeViewBase
    private lateinit var selectedColorLabel: TextView
    private lateinit var selectedColor: ImageView
    private lateinit var pickColor: ImageView
    var chosenColor = Color.RED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_camera_view)

        setViews()
    }

    private fun setViews(){

        cameraView = findViewById(R.id.camera_view)
        cameraView.visibility = SurfaceView.VISIBLE
        cameraView.setCvCameraViewListener(this)
        cameraView.enableView()

        selectedColorLabel = findViewById(R.id.selected_color_label)
        selectedColor = findViewById(R.id.selected_color)
        selectedColorLabel.text = "Selected Color:"
        selectedColor.setColorFilter(chosenColor)
        pickColor = findViewById(R.id.color_pick)
        pickColor.setOnClickListener(this)
        tl = Point()

        cameraView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                tl.x = event.x.toDouble()
                tl.y = event.y.toDouble()
                return true
            }
        })
    }

    private fun chooseColor() {
//        texture = false
        val colorPicker = AmbilWarnaDialog(
            this,
            chosenColor,
            object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog) {
                }

                override fun onOk(dialog: AmbilWarnaDialog, color: Int) {
                    chosenColor = color
                    selectedColorLabel.text = "Selected Color:"
                    selectedColor.setColorFilter(chosenColor)
//                bitmap = orignalBitmap
                }
            })
        colorPicker.show()
    }

    fun rpPaintHSVCamera(bitmap: Mat?, p: Point): Mat {
        val cannyMinThres = 30.0
        val ratio = 2.5
        // show intermediate step results
        // grid created here to do that
        var mt = bitmap
        if(mt == null)
            mt = Mat()
//        if(bitmap != null) {
//            Imgproc.resize(bitmap, mt, Size(bitmap.cols() / 5.0, bitmap.rows() / 5.0))
//        }
        val mRgbMat = mt

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
        Imgproc.dilate(cannyMat, cannyMat, mask, Point(0.0, 0.0), 1)
//        showImage(cannyMat, cannyEdgeImage)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        val seedPoint = Point(
            p.x * (mRgbMat.width() / width.toDouble()),
            p.y * (mRgbMat.height() / height.toDouble())
        )
        Imgproc.resize(cannyMat, cannyMat, Size(cannyMat.width() + 2.0, cannyMat.height() + 2.0))
        Imgproc.medianBlur(mRgbMat, mRgbMat, 15)
        val floodFillFlag = 8
        Imgproc.floodFill(
            mRgbMat,
            cannyMat,
            seedPoint,
            Scalar(
                Color.red(chosenColor).toDouble(), Color.green(chosenColor).toDouble(), Color.blue(
                    chosenColor
                ).toDouble()
            ),
            Rect(),
            Scalar(5.0, 5.0, 5.0),
            Scalar(5.0, 5.0, 5.0),
            floodFillFlag
        )
        // showImage(mRgbMat,floodFillImage)
        Imgproc.dilate(mRgbMat, mRgbMat, mask, Point(0.0, 0.0), 1)
        //got the hsv of the mask image
        val rgbHsvImage = Mat()
        Imgproc.cvtColor(mRgbMat, rgbHsvImage, Imgproc.COLOR_RGB2HSV)
        val list1 = ArrayList<Mat>(3)
        Core.split(rgbHsvImage, list1)
        //merged the “v” of original image with mRgb mat
        val result = Mat()
        Core.merge(listOf(list1.get(0), list1.get(1), list.get(2)), result)
        // converted to rgb
        Imgproc.cvtColor(result, result, Imgproc.COLOR_HSV2RGB)
        Core.addWeighted(result, 0.7, img, 0.3, 0.0, result)
//        updateBitmap(result)
        return result
    }

    override fun onCameraViewStarted(width: Int, height: Int) {

    }

    override fun onCameraViewStopped() {

    }

    override fun onCameraFrame(inputFrame: Mat?): Mat {
        return rpPaintHSVCamera(inputFrame, tl)
    }

    override fun onClick(v: View?) {
        when(v!!.id) {

            R.id.color_pick ->
                chooseColor()
        }
    }

    override fun onPause() {
        super.onPause()
        if (cameraView != null) cameraView.disableView()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (cameraView != null) cameraView.disableView()
    }
}