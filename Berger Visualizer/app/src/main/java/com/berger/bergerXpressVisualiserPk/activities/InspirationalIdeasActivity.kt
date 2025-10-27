package com.berger.bergerXpressVisualiserPk.activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.berger.bergerXpressVisualiserPk.R
import com.berger.bergerXpressVisualiserPk.adapters.InspirationalIdeasAdapter
import com.berger.bergerXpressVisualiserPk.customWidgets.CustomDialogs
import com.berger.bergerXpressVisualiserPk.models.GeneralResponse
import com.berger.bergerXpressVisualiserPk.models.Idea
import com.berger.bergerXpressVisualiserPk.restApis.RestApis
import com.berger.bergerXpressVisualiserPk.restApis.RetroClient
import com.berger.bergerXpressVisualiserPk.utill.Constants
import com.berger.bergerXpressVisualiserPk.utill.Internet
import com.berger.bergerXpressVisualiserPk.utill.Utills
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InspirationalIdeasActivity : AppCompatActivity(), View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var title: TextView
    private lateinit var back: ImageView

    private lateinit var albumRecycler: RecyclerView
    private lateinit var inspirationalIdeasAdapter: InspirationalIdeasAdapter

    private lateinit var webView: WebView
    private lateinit var refresh: SwipeRefreshLayout
    private val urlDealer = "https://berger.com.pk/decorative-paints"

    private var images: ArrayList<Int>? = null
    private var inspirationalIdeas: ArrayList<Idea>? = null
    private var customDialog: CustomDialogs? = null

    private var mLastClickTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inspirational_ideas)
        setViews()
    }

    private fun setViews() {
        title = findViewById(R.id.title)
        title.text = resources.getText(R.string.title_inspirational_ideas_screen)
        back = findViewById(R.id.back)
        back.setOnClickListener(this)

        albumRecycler = findViewById(R.id.album_recycler)

        customDialog = CustomDialogs(this)

//        inspirationalIdeas = Utills.getInspirationalIdeasFromDatabase(this)
//
//        if(inspirationalIdeas != null){
//            populateAlbum()
//        } else {
//            getInspirationalIdeasCall()
//        }
//        dummyInspirationalIdeas()

        webView = findViewById(R.id.web_view)
        refresh = findViewById(R.id.reload_web)
        refresh.setOnRefreshListener(this)

        loadPage()

        Utills.changeNavigationBarColor(this, Constants.COLOR_THEME)
    }

    private fun dummyInspirationalIdeas(){

        images = ArrayList()

//        images?.add(R.drawable.album_1)
//        images?.add(R.drawable.album_2)
//        images?.add(R.drawable.album_3)
//        images?.add(R.drawable.album_4)
//        images?.add(R.drawable.album_5)
//        images?.add(R.drawable.album_6)
//        images?.add(R.drawable.album_7)

        populateAlbum()
    }

    private fun populateAlbum(){
        if (inspirationalIdeas == null || inspirationalIdeas?.size == 0) {

        } else {
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
            inspirationalIdeasAdapter = InspirationalIdeasAdapter(this, inspirationalIdeas!!)
            albumRecycler.adapter = inspirationalIdeasAdapter
            albumRecycler.invalidate()
        }
    }

    private fun getInspirationalIdeasCall() {

        if (Internet.isAvailable(this)) {

            customDialog?.showLoadingDialogue()

            val restApis = RetroClient.getClient().create(RestApis::class.java)

            val sendCartCall = restApis.inspirationalIdeasCall
            sendCartCall.enqueue(object : Callback<GeneralResponse> {
                override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                    customDialog?.dismissLoadingDialogue()
                    if (response.isSuccessful) {
                        if(response.body() != null && response.body()!!.results != null &&
                            response.body()!!.results?.inspirationalIdeas != null && response.body()!!.results?.inspirationalIdeas!!.size > 0){
                            inspirationalIdeas = response.body()!!.results?.inspirationalIdeas

                            populateAlbum()

                            Utills.updateInspirationalIdeasDatabase(this@InspirationalIdeasActivity, inspirationalIdeas)
                        }
                    } else {
                        Utills.showToast(this@InspirationalIdeasActivity, resources.getString(R.string.inspirational_ideas_toast_no_ideas))
                    }
                }

                override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                    customDialog?.dismissLoadingDialogue()
                    Utills.showToast(this@InspirationalIdeasActivity, resources.getText(R.string.connection_problem).toString())
                }
            })

        } else {
            Utills.showToast(this, resources.getText(R.string.no_internet_connection).toString())
        }
    }

    private fun loadPage(){
        webView.settings.domStorageEnabled = true
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(urlDealer)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                if (!url.contains("https://berger.com.pk/")) {
                    view.stopLoading()
                    view.goBack()
                }
            }
        }
    }

    override fun onRefresh() {
        loadPage()
        refresh.isRefreshing = false
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
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}