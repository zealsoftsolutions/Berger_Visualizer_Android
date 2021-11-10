package com.berger.bergerXpressVisualiserPk.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.berger.bergerXpressVisualiserPk.R
import com.berger.bergerXpressVisualiserPk.adapters.MyIdeasListAdapter
import com.berger.bergerXpressVisualiserPk.customWidgets.CustomDialogs
import com.berger.bergerXpressVisualiserPk.models.Idea
import com.berger.bergerXpressVisualiserPk.utill.Constants
import com.berger.bergerXpressVisualiserPk.utill.Preferences
import com.berger.bergerXpressVisualiserPk.utill.Utills
import java.io.File
import java.lang.Exception

class MyIdeasActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var title: TextView
    private lateinit var back: ImageView

    private lateinit var albumRecycler: RecyclerView
    private lateinit var albumAdapter: MyIdeasListAdapter

    private lateinit var noIdeaFoundView: ConstraintLayout
    private lateinit var createNew: TextView

    private lateinit var createNewIdea: Button


    private var images: ArrayList<Idea>? = null

    private var mLastClickTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_ideas)

        setViews()
    }

    private fun setViews() {
        title = findViewById(R.id.title)
        title.text = resources.getText(R.string.title_my_ideas_screen)
        back = findViewById(R.id.back)
        back.setOnClickListener(this)

        albumRecycler = findViewById(R.id.album_recycler)

        noIdeaFoundView = findViewById(R.id.no_idea_section)
        createNew = findViewById(R.id.create_new)
        createNew.setOnClickListener(this)
        createNewIdea = findViewById(R.id.create_idea)
        createNewIdea.setOnClickListener(this)

        images = Preferences.getMyIdeasFromSharedPreferences(this) as ArrayList<Idea>?

        populateAlbum()

        Utills.changeNavigationBarColor(this, Constants.COLOR_THEME)
    }

    private fun populateAlbum(){
        if (images == null || images?.size == 0) {
            albumRecycler.visibility = View.GONE
            noIdeaFoundView.visibility = View.VISIBLE
            createNewIdea.visibility = View.GONE
        } else {
            noIdeaFoundView.visibility = View.GONE
            createNewIdea.visibility = View.VISIBLE
            albumRecycler.visibility = View.VISIBLE
            var gridLayoutManager = GridLayoutManager(this, 2)
            gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {

                override fun getSpanSize(position: Int): Int {
                    return if (position%3 == 0) {
                        2
                    } else {
                        1
                    }
                }
            }
            albumRecycler.layoutManager = gridLayoutManager
            albumAdapter = MyIdeasListAdapter(this, images!!)
            albumRecycler.adapter = albumAdapter
            albumRecycler.invalidate()
        }
    }

    fun showDeleteDialog(index: Int){
        var customDialogs = CustomDialogs(this)
        customDialogs.showGeneralDialog(index)
    }

    fun deleteImage(index: Int){

        if(images != null && index < images?.size!! && images?.get(index) != null) {
            try {
                val file = File(images?.get(index)?.imageUrl)
                if (file != null) {
                    if (file.exists()) {
                        file.delete()
                    }
                }
            } catch (e: Exception){ }
            images?.removeAt(index)

            Preferences.addMyIdeasToSharedPreferences(this, images)

            populateAlbum()
        }
    }

    fun startPainting(idea: Idea?){
        if(idea != null) {
            var paintIt = Intent(this, PaintActivity::class.java)
            paintIt.putExtra(Constants.INTENT_MY_IDEA, idea.imageUrl)
            paintIt.putExtra(Constants.INTENT_MY_IDEA_SHADES, idea.shadesUsed)
            Constants.points = idea.maskingTapes
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

            R.id.create_new -> {
                startActivity(Intent(this, SourceSelectActivity::class.java))
            }

            R.id.create_idea -> {
                startActivity(Intent(this, SourceSelectActivity::class.java))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        images = Preferences.getMyIdeasFromSharedPreferences(this) as ArrayList<Idea>?
        populateAlbum()
    }
}