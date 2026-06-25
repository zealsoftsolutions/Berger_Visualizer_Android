package com.berger.bergerXpressVisualiserPk.activities

import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.berger.bergerXpressVisualiserPk.utill.Constants
import com.berger.bergerXpressVisualiserPk.R
import com.berger.bergerXpressVisualiserPk.utill.Utills


class DealerLocatorActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var title: TextView
    private lateinit var back: ImageView

    private lateinit var webView: WebView
    private lateinit var refresh: SwipeRefreshLayout
    private val urlDealer = "http://berger.com.pk/dealer-locater/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dealer_locator)

        setViews()
    }

    private fun setViews() {
        title = findViewById(R.id.title)
        title.text = resources.getText(R.string.title_dealer_screen)
        back = findViewById(R.id.back)

        webView = findViewById(R.id.web_view)
        refresh = findViewById(R.id.reload_web)
        refresh.setOnRefreshListener(this)

        Utills.changeNavigationBarColor(this, Constants.COLOR_THEME)

        loadPage()
    }

    private fun loadPage(){
        webView.settings.domStorageEnabled = true
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(urlDealer)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                if (urlDealer != url) {
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
}