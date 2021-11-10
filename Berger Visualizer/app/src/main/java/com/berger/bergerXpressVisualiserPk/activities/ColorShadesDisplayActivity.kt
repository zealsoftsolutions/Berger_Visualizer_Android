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

    private fun dummyColors() {

        colors = ArrayList()

        //------------------------------------------------------------------------------------------
        // Yellow

        var c1 = Colors()
        c1.colorName = "Yellow"
        c1.colorDescription =
            "Yellow is sunshine. It is a warm color, shining with optimism, enlightenment, and happiness and carrying the promise of a positive future. It instills energy and sparks creative thoughts. Its many shades can be used to effectively invoke different moods and create a pleasant atmosphere. While it works perfectly as the primary color, yellow often works best as a companion to other colors; to perk up a cooler, subdued palette, for a more natural, earthy flavor or outright flamboyance and excitement!"
        c1.colorCodeHex = "#FFD400"
        c1.labelColorHex = "#FFFFFF"
//        c1.colorImageUrl = R.drawable.image_color_yellow

        var c1s1 = Shade()
        c1s1.shadeName = "Sassy Yellow : 1-12-2"
        c1s1.shadeCodeHex = "#fceeb3"

        var c1s2 = Shade()
        c1s2.shadeName = "Yellow Duchess : 1-15-3"
        c1s2.shadeCodeHex = "#f6f1ae"

        var c1s3 = Shade()
        c1s3.shadeName = "Sensation : 1-15-4"
        c1s3.shadeCodeHex = "#f1eb9b"

        var c1s4 = Shade()
        c1s4.shadeName = "Banana Peel : 1-11-3"
        c1s4.shadeCodeHex = "#fce18e"

        var c1s5 = Shade()
        c1s5.shadeName = "Fluorite : 1-15-5"
        c1s5.shadeCodeHex = "#ece86f"

        c1.shades = ArrayList()

        c1.shades?.add(c1s1)
        c1.shades?.add(c1s2)
        c1.shades?.add(c1s3)
        c1.shades?.add(c1s4)
        c1.shades?.add(c1s5)

        //------------------------------------------------------------------------------------------
        // Red

        var c2 = Colors()
        c2.colorName = "Red"
        c2.colorDescription =
            "Romance, passion, danger, happiness…Red signifies intensity in all forms. The hottest of the warm colors, red is bold and beautiful. It has more personal associations than any other color. Recognized as a stimulant, red is inherently exciting and the amount of red is directly related to the level of energy perceived. Red draws attention and used as an accent can really make a great impact. It promotes enthusiasm, action and confidence."
        c2.colorCodeHex = "#FF0000"
        c2.labelColorHex = "#FFFFFF"
//        c2.colorImageUrl = R.drawable.image_color_red

        var c2s1 = Shade()
        c2s1.shadeName = "Ceramic Pink : 1-48-5"
        c2s1.shadeCodeHex = "#df799a"

        var c2s2 = Shade()
        c2s2.shadeName = "Pink Dusk : 3-1-4"
        c2s2.shadeCodeHex = "#d79f9d"

        var c2s3 = Shade()
        c2s3.shadeName = "Bare Essence : 1-50-5"
        c2s3.shadeCodeHex = "#f19ca2"

        var c2s4 = Shade()
        c2s4.shadeName = "Delectable : 1-1-7"
        c2s4.shadeCodeHex = "#cf3b40"

        var c2s5 = Shade()
        c2s5.shadeName = "Blaze : 3-1-7"
        c2s5.shadeCodeHex = "#9d2e30"

        c2.shades = ArrayList()

        c2.shades?.add(c2s1)
        c2.shades?.add(c2s2)
        c2.shades?.add(c2s3)
        c2.shades?.add(c2s4)
        c2.shades?.add(c2s5)


        //------------------------------------------------------------------------------------------
        // Blue

        var c3 = Colors()
        c3.colorName = "Blue"
        c3.colorDescription =
            "A universal favorite regardless of age or gender, blue is seen as trustworthy, dependable, and committed. The color of ocean and sky, blue is perceived as a constant in our lives. As the collective color of the spirit, it invokes rest and calm. However, it can also be very bold and dramatic depending on the shade. The coolest of colors and the most versatile, blue is an ideal color for almost any location or atmosphere."
        c3.colorCodeHex = "#0000FF"
        c3.labelColorHex = "#FFFFFF"
//        c3.colorImageUrl = R.drawable.image_color_blue

        var c3s1 = Shade()
        c3s1.shadeName = "Azure : 1-36-5"
        c3s1.shadeCodeHex = "#5c84c1"

        var c3s2 = Shade()
        c3s2.shadeName = "American Blue : 1-36-6"
        c3s2.shadeCodeHex = "#4a6db5"

        var c3s3 = Shade()
        c3s3.shadeName = "Stargazer : 1-33-7"
        c3s3.shadeCodeHex = "#007bb0"

        var c3s4 = Shade()
        c3s4.shadeName = "Sky High : 1-33-6"
        c3s4.shadeCodeHex = "#0090c8"

        var c3s5 = Shade()
        c3s5.shadeName = "Blue Madonna : 1-34-5"
        c3s5.shadeCodeHex = "#3e96cc"

        c3.shades = ArrayList()

        c3.shades?.add(c3s1)
        c3.shades?.add(c3s2)
        c3.shades?.add(c3s3)
        c3.shades?.add(c3s4)
        c3.shades?.add(c3s5)


        //------------------------------------------------------------------------------------------
        // Orange

        var c4 = Colors()
        c4.colorName = "Orange"
        c4.colorDescription =
            "The color orange radiates warmth and happiness, combining the physical energy and stimulation of red with the cheerfulness of yellow. It is an optimistic and uplifting color, rejuvenating our spirit. It invokes spontaneity and a positive outlook in life and is a great color to have around."
        c4.colorCodeHex = "#FF6600"
        c4.labelColorHex = "#FFFFFF"
//        c4.colorImageUrl = R.drawable.image_color_orange

        var c4s1 = Shade()
        c4s1.shadeName = "Corallina : 1-7-4"
        c4s1.shadeCodeHex = "#ffc092"

        var c4s2 = Shade()
        c4s2.shadeName = "Mandarin : 1-8-5"
        c4s2.shadeCodeHex = "#feb86b"

        var c4s3 = Shade()
        c4s3.shadeName = "Zinnia Scent : 1-7-5"
        c4s3.shadeCodeHex = "#ffad66"

        var c4s4 = Shade()
        c4s4.shadeName = "Orange Peel : 1-8-6"
        c4s4.shadeCodeHex = "#fea534"

        var c4s5 = Shade()
        c4s5.shadeName = "Bird of Paradise : 1-7-6"
        c4s5.shadeCodeHex = "#f9902b"

        c4.shades = ArrayList()

        c4.shades?.add(c4s1)
        c4.shades?.add(c4s2)
        c4.shades?.add(c4s3)
        c4.shades?.add(c4s4)
        c4.shades?.add(c4s5)


        //------------------------------------------------------------------------------------------
        // Green

        var c5 = Colors()
        c5.colorName = "Green"
        c5.colorDescription =
            "The color most present in nature, green is refreshing and tranquil with a natural balance of warm and cool undertones. It has a calming effect due to its abundance in the natural world and relives stress. The second most popular color after blue, green brightens up every environment for a pleasant and serene effect."
        c5.colorCodeHex = "#00FF00"
        c5.labelColorHex = "#FFFFFF"
//        c5.colorImageUrl = R.drawable.image_color_green

        var c5s1 = Shade()
        c5s1.shadeName = "Green Acre : 2-25-3"
        c5s1.shadeCodeHex = "#acdbbe"

        var c5s2 = Shade()
        c5s2.shadeName = "Placid Green : 2-23-4"
        c5s2.shadeCodeHex = "#97cc93"

        var c5s3 = Shade()
        c5s3.shadeName = "Grazing Field : 2-24-1"
        c5s3.shadeCodeHex = "#80c897"

        var c5s4 = Shade()
        c5s4.shadeName = "Mint Extract : 2-25-5"
        c5s4.shadeCodeHex = "#3da581"

        var c5s5 = Shade()
        c5s5.shadeName = "Ocean Gardens : 1-26-6"
        c5s5.shadeCodeHex = "#00aa8e"

        c5.shades = ArrayList()

        c5.shades?.add(c5s1)
        c5.shades?.add(c5s2)
        c5.shades?.add(c5s3)
        c5.shades?.add(c5s4)
        c5.shades?.add(c5s5)


        //------------------------------------------------------------------------------------------
        // Violet

        var c6 = Colors()
        c6.colorName = "Violet"
        c6.colorDescription =
            "Violet is the highest color in the visible spectrum and is one of the cool colors. Vibrant and vivid, violet promotes imagination and uniqueness. It has an inherent mystery and is often associated with fantasy and the future. A great choice for an artistic and relaxed atmosphere, violet also lends a distinctive quality of luxury and nobility."
        c6.colorCodeHex = "#C800FF"
        c6.labelColorHex = "#FFFFFF"
//        c6.colorImageUrl = R.drawable.image_color_violet

        var c6s1 = Shade()
        c6s1.shadeName = "Velvet Touch : 1-42-4"
        c6s1.shadeCodeHex = "#ba99cc"

        var c6s2 = Shade()
        c6s2.shadeName = "Mystic Orchid : 1-40-4"
        c6s2.shadeCodeHex = "#a99bd0"

        var c6s3 = Shade()
        c6s3.shadeName = "Pink Velvet : 2-44-5"
        c6s3.shadeCodeHex = "#b46f9f"

        var c6s4 = Shade()
        c6s4.shadeName = "Floral Jardin : 1-43-6"
        c6s4.shadeCodeHex = "#ac6aaa"

        var c6s5 = Shade()
        c6s5.shadeName = "Pink Parasol : 1-43-7"
        c6s5.shadeCodeHex = "#9c5799"

        c6.shades = ArrayList()

        c6.shades?.add(c6s1)
        c6.shades?.add(c6s2)
        c6.shades?.add(c6s3)
        c6.shades?.add(c6s4)
        c6.shades?.add(c6s5)



        //------------------------------------------------------------------------------------------
        // Brown

        var c7 = Colors()
        c7.colorName = "Brown"
        c7.colorDescription =
            "Brown is one of the most versatile colors due to its neutrality. The color of earth and wood, it has an instant effect of wholesomeness and stability. Brown promotes comfort and simplicity. Intelligently used brown can give a very welcoming impression of openness and honesty, as well as sophistication and elegance. That is why it is usually a good choice to use brown in drawing/dining rooms."
        c7.colorCodeHex = "#A52A2A"
        c7.labelColorHex = "#FFFFFF"
//        c7.colorImageUrl = R.drawable.image_color_brown

        var c7s1 = Shade()
        c7s1.shadeName = "Honeysweet : 3-13-5"
        c7s1.shadeCodeHex = "#d6c376"

        var c7s2 = Shade()
        c7s2.shadeName = "Daintree Road : 3-14-5"
        c7s2.shadeCodeHex = "#d5c778"

        var c7s3 = Shade()
        c7s3.shadeName = "Honey Dew : 2-15-5"
        c7s3.shadeCodeHex = "#ded273"

        var c7s4 = Shade()
        c7s4.shadeName = "Filigree Gold : 3-13-6"
        c7s4.shadeCodeHex = "#c7ab4b"

        var c7s5 = Shade()
        c7s5.shadeName = "Mountain Lion : 3-12-5"
        c7s5.shadeCodeHex = "#cdb364"

        c7.shades = ArrayList()

        c7.shades?.add(c7s1)
        c7.shades?.add(c7s2)
        c7.shades?.add(c7s3)
        c7.shades?.add(c7s4)
        c7.shades?.add(c7s5)


        //------------------------------------------------------------------------------------------
        // White

        var c8 = Colors()
        c8.colorName = "White"
        c8.colorDescription =
            "The color of light, white projects purity and clarity. It discourages clutter and brings order and freshness. It stands for wholeness and completion and also represents openness and truth. Smart usage of white in the living space promotes a positive, glowing outlook on life. It contains an equal balance of all the colors in the spectrum and therefore, is an excellent complement to any color."
        c8.colorCodeHex = "#FFFFFF"
        c8.labelColorHex = "#000000"
//        c8.colorImageUrl = R.drawable.image_color_white

        var c8s1 = Shade()
        c8s1.shadeName = "Peach Shadow : 3-6-1"
        c8s1.shadeCodeHex = "#f8e6d5"

        var c8s2 = Shade()
        c8s2.shadeName = "Alpine Pink : 1-47-1"
        c8s2.shadeCodeHex = "#eee4e4"

        var c8s3 = Shade()
        c8s3.shadeName = "Harvest Moon : 3-13-1"
        c8s3.shadeCodeHex = "#f2eed6"

        var c8s4 = Shade()
        c8s4.shadeName = "Spindrift : 1-24-1"
        c8s4.shadeCodeHex = "#e1e9db"

        var c8s5 = Shade()
        c8s5.shadeName = "Impressionist Sky:2-29-1"
        c8s5.shadeCodeHex = "#dcedeb"

        c8.shades = ArrayList()

        c8.shades?.add(c8s1)
        c8.shades?.add(c8s2)
        c8.shades?.add(c8s3)
        c8.shades?.add(c8s4)
        c8.shades?.add(c8s5)

        colors?.add(c1)
        colors?.add(c2)
        colors?.add(c3)
        colors?.add(c4)
        colors?.add(c5)
        colors?.add(c6)
        colors?.add(c7)
        colors?.add(c8)

        setColorShadesViewPager()
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