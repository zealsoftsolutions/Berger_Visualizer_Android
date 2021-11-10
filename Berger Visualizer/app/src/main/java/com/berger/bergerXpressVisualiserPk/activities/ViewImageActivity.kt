package com.berger.bergerXpressVisualiserPk.activities

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.ImageView
import android.widget.ProgressBar
import com.berger.bergerXpressVisualiserPk.R
import com.berger.bergerXpressVisualiserPk.utill.Constants
import com.berger.bergerXpressVisualiserPk.utill.Utills
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.MediaStoreSignature
import java.util.*

import android.graphics.BitmapFactory

class ViewImageActivity : AppCompatActivity() {

    private lateinit var close: ImageView
    private lateinit var zoomableImageView: ImageView
    private lateinit var zoomableImageWebView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var zoomIn: ImageView
    private lateinit var zoomOut: ImageView
    private var imageUrl: String? = null
    private var type = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)

        setViews()
    }

    private fun setViews() {

        Utills.transparentToolbar(this, true)
        Utills.changeNavigationBarColor(this, Constants.COLOR_THEME)

        close = findViewById(R.id.close)
        zoomableImageView = findViewById(R.id.zoomable_image_view)
        zoomableImageWebView = findViewById(R.id.zoomable_image_web_view)

        var matrix = ImageMatrixTouchHandler(applicationContext)

        zoomableImageView.setOnTouchListener(matrix)
        progressBar = findViewById(R.id.progress)
        progressBar.max = 100

        zoomIn = findViewById(R.id.zoom_in)
        zoomIn.setOnClickListener{
            zoomableImageWebView.zoomIn()
        }
        zoomOut = findViewById(R.id.zoom_out)
        zoomOut.setOnClickListener{
            zoomableImageWebView.zoomOut()
        }

        imageUrl = intent.getStringExtra(Constants.INTENT_IMAGE_URL)
        var imageBitmap: String? = null
        if(intent.hasExtra(Constants.INTENT_IMAGE_BITMAP))
            imageBitmap = Constants.IMAGE_BITMAP

        type = intent.getStringExtra(Constants.INTENT_URL_TYPE).toString()
        var imagePlaceHolder = R.drawable.place_holder_image
        var showLoading = false

        if(!imageBitmap.isNullOrEmpty()){
            zoomableImageView.setImageBitmap(Utills.stringToBitmap(imageBitmap))
            loadBitmap(imageBitmap)
        } else {
            when (type) {
                Constants.TYPE_INSPIRATIONAL_IDEAS -> {
                    imageUrl = Utills.getCompleteUrl(imageUrl)
                    displayImage(showLoading, imagePlaceHolder)
                    loadImage()

//                var d = intent.getIntExtra("drawable", -1)

//                if(d != null){
//                    displayImage(d, showLoading, imagePlaceHolder)
//                }
                }
                Constants.TYPE_MY_IDEAS -> {
                    imageUrl = imageUrl
                    displayImage(showLoading, imagePlaceHolder)
                    loadBitmap(fileToBase64String(imageUrl))
                }
            }
        }

        val options = RequestOptions()
            .placeholder(imagePlaceHolder)
            .error(imagePlaceHolder)
            .priority(Priority.HIGH)

        close.setOnClickListener { finish() }
    }

    private fun displayImage(showLoading: Boolean, imagePlaceHolder: Int) {
        if(showLoading)
//            GlideImageLoader(zoomableImageView, progressBar).load(Utills.getCompleteUrl(imageUrl),options)
        else {
            Glide
                .with(this)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(imagePlaceHolder)
                .signature(MediaStoreSignature("", Calendar.getInstance().time.time, 0))
                .error(imagePlaceHolder)
                .priority(Priority.HIGH)
                .into(zoomableImageView)
        }
    }

    private fun displayImage(url: Int, showLoading: Boolean, imagePlaceHolder: Int){
        if(showLoading)
//            GlideImageLoader(zoomableImageView, progressBar).load(Utills.getCompleteUrl(imageUrl),options)
        else {
            Glide
                .with(this)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(imagePlaceHolder)
                .signature(MediaStoreSignature("", Calendar.getInstance().time.time, 0))
                .error(imagePlaceHolder)
                .priority(Priority.HIGH)
                .into(zoomableImageView)
        }
    }

    private fun loadImage(){
        if(imageUrl != null) {

            var html = "<html><head><style type='text/css'>html,body {margin: 0;padding: 0;width: 100%;height: 100%;}html {display: table;}body {display: table-cell;vertical-align: middle;text-align: center;}img{width:100%;}</style></head><body><p><img src='{IMAGE_PLACEHOLDER}' /></p></body></html>";

            html = html.replace("{IMAGE_PLACEHOLDER}", imageUrl!!)
            zoomableImageWebView.loadDataWithBaseURL(
                "file:///android_asset/",
                html,
                "text/html",
                "utf-8",
                ""
            )
            zoomableImageWebView.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
            zoomableImageWebView.setBackgroundColor(Color.parseColor("#000000"))
            zoomableImageWebView.settings.builtInZoomControls = true
            zoomableImageWebView.settings.displayZoomControls = false
            zoomableImageWebView.settings.defaultZoom = WebSettings.ZoomDensity.FAR
        }
    }

    private fun loadBitmap(base64String: String){
        var html = "<html><head><style type='text/css'>html,body {margin: 0;padding: 0;width: 100%;height: 100%;}html {display: table;}body {display: table-cell;vertical-align: middle;text-align: center;}img{width:100%;}</style></head><body><p><img src='{IMAGE_PLACEHOLDER}' /></p></body></html>";

        val image = "data:image/png;base64,$base64String"

        html = html.replace("{IMAGE_PLACEHOLDER}", image)
        zoomableImageWebView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf-8", "")
        zoomableImageWebView.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
        zoomableImageWebView.setBackgroundColor(Color.parseColor("#000000"))
        zoomableImageWebView.settings.builtInZoomControls = true
        zoomableImageWebView.settings.displayZoomControls = false
        zoomableImageWebView.settings.defaultZoom = WebSettings.ZoomDensity.FAR
    }

    private fun fileToBase64String(path: String?): String {
        val bm = BitmapFactory.decodeFile(path)
        return Utills.bitmapToString(bm)
    }

    override fun onDestroy() {
        Constants.IMAGE_BITMAP = null
        super.onDestroy()
    }
}