package com.berger.bergerXpressVisualiserPk.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.berger.bergerXpressVisualiserPk.utill.Constants
import com.berger.bergerXpressVisualiserPk.R
import com.berger.bergerXpressVisualiserPk.adapters.ContactUsListAdapter
import com.berger.bergerXpressVisualiserPk.customWidgets.CustomDialogs
import com.berger.bergerXpressVisualiserPk.models.Contact
import com.berger.bergerXpressVisualiserPk.models.GeneralResponse
import com.berger.bergerXpressVisualiserPk.restApis.RestApis
import com.berger.bergerXpressVisualiserPk.restApis.RetroClient
import com.berger.bergerXpressVisualiserPk.utill.Internet
import com.berger.bergerXpressVisualiserPk.utill.Utills
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContactUsActivity : AppCompatActivity() {

    private lateinit var title: TextView
    private lateinit var back: ImageView
    private lateinit var webView: WebView

    private lateinit var contactUsRecycler: RecyclerView
    private var contactUsListAdapter: ContactUsListAdapter? = null

    private var contactUsList: ArrayList<Contact>? = null

    private var customDialog: CustomDialogs? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)
        setViews()
    }

    private fun setViews(){
        title = findViewById(R.id.title)
        title.text = resources.getString(R.string.title_contact_us_screen)
        back = findViewById(R.id.back)
        back.setOnClickListener {
            finish()
        }

        webView = findViewById(R.id.webview)

        contactUsRecycler = findViewById(R.id.contact_us_recycler)
        customDialog = CustomDialogs(this)

//        setText()
        contactUsList = Utills.getContactUsFromDatabase(this)

        if(contactUsList != null){
            populateContactUsList()
        } else {
            getContactUsCall()
        }

        Utills.changeNavigationBarColor(this, Constants.COLOR_THEME)
    }

    private fun getContactUsCall() {

        if (Internet.isAvailable(this)) {

            customDialog?.showLoadingDialogue()

            val restApis = RetroClient.getClient().create(RestApis::class.java)

            val sendSurfacesListCall = restApis.contactUsCall
            sendSurfacesListCall.enqueue(object : Callback<GeneralResponse> {
                override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                    customDialog?.dismissLoadingDialogue()
                    if (response.isSuccessful) {
                        if(response.body() != null && response.body()!!.results != null &&
                            response.body()!!.results?.contactUsList != null && response.body()!!.results?.contactUsList?.size!! > 0){
                            contactUsList = response.body()!!.results?.contactUsList

                            populateContactUsList()

                            Utills.updateContactUsDatabase(this@ContactUsActivity, contactUsList)
                        }
                    } else {
                        Utills.showToast(this@ContactUsActivity, resources.getString(R.string.contact_us_toast_no_contacts))
                    }
                }

                override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                    customDialog?.dismissLoadingDialogue()
                    Utills.showToast(this@ContactUsActivity, resources.getText(R.string.connection_problem).toString())
                }
            })

        } else {
            Utills.showToast(this, resources.getText(R.string.no_internet_connection).toString())
        }
    }

    private fun populateContactUsList(){
        if (contactUsList == null || contactUsList?.size == 0) {

        } else {
            contactUsRecycler.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            contactUsListAdapter = ContactUsListAdapter(this, contactUsList!!)
            contactUsRecycler.adapter = contactUsListAdapter
            contactUsRecycler.invalidate()
        }
    }

    private fun setText(){
        webView.loadData(getString(R.string.contactUs), "text/html; charset=utf-8", "utf-8")
    }
}