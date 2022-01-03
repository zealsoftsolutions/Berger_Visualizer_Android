package com.berger.bergerXpressVisualiserPk.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.berger.bergerXpressVisualiserPk.R
import com.berger.bergerXpressVisualiserPk.adapters.ColorShadesDisplayPagerAdapter
import com.berger.bergerXpressVisualiserPk.adapters.ProductShadesDisplayPagerAdapter
import com.berger.bergerXpressVisualiserPk.customWidgets.CustomDialogs
import com.berger.bergerXpressVisualiserPk.models.*
import com.berger.bergerXpressVisualiserPk.restApis.RestApis
import com.berger.bergerXpressVisualiserPk.restApis.RetroClient
import com.berger.bergerXpressVisualiserPk.utill.Constants
import com.berger.bergerXpressVisualiserPk.utill.Internet
import com.berger.bergerXpressVisualiserPk.utill.Utills
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ColorShadesDisplayActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var title: TextView
    private lateinit var back: ImageView

    private lateinit var colorShadesViewPager: ViewPager
    private lateinit var colorShadesDisplayPagerAdapter: ColorShadesDisplayPagerAdapter
    private lateinit var productShadesDisplayPagerAdapter: ProductShadesDisplayPagerAdapter

    private var colors: ArrayList<Colors>? = null
    private var products: ArrayList<Product>? = null

    private var performSelect = false
    private var showProductsColors = false
    private var customDialog: CustomDialogs? = null

    private var pId: String? = null
    private var showAll = false
    private var setPageNo = false
    private var mLastClickTime: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_shades_display)

        setViews()
    }

    private fun setViews() {
        title = findViewById(R.id.title)
        title.text = resources.getText(R.string.title_primary_colors_screen)
        back = findViewById(R.id.back)
        back.setOnClickListener(this)

        colorShadesViewPager = findViewById(R.id.color_shades_viewpager)

        customDialog = CustomDialogs(this)

        if(intent.hasExtra("select"))
            performSelect = intent.getBooleanExtra("select", false)

        if(performSelect){
            var toolbar = findViewById<View>(R.id.toolbar)
            toolbar.visibility = View.GONE
        }

        Utills.changeNavigationBarColor(this, Constants.COLOR_THEME)

        if(intent.hasExtra(Constants.INTENT_COLORS)) {
//            colors = intent.getSerializableExtra(Constants.INTENT_COLORS) as ArrayList<Colors>
//            showAll = false
            var product = intent.getSerializableExtra(Constants.INTENT_COLORS) as Product
            products = ArrayList()
            products?.add(product)
            showProductsColors = true
        } else if(intent.hasExtra(Constants.INTENT_PRODUCTS)){
            showProductsColors = true
            colors = Utills.getPalletFromDatabase(this)
            products = Utills.getProductFromDatabase(this)
            showAll = true
            if(intent.hasExtra(Constants.INTENT_PRODUCT_ID))
                pId = intent.getStringExtra(Constants.INTENT_PRODUCT_ID)
        } else if(intent.hasExtra(Constants.INTENT_PRODUCT_ID)) {
            pId = intent.getStringExtra(Constants.INTENT_PRODUCT_ID)
            colors = Utills.getPalletFromDatabase(this)
            products = Utills.getProductFromDatabase(this)
            showAll = true
        } else {
            colors = Utills.getPalletFromDatabase(this)
            products = Utills.getProductFromDatabase(this)
            showAll = true
        }

        if(intent.hasExtra(Constants.INTENT_FROM_VISUALIZER)){
            setPageNo = true
        }

        if(colors != null) {
            if(showAll) {
                if (products != null) {
                    filterProducts()
                } else {
                    getProductsCall()
                }
            } else {
                setColorShadesViewPager()
            }
        } else if(showProductsColors){
            if(products != null){
                filterProducts()
            } else {
                getProductsCall()
            }
        } else {
            getColorPalletCall()
        }
//        dummyColors()
    }

    private fun setColorShadesViewPager() {
        if(colors != null && colors?.size!! > 0) {
            colorShadesDisplayPagerAdapter = ColorShadesDisplayPagerAdapter(supportFragmentManager, colors, performSelect)
            colorShadesViewPager.adapter = colorShadesDisplayPagerAdapter
            colorShadesViewPager.currentItem = 0
//            colorShadesViewPager.clipToPadding = false
//            colorShadesViewPager.setPadding(25, 0, 25, 0)
            colorShadesViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    //int pos = tabLayout.getSelectedTabPosition();
                }

                override fun onPageSelected(position: Int) {
                    colorShadesDisplayPagerAdapter.getItem(position).isVisible
                }

                override fun onPageScrollStateChanged(state: Int) {}
            })
        }
    }

    private fun setProductsShadesViewPager() {
        if(products != null && products?.size!! > 0) {
            productShadesDisplayPagerAdapter = ProductShadesDisplayPagerAdapter(supportFragmentManager, products, colors, performSelect)
            colorShadesViewPager.adapter = productShadesDisplayPagerAdapter
            colorShadesViewPager.currentItem = getPositionOfProduct()
//            colorShadesViewPager.clipToPadding = false
//            colorShadesViewPager.setPadding(25, 0, 25, 0)
            colorShadesViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    Constants.SELECTED_POSITION = position
                    Constants.SELECTED_PRODUCT_ID = null
                }

                override fun onPageSelected(position: Int) {

                }

                override fun onPageScrollStateChanged(state: Int) {}
            })
        }
    }

    private fun getColorPalletCall() {

        if (Internet.isAvailable(this)) {

            customDialog?.showLoadingDialogue()

            var productId = Params()

            if(!pId.isNullOrEmpty()){
                productId.productId = pId
            }

            val restApis = RetroClient.getClient().create(RestApis::class.java)

            val sendCartCall = restApis.getColorPalletCall(productId)
            sendCartCall.enqueue(object : Callback<GeneralResponse> {
                override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                    customDialog?.dismissLoadingDialogue()
                    if (response.isSuccessful) {
                        if(response.body() != null && response.body()!!.results != null &&
                            response.body()!!.results?.colors != null && response.body()!!.results?.colors!!.size > 0){
                            colors = response.body()!!.results?.colors

//                            for(i in 0..3){
//                                var color = Colors()
//
//                                color.colorName = "Blue"
//                                color.colorCodeHex = "#0000FF"
//                                color.labelColorHex = "#FFFFFF"
////                                var shades = colors?.get(0)?.shades?.slice(i*10..i*25)
//                                color.shades = colors?.get(0)?.shades
//
//                                colors?.add(color)
//                            }

                            if(showAll){
                                if(products != null){
                                    filterProducts()
                                } else {
                                    getProductsCall()
                                }
                            }

                            if(pId.isNullOrEmpty()) {
                                Utills.updatePalletDatabase(this@ColorShadesDisplayActivity, colors)
                            }
                        }
                    } else {
                        Utills.showToast(this@ColorShadesDisplayActivity, resources.getString(R.string.colors_toast_no_color))
                    }
                }

                override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                    customDialog?.dismissLoadingDialogue()
                    Utills.showToast(this@ColorShadesDisplayActivity, resources.getText(R.string.connection_problem).toString())
                }
            })

        } else {
            Utills.showToast(this, resources.getText(R.string.no_internet_connection).toString())
        }

    }

    private fun getProductsCall() {
        if (Internet.isAvailable(this)) {

            customDialog?.showLoadingDialogue()

            val restApis = RetroClient.getClient().create(RestApis::class.java)

            val sendCartCall = restApis.getProductsCall(Params())
            sendCartCall.enqueue(object : Callback<GeneralResponse> {
                override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                    customDialog?.dismissLoadingDialogue()
                    if (response.isSuccessful) {
                        if(response.body() != null && response.body()!!.results != null &&
                            response.body()!!.results?.products != null && response.body()!!.results?.products!!.size > 0){
                            products = response.body()!!.results?.products
                            Utills.updateProductsDatabase(this@ColorShadesDisplayActivity, products)
                            filterProducts()
                        }
                    } else {
                        Utills.showToast(this@ColorShadesDisplayActivity, resources.getString(R.string.products_toast_no_products))
                    }
                }

                override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                    customDialog?.dismissLoadingDialogue()
                    Utills.showToast(this@ColorShadesDisplayActivity, resources.getText(R.string.connection_problem).toString())
                }
            })
        } else {
            Utills.showToast(this, resources.getText(R.string.no_internet_connection).toString())
        }
    }

    private fun filterProducts(){
        for(index in products?.size!!-1 downTo 0){

            if(products?.get(index)?.colors == null || products?.get(index)?.colors?.size!! < 1){
                products?.removeAt(index)
            }
        }
        setProductsShadesViewPager()
    }

    private fun setProductsInColor(){

    }

    private fun getPositionOfProduct(): Int {
        if(setPageNo) {
            if (Constants.SELECTED_POSITION != -1) {
                if (showAll) {
                    var count = 0
                    if (products != null)
                        count += products?.size!!
                    if (colors != null)
                        count += colors?.size!!

                    if (Constants.SELECTED_POSITION < count)
                        return Constants.SELECTED_POSITION
                } else if (Constants.SELECTED_POSITION < products?.size!!)
                    return Constants.SELECTED_POSITION
                Constants.SELECTED_POSITION = -1
            } else if (pId != null) {
                for (i in products?.indices!!) {
                    if (pId == products?.get(i)?.id) {
                        return i
                    }
                }
            }
        }
        return 0
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
        }
    }

    override fun onDestroy() {
        if(!setPageNo){
            Constants.SELECTED_POSITION = -1
        }
        super.onDestroy()
    }
}