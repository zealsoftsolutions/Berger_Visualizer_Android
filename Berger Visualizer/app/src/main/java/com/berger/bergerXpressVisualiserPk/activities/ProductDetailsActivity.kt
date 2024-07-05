package com.berger.bergerXpressVisualiserPk.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.SystemClock
import android.util.DisplayMetrics
import android.view.Display
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.berger.bergerXpressVisualiserPk.R
import com.berger.bergerXpressVisualiserPk.adapters.*
import com.berger.bergerXpressVisualiserPk.utill.Utills
import com.berger.bergerXpressVisualiserPk.models.Colors
import com.berger.bergerXpressVisualiserPk.models.Product
import com.berger.bergerXpressVisualiserPk.models.Shade
import com.berger.bergerXpressVisualiserPk.utill.Constants
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.MediaStoreSignature
import java.util.*


class ProductDetailsActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var title: TextView
    private lateinit var back: ImageView

    private lateinit var productViewCard: CardView
    private lateinit var productImage: ImageView
    private lateinit var productName: TextView

    private lateinit var colorsRecycler: RecyclerView
    private lateinit var colorsAdapter: ColorFamilyAdapter

    private lateinit var leftColorDivider: View
    private lateinit var rightColorDivider: View
    private lateinit var colorName: TextView

    private lateinit var secondaryColorsRecycler: RecyclerView
    private lateinit var secondaryColorsAdapter: ColorShadeProductListAdapter

//    private lateinit var colorCard: CardView
    private lateinit var colorItem: ConstraintLayout
    private lateinit var colorView: View
    private lateinit var colorLabel: TextView
    private lateinit var openVisualizer: ImageButton

    private lateinit var sizesLabel: TextView
    private lateinit var sizesRecycler: RecyclerView
    private lateinit var sizesAdapter: SizesListAdapter
    private lateinit var sizeListAdapter: SizeListAdapter

    private lateinit var surfacesLabel: TextView
    private lateinit var surfacesRecycler: RecyclerView
    private lateinit var surfacesAdapter: SurfacesListAdapter

    private lateinit var descriptionLabel: TextView
    private lateinit var description: TextView

    private lateinit var usesLabel: TextView
    private lateinit var uses: TextView

    private lateinit var surfacePreparationLabel: TextView
    private lateinit var surfacePreparation: TextView

    private lateinit var applicationLabel: TextView
    private lateinit var application: TextView

    private lateinit var dryingTimeLabel: TextView
    private lateinit var dryingTime: TextView

    private lateinit var coverageLabel: TextView
    private lateinit var coverage: TextView

    private lateinit var safetyPrecautionsLabel: TextView
    private lateinit var safetyPrecautionsRecycler: RecyclerView
    private lateinit var safetyPrecautionsAdapter: SafetyPrecautionsListAdapter

    private lateinit var featuresLabel: TextView
    private lateinit var featuresRecycler: RecyclerView
    private lateinit var featuresAdapter: FeaturesListAdapter

    private var product: Product? = null
    private var selectedColor: Colors? = null
    private var selectedShade: Shade? = null

    private var mLastClickTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        setViews()
    }

    private fun setViews() {
        title = findViewById(R.id.title)
        title.text = resources.getText(R.string.title_product_detail_screen)
        back = findViewById(R.id.back)
        back.setOnClickListener(this)

        productViewCard = findViewById(R.id.product_view_card)
        productViewCard.setOnClickListener(this)
        productImage = findViewById(R.id.product_image)
        productName = findViewById(R.id.product_name)

        colorsRecycler = findViewById(R.id.colors_recycler)

        secondaryColorsRecycler = findViewById(R.id.secondary_recycler)

//        colorCard = findViewById(R.id.color_card)
//        colorCard.setOnClickListener(this)
        colorItem= findViewById(R.id.color_item)
        colorItem.setOnClickListener(this)
        colorView = findViewById(R.id.shade)
        colorLabel = findViewById(R.id.shade_label)
        openVisualizer = findViewById(R.id.open_visualizer)
        openVisualizer.setOnClickListener(this)

        leftColorDivider = findViewById(R.id.left_color_divider)
        rightColorDivider = findViewById(R.id.right_color_divider)
        colorName = findViewById(R.id.color_name)

        sizesLabel = findViewById(R.id.sizes_label)
        sizesRecycler = findViewById(R.id.sizes_recycler)

        surfacesLabel = findViewById(R.id.surfaces_label)
        surfacesRecycler = findViewById(R.id.surfaces_recycler)

        descriptionLabel = findViewById(R.id.description_label)
        description = findViewById(R.id.description)

        usesLabel = findViewById(R.id.uses_label)
        uses = findViewById(R.id.uses)

        surfacePreparationLabel = findViewById(R.id.surface_preparation_label)
        surfacePreparation = findViewById(R.id.surface_preparation)

        applicationLabel = findViewById(R.id.application_label)
        application = findViewById(R.id.application)

        dryingTimeLabel = findViewById(R.id.drying_time_label)
        dryingTime = findViewById(R.id.drying_time)

        coverageLabel = findViewById(R.id.coverage_label)
        coverage = findViewById(R.id.coverage)

        safetyPrecautionsLabel = findViewById(R.id.safety_precautions_label)
        safetyPrecautionsRecycler = findViewById(R.id.safety_precautions_recycler)

        featuresLabel = findViewById(R.id.features_label)
        featuresRecycler = findViewById(R.id.features_recycler)

        if(intent.hasExtra(Constants.INTENT_PRODUCT))
            product = intent.getSerializableExtra(Constants.INTENT_PRODUCT) as Product

        populateDetails()

        Utills.changeNavigationBarColor(this, Constants.COLOR_THEME)
    }

    private fun populateDetails(){

        if(product != null){

            if(!product?.savedImageUrl.isNullOrEmpty()) {
                productImage.setImageBitmap(Utills.stringToBitmap(product?.savedImageUrl))
//                Glide
//                    .with(this)
//                    .load(product?.savedImageUrl)
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .placeholder(R.drawable.place_holder_image)
//                    .signature(MediaStoreSignature("", Calendar.getInstance().time.time, 0))
//                    .into(productImage)
            } else if(!product?.productImage.isNullOrEmpty()){
                Glide
                    .with(this)
                    .load(Utills.getCompleteUrl(product?.productImage))
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }
                    })
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.place_holder_image)
                    .signature(MediaStoreSignature("", Calendar.getInstance().time.time, 0))
                    .into(productImage)
            }

            if(product?.productName != null && product?.productName!!.isNotEmpty()) {
                productName.text = product?.productName
            } else { }

            if(product?.productDescription != null && product?.productDescription!!.isNotEmpty()) {
                description.text = product?.productDescription
                descriptionLabel.visibility = View.VISIBLE
                description.visibility = View.VISIBLE
                findViewById<View>(R.id.description_left_divider).visibility = View.VISIBLE
                findViewById<View>(R.id.description_right_divider).visibility = View.VISIBLE
            } else {
                descriptionLabel.visibility = View.GONE
                description.visibility = View.GONE
                findViewById<View>(R.id.description_left_divider).visibility = View.GONE
                findViewById<View>(R.id.description_right_divider).visibility = View.GONE
            }

            if(product?.uses != null && product?.uses!!.isNotEmpty()) {
                uses.text = product?.uses
                usesLabel.visibility = View.VISIBLE
                uses.visibility = View.VISIBLE
                findViewById<View>(R.id.uses_left_divider).visibility = View.VISIBLE
                findViewById<View>(R.id.uses_right_divider).visibility = View.VISIBLE
            } else {
                usesLabel.visibility = View.GONE
                uses.visibility = View.GONE
                findViewById<View>(R.id.uses_left_divider).visibility = View.GONE
                findViewById<View>(R.id.uses_right_divider).visibility = View.GONE
            }

            if(product?.howToPrepare != null && product?.howToPrepare!!.isNotEmpty()) {
                surfacePreparation.text = product?.howToPrepare
                surfacePreparationLabel.visibility = View.VISIBLE
                surfacePreparation.visibility = View.VISIBLE
                findViewById<View>(R.id.surface_preparation_left_divider).visibility = View.VISIBLE
                findViewById<View>(R.id.surface_preparation_right_divider).visibility = View.VISIBLE
            } else {
                surfacePreparationLabel.visibility = View.GONE
                surfacePreparation.visibility = View.GONE
                findViewById<View>(R.id.surface_preparation_left_divider).visibility = View.GONE
                findViewById<View>(R.id.surface_preparation_right_divider).visibility = View.GONE
            }

            if(product?.howToApply != null && product?.howToApply!!.isNotEmpty()) {
                application.text = product?.howToApply
                applicationLabel.visibility = View.VISIBLE
                application.visibility = View.VISIBLE
                findViewById<View>(R.id.application_left_divider).visibility = View.VISIBLE
                findViewById<View>(R.id.application_right_divider).visibility = View.VISIBLE
            } else {
                applicationLabel.visibility = View.GONE
                application.visibility = View.GONE
                findViewById<View>(R.id.application_left_divider).visibility = View.GONE
                findViewById<View>(R.id.application_right_divider).visibility = View.GONE
            }

            if(product?.dryingTime != null && product?.dryingTime!!.isNotEmpty()) {
                dryingTime.text = product?.dryingTime
                dryingTimeLabel.visibility = View.VISIBLE
                dryingTime.visibility = View.VISIBLE
                findViewById<View>(R.id.drying_time_left_divider).visibility = View.VISIBLE
                findViewById<View>(R.id.drying_time_right_divider).visibility = View.VISIBLE
            } else {
                dryingTimeLabel.visibility = View.GONE
                dryingTime.visibility = View.GONE
                findViewById<View>(R.id.drying_time_left_divider).visibility = View.GONE
                findViewById<View>(R.id.drying_time_right_divider).visibility = View.GONE
            }

            if(product?.coverage != null && product?.coverage!!.isNotEmpty()) {
                coverage.text = product?.coverage
                coverageLabel.visibility = View.VISIBLE
                coverage.visibility = View.VISIBLE
                findViewById<View>(R.id.coverage_left_divider).visibility = View.VISIBLE
                findViewById<View>(R.id.coverage_right_divider).visibility = View.VISIBLE
            } else {
                coverageLabel.visibility = View.GONE
                coverage.visibility = View.GONE
                findViewById<View>(R.id.coverage_left_divider).visibility = View.GONE
                findViewById<View>(R.id.coverage_right_divider).visibility = View.GONE
            }

            if(product?.colors != null && product?.colors?.size!! > 0){
                colorItem.visibility = View.VISIBLE
            } else {
                colorItem.visibility = View.GONE
            }

//            populateColors()
            populateSizes()
//            populateSizesList()
            populateSurfaces()
            populateSafetyPrecautionsList()
            populateFeatures()
            setSelectedShade()
        }
    }

    private fun populateColors(){
        if (product?.colors == null || product?.colors?.size == 0) {

        } else {
            colorsRecycler.visibility = View.VISIBLE
            colorsRecycler.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, product?.colors!!.size)
            colorsAdapter = ColorFamilyAdapter(this, product?.colors!!)
            colorsRecycler.adapter = colorsAdapter
            colorsRecycler.invalidate()
        }
    }

    fun selectColor(color: Colors){
        if(color != null){
            if(selectedColor != null && color.colorCodeHex == selectedColor?.colorCodeHex &&
                colorName.visibility == View.VISIBLE){
                rightColorDivider.visibility = View.GONE
                leftColorDivider.visibility = View.GONE
                colorName.visibility = View.GONE
                secondaryColorsRecycler.visibility = View.GONE
            } else {
                rightColorDivider.visibility = View.VISIBLE
                leftColorDivider.visibility = View.VISIBLE
                colorName.visibility = View.VISIBLE
                colorName.text = color.colorName
                colorName.setTextColor(Color.parseColor(color.colorCodeHex))
                populateSecondaryColors(color.shades)
                selectedColor = color
            }
        } else {
            rightColorDivider.visibility = View.GONE
            leftColorDivider.visibility = View.GONE
            colorName.visibility = View.GONE
        }
    }

    fun populateSecondaryColors(shades: ArrayList<Shade>?){
        if (shades == null || shades.size == 0) {
            secondaryColorsRecycler.visibility = View.GONE
        } else {
//
//            val displayMetrics = DisplayMetrics()
//            windowManager.defaultDisplay.getMetrics(displayMetrics)
//            var width = displayMetrics.widthPixels

            val display: Display = windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = resources.displayMetrics.density
            val dpHeight = outMetrics.heightPixels / density
            val dpWidth = outMetrics.widthPixels / density

            var width = dpWidth/5

            var span = 4

            if(width > 100){
                span = 5
            } else {
                span = 4
            }

            secondaryColorsRecycler.visibility = View.VISIBLE
            secondaryColorsRecycler.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, span)
            secondaryColorsAdapter = ColorShadeProductListAdapter(this, shades)
            secondaryColorsRecycler.adapter = secondaryColorsAdapter
            secondaryColorsRecycler.invalidate()
        }
    }

    private fun populateSizes(){
        if (product?.sizes == null || product?.sizes?.size == 0) {
            sizesLabel.visibility = View.GONE
            sizesRecycler.visibility = View.GONE
            findViewById<View>(R.id.sizes_left_divider).visibility = View.GONE
            findViewById<View>(R.id.sizes_right_divider).visibility = View.GONE
        } else {
            sizesLabel.visibility = View.VISIBLE
            sizesRecycler.visibility = View.VISIBLE
            findViewById<View>(R.id.sizes_left_divider).visibility = View.VISIBLE
            findViewById<View>(R.id.sizes_right_divider).visibility = View.VISIBLE
            sizesRecycler.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 3)
            sizesAdapter = SizesListAdapter(this, product?.sizes!!)
            sizesRecycler.adapter = sizesAdapter
            sizesRecycler.invalidate()
        }
    }

    private fun populateSizesList(){
        if (product?.packSizes == null || product?.packSizes?.size == 0) {
            sizesLabel.visibility = View.GONE
            sizesRecycler.visibility = View.GONE
        } else {
            sizesLabel.visibility = View.VISIBLE
            sizesRecycler.visibility = View.VISIBLE
            sizesRecycler.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 3)
            sizeListAdapter = SizeListAdapter(this, product?.packSizes!!)
            sizesRecycler.adapter = sizeListAdapter
            sizesRecycler.invalidate()
        }
    }

    private fun populateSurfaces(){
        if (product?.surfaces == null || product?.surfaces?.size == 0) {
            surfacesLabel.visibility = View.GONE
            surfacesRecycler.visibility = View.GONE
            findViewById<View>(R.id.surfaces_left_divider).visibility = View.GONE
            findViewById<View>(R.id.surfaces_right_divider).visibility = View.GONE
        } else {
            surfacesLabel.visibility = View.VISIBLE
            surfacesRecycler.visibility = View.VISIBLE
            findViewById<View>(R.id.surfaces_left_divider).visibility = View.VISIBLE
            findViewById<View>(R.id.surfaces_right_divider).visibility = View.VISIBLE
            surfacesRecycler.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 3)
            surfacesAdapter = SurfacesListAdapter(this, product?.surfaces!!)
            surfacesRecycler.adapter = surfacesAdapter
            surfacesRecycler.invalidate()
        }
    }

    private fun populateSafetyPrecautionsList(){
        if (product?.safetyPrecautions == null || product?.safetyPrecautions?.size == 0) {
            safetyPrecautionsLabel.visibility = View.GONE
            safetyPrecautionsRecycler.visibility = View.GONE
            findViewById<View>(R.id.safety_precautions_left_divider).visibility = View.GONE
            findViewById<View>(R.id.safety_precautions_right_divider).visibility = View.GONE
        } else {
            safetyPrecautionsLabel.visibility = View.VISIBLE
            safetyPrecautionsRecycler.visibility = View.VISIBLE
            findViewById<View>(R.id.safety_precautions_left_divider).visibility = View.VISIBLE
            findViewById<View>(R.id.safety_precautions_right_divider).visibility = View.VISIBLE
            safetyPrecautionsRecycler.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            safetyPrecautionsAdapter = SafetyPrecautionsListAdapter(this, product?.safetyPrecautions!!)
            safetyPrecautionsRecycler.adapter = safetyPrecautionsAdapter
            safetyPrecautionsRecycler.invalidate()
        }
    }

    private fun populateFeatures(){
        if (product?.features == null || product?.features?.size == 0) {
            featuresLabel.visibility = View.GONE
            featuresRecycler.visibility = View.GONE
            findViewById<View>(R.id.features_left_divider).visibility = View.GONE
            findViewById<View>(R.id.features_right_divider).visibility = View.GONE
        } else {
            featuresLabel.visibility = View.VISIBLE
            featuresRecycler.visibility = View.VISIBLE
            findViewById<View>(R.id.features_left_divider).visibility = View.VISIBLE
            findViewById<View>(R.id.features_right_divider).visibility = View.VISIBLE
            featuresRecycler.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 2)
            featuresAdapter = FeaturesListAdapter(this, product?.features!!)
            featuresRecycler.adapter = featuresAdapter
            featuresRecycler.invalidate()
        }
    }

    private fun setSelectedShade(){
        if(selectedShade != null){
            colorView.setBackgroundColor(Color.parseColor(selectedShade?.shadeCodeHex))
            colorView.visibility = View.VISIBLE
            openVisualizer.visibility = View.VISIBLE

            var name = ""

            if(selectedShade?.shadeName != null)
                name = selectedShade?.shadeName!!

            if(!selectedShade?.shadeCode.isNullOrEmpty())
                name += selectedShade?.shadeCode

            colorLabel.text = name
            colorLabel.setTextColor(Color.parseColor(Utills.setLabelColorAccordingToBackground(this, selectedShade?.shadeCodeHex)))
            Constants.SELECTED_PRODUCT_ID = product?.id
            Constants.SELECTED_POSITION = -1
        } else {
            colorView.visibility = View.GONE
            openVisualizer.visibility = View.GONE
            colorLabel.text = resources.getString(R.string.product_button_view_colors)
            colorLabel.setTextColor(resources.getColor(R.color.black))
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            else -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                performClick(v)
            }
        }
    }

    private fun performClick(v: View?) {
        when (v?.id) {
            R.id.back -> finish()

            R.id.color_card -> {
                val shades = Intent(this, ColorShadesDisplayActivity::class.java)
                shades.putExtra("select", true)
                if(product?.colors != null && product?.colors?.size!! > 0)
                    shades.putExtra(Constants.INTENT_COLORS, product)
                else
                    shades.putExtra(Constants.INTENT_PRODUCT_ID, product?.id)

                startActivity(shades)
            }

            R.id.color_item -> {
                val shades = Intent(this, ColorShadesDisplayActivity::class.java)
                shades.putExtra("select", true)
                if(product?.colors != null && product?.colors?.size!! > 0)
                    shades.putExtra(Constants.INTENT_COLORS, product)
                else
                    shades.putExtra(Constants.INTENT_PRODUCT_ID, product?.id)
                startActivity(shades)
            }

            R.id.open_visualizer -> {
                Constants.SELECTED_SHADE = selectedShade
                var visualize = Intent(this, SourceSelectActivity::class.java)
//                visualize.putExtra(Constants.INTENT_SHADE, selectedShade)
                startActivity(visualize)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(Constants.SELECTED_SHADE != null){
            selectedShade = Constants.SELECTED_SHADE
            setSelectedShade()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}