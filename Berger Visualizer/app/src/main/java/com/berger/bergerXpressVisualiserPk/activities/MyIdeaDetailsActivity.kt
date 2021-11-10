package com.berger.bergerXpressVisualiserPk.activities

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.berger.bergerXpressVisualiserPk.R
import com.berger.bergerXpressVisualiserPk.adapters.SecondaryColorsListAdapter
import com.berger.bergerXpressVisualiserPk.customWidgets.CustomDialogs
import com.berger.bergerXpressVisualiserPk.models.Idea
import com.berger.bergerXpressVisualiserPk.models.Shade
import com.berger.bergerXpressVisualiserPk.utill.Constants
import com.berger.bergerXpressVisualiserPk.utill.Preferences
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.signature.MediaStoreSignature
import java.io.File
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


class MyIdeaDetailsActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var title: TextView
    private lateinit var back: ImageView

    private lateinit var ideaImage: ImageView
    private lateinit var colorsUsedLabel: TextView
    private lateinit var shadesRecycler: RecyclerView
    private lateinit var shadesAdapter: SecondaryColorsListAdapter

    private lateinit var delete: Button
    private lateinit var visualize: Button

    private var myIdea: Idea? = null
    private var index = -1

    private var mLastClickTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_idea_details)

        setViews()
    }

    private fun setViews() {
        title = findViewById(R.id.title)
        title.text = resources.getText(R.string.title_my_idea_details_screen)
        back = findViewById(R.id.back)
        back.setOnClickListener(this)

        ideaImage = findViewById(R.id.idea_image)
        ideaImage.setOnClickListener(this)

        colorsUsedLabel = findViewById(R.id.colors_used_label)
        shadesRecycler = findViewById(R.id.shades_recycler)

        if(intent.hasExtra(Constants.INTENT_MY_IDEA)) {
            myIdea = Idea()
            myIdea?.imageUrl = intent.getStringExtra(Constants.INTENT_MY_IDEA)

            if(intent.hasExtra(Constants.INTENT_MY_IDEA_SHADES))
                myIdea?.shadesUsed = intent.getSerializableExtra(Constants.INTENT_MY_IDEA_SHADES) as ArrayList<Shade>

            myIdea?.maskingTapes = Constants.points
        }

        if(intent.hasExtra(Constants.INTENT_IDEA_INDEX))
            index = intent.getIntExtra(Constants.INTENT_IDEA_INDEX, -1)

        delete = findViewById(R.id.delete)
        delete.setOnClickListener(this)
        visualize = findViewById(R.id.visualize)
        visualize.setOnClickListener(this)

        showDetails()
    }

    private fun showDetails(){
        if(myIdea != null){

            if(myIdea?.imageUrl != null && myIdea?.imageUrl!!.isNotEmpty()) {
                Glide
                    .with(this)
                    .load(myIdea?.imageUrl)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?, model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?, model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable>?,
                            dataSource: DataSource?, isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }
                    })
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.place_holder_image)
                    .signature(MediaStoreSignature("", Calendar.getInstance().time.time, 0))
                    .into(ideaImage)
            }

            populateColors(myIdea?.shadesUsed)
        }
    }


    private fun populateColors(shades: ArrayList<Shade>?){
        if (shades != null && shades.size > 0) {
            colorsUsedLabel.visibility = View.VISIBLE
            shadesRecycler.visibility = View.VISIBLE
            shadesRecycler.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 3)
            shadesAdapter = SecondaryColorsListAdapter(this, shades, false, false)
            shadesRecycler.adapter = shadesAdapter
            shadesRecycler.invalidate()
        } else {
            colorsUsedLabel.visibility = View.GONE
            shadesRecycler.visibility = View.GONE
        }
    }

    fun showDeleteDialog(){
        if(index > -1) {
            var customDialogs = CustomDialogs(this)
            customDialogs.showGeneralDialog(index)
        }
    }

    fun deleteImage(index: Int){
            try {
                val file = File(myIdea?.imageUrl)
                if (file != null) {
                    if (file.exists()) {
                        file.delete()
                    }
                }
            } catch (e: Exception){ }

        var ideas = Preferences.getMyIdeasFromSharedPreferences(this)

        ideas.removeAt(index)

        Preferences.addMyIdeasToSharedPreferences(this, ideas)
        finish()
    }

    fun startPainting(){
        if(myIdea != null && myIdea?.imageUrl != null) {
//            if(myIdea?.maskingTapes != null && myIdea?.maskingTapes?.size!! > 0){
//                var pointsArray = ArrayList<android.graphics.Point>()
//                try {
//                    for (point in myIdea?.maskingTapes!!) {
//                        pointsArray.add(point)
//                    }
//                    Constants.points = pointsArray
//                } catch (e: Exception){}
//            }
            Constants.points = myIdea?.maskingTapes

            var paintIt = Intent(this, PaintActivity::class.java)
            paintIt.putExtra(Constants.INTENT_MY_IDEA, myIdea?.imageUrl)
            paintIt.putExtra(Constants.INTENT_MY_IDEA_SHADES, myIdea?.shadesUsed)
            paintIt.putExtra(Constants.INTENT_RESIZING, false)
            startActivity(paintIt)
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

            R.id.idea_image -> {
                if(myIdea != null) {
                    val viewImage = Intent(this, ViewImageActivity::class.java)
                    viewImage.putExtra("imageUrl", myIdea?.imageUrl)
                    viewImage.putExtra("type", Constants.TYPE_MY_IDEAS)
                    startActivity(viewImage)
                }
            }

            R.id.delete -> showDeleteDialog()

            R.id.visualize -> startPainting()
        }
    }
}