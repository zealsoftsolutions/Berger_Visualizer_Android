package com.berger.bergerXpressVisualiserPk.activities

import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.berger.bergerXpressVisualiserPk.R
import com.berger.bergerXpressVisualiserPk.utill.Utills
import com.berger.bergerXpressVisualiserPk.adapters.SecondaryColorsListAdapter
import com.berger.bergerXpressVisualiserPk.models.Colors
import com.berger.bergerXpressVisualiserPk.utill.Constants
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.MediaStoreSignature
import java.lang.Exception
import java.util.*

class ColorShadesActivity : AppCompatActivity() {

    private lateinit var title: TextView
    private lateinit var back: ImageView

    private lateinit var colorItem: ConstraintLayout
    private lateinit var colorName: TextView
    private lateinit var colorImage: ImageView
    private lateinit var colorLabel: TextView
    private lateinit var colorDescription: TextView

    private lateinit var shadesRecycler: RecyclerView
    private lateinit var shadesAdapter: SecondaryColorsListAdapter

    private var color: Colors? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_shades)

        setViews()
    }

    private fun setViews() {
        title = findViewById(R.id.title)
        title.text = resources.getText(R.string.title_primary_colors_screen)
        back = findViewById(R.id.back)

        colorItem = findViewById(R.id.color_item)
        colorName = findViewById(R.id.color_label)

        colorImage = findViewById(R.id.color_image)
        colorLabel = findViewById(R.id.color_label)
        colorDescription = findViewById(R.id.color_description)

        shadesRecycler = findViewById(R.id.shades_recycler)

        if(intent.hasExtra(Constants.INTENT_COLOR))
            color = intent.getSerializableExtra(Constants.INTENT_COLOR) as Colors

        if(color != null)
            populateDetails()

        Utills.changeNavigationBarColor(this, Constants.COLOR_THEME)
    }

    private fun populateDetails(){
        if(color?.colorName != null){
            colorName.text = color?.colorName
        }

        colorDescription.text = color?.colorDescription

        if(color?.colorImageUrl != null){
            Glide
                    .with(this)
                    .load(color?.colorImageUrl)
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
                    .placeholder(R.drawable.berger_logo)
                    .signature(MediaStoreSignature("", Calendar.getInstance().time.time, 0))
                    .into(colorImage)
        }

        if(color?.colorCodeHex != null && color?.colorCodeHex!!.isNotEmpty()) {
            try {
                colorItem.setBackgroundColor(Color.parseColor(Utills.getValidHex(color?.colorCodeHex)))
                colorLabel.setBackgroundColor(Color.parseColor(Utills.getValidHex(color?.colorCodeHex)))
            } catch (e: Exception) { }
        }

        if(color?.labelColorHex != null && color?.labelColorHex!!.isNotEmpty()){
            try {
                colorLabel.setTextColor(Color.parseColor(color?.labelColorHex))
            } catch (e: Exception) { }
        }

        populateColors()
    }

    private fun populateColors(){
        if (color?.shades == null || color?.shades?.size == 0) {

        } else {
            shadesRecycler.visibility = View.VISIBLE
            shadesRecycler.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 3)
            shadesAdapter = SecondaryColorsListAdapter(this, color?.shades!!, false, false)
            shadesRecycler.adapter = shadesAdapter
            shadesRecycler.invalidate()
        }
    }
}