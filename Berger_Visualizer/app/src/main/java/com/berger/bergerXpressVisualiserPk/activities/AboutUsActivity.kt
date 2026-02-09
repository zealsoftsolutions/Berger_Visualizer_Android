package com.berger.bergerXpressVisualiserPk.activities

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import com.berger.bergerXpressVisualiserPk.utill.Constants
import com.berger.bergerXpressVisualiserPk.R
import com.berger.bergerXpressVisualiserPk.customWidgets.CustomDialogs
import com.berger.bergerXpressVisualiserPk.models.GeneralResponse
import com.berger.bergerXpressVisualiserPk.restApis.RestApis
import com.berger.bergerXpressVisualiserPk.restApis.RetroClient
import com.berger.bergerXpressVisualiserPk.utill.Internet
import com.berger.bergerXpressVisualiserPk.utill.Utills
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AboutUsActivity : AppCompatActivity() {

    private lateinit var title: TextView
    private lateinit var back: ImageView
    private lateinit var webView: WebView
    private lateinit var aboutText: TextView

    private var customDialog: CustomDialogs? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        setViews()
    }

    private fun setViews(){
        title = findViewById(R.id.title)
        title.text = resources.getString(R.string.title_about_us_screen)
        back = findViewById(R.id.back)
        back.setOnClickListener {
            finish()
        }

        webView = findViewById(R.id.webview)
        webView.setBackgroundColor(Color.TRANSPARENT)
        aboutText = findViewById(R.id.about_text)
        customDialog = CustomDialogs(this)

        val aboutUs = Utills.getAboutUsFromDatabase(this)

        if(aboutUs.isNullOrEmpty())
            getAboutUsCall()
        else
            setText(aboutUs)

        Utills.changeNavigationBarColor(this, Constants.COLOR_THEME)
    }

    private fun getAboutUsCall() {

        if (Internet.isAvailable(this)) {

            customDialog?.showLoadingDialogue()

            val restApis = RetroClient.getClient().create(RestApis::class.java)

            val sendSurfacesListCall = restApis.aboutUsCall
            sendSurfacesListCall.enqueue(object : Callback<GeneralResponse> {
                override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                    customDialog?.dismissLoadingDialogue()
                    if (response.isSuccessful) {
                        if(response.body() != null && response.body()!!.results != null &&
                            !response.body()!!.results?.aboutUs.isNullOrEmpty()){

                            setText(response.body()!!.results?.aboutUs!!)

                            Utills.updateAboutUsDatabase(this@AboutUsActivity, response.body()!!.results?.aboutUs!!)
                        }
                    } else {
                        Utills.showToast(this@AboutUsActivity, resources.getString(R.string.calculator_toast_no_surface_list))
                    }
                }

                override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                    customDialog?.dismissLoadingDialogue()
                    Utills.showToast(this@AboutUsActivity, resources.getText(R.string.connection_problem).toString())
                }
            })

        } else {
            Utills.showToast(this, resources.getText(R.string.no_internet_connection).toString())
        }
    }

    private fun setText(about: String){

        var html = "<html><head></head><body style='text-align:justify;color:rgb(6,90,105);font-size:16px'>{BODY}</body></html>"

        html = html.replace("{BODY}", about)

        webView.loadData(html, "text/html; charset=utf-8", "utf-8")
        aboutText.text = about
    }
}