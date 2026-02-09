package com.berger.bergerXpressVisualiserPk.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.berger.bergerXpressVisualiserPk.utill.Constants
import com.berger.bergerXpressVisualiserPk.R
import com.berger.bergerXpressVisualiserPk.utill.Utills
import com.berger.bergerXpressVisualiserPk.adapters.ProductsListAdapter
import com.berger.bergerXpressVisualiserPk.customWidgets.CustomDialogs
import com.berger.bergerXpressVisualiserPk.models.*
import com.berger.bergerXpressVisualiserPk.restApis.RestApis
import com.berger.bergerXpressVisualiserPk.restApis.RetroClient
import com.berger.bergerXpressVisualiserPk.utill.Internet
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProductsActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var title: TextView
    private lateinit var back: ImageView

    private lateinit var productsRecycler: RecyclerView
    private lateinit var productsAdapter: ProductsListAdapter

    private var products: ArrayList<Product>? = null

    private var performSelect = false
    private var category: String? = null

    private var customDialog: CustomDialogs? = null
    private var mLastClickTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)
        setViews()
    }

    private fun setViews() {
        title = findViewById(R.id.title)
        title.text = resources.getText(R.string.title_products_screen)
        back = findViewById(R.id.back)
        back.setOnClickListener(this)

        productsRecycler = findViewById(R.id.products_recycler)

        customDialog = CustomDialogs(this)

        if(intent.hasExtra(Constants.INTENT_PERFORM_SELECT))
            performSelect = intent.getBooleanExtra(Constants.INTENT_PERFORM_SELECT, false)

        if(intent.hasExtra(Constants.INTENT_SURFACE))
            category = intent.getStringExtra(Constants.INTENT_SURFACE)

        products = Utills.getProductFromDatabase(this)

        if(products != null){
            if(!category.isNullOrEmpty())
                filterProducts(category!!)
            else
                populateProducts()
        } else {
            getProductsCall()
        }

//        dummyProducts()

        Utills.changeNavigationBarColor(this, Constants.COLOR_THEME)
    }

    private fun getProductsCall() {
        if (Internet.isAvailable(this)) {

            customDialog?.showLoadingDialogue()

            var selectedCategory = Params()

            if(!category.isNullOrEmpty()){
                selectedCategory.category = category
            }

            val restApis = RetroClient.getClient().create(RestApis::class.java)

            val sendCartCall = restApis.getProductsCall(selectedCategory)
            sendCartCall.enqueue(object : Callback<GeneralResponse> {
                override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                    customDialog?.dismissLoadingDialogue()
                    if (response.isSuccessful) {
                        if(response.body() != null && response.body()!!.results != null &&
                            response.body()!!.results?.products != null && response.body()!!.results?.products!!.size > 0){
                            products = response.body()!!.results?.products

                            populateProducts()

                            products?.let {
                                for(i in it.indices){
                                    cacheImage(it[i], i)
                                }
                            }

                            Utills.updateProductsDatabase(this@ProductsActivity, products)
                        }
                    } else {
                        Utills.showToast(this@ProductsActivity, resources.getString(R.string.products_toast_no_products))
                    }
                }

                override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                    customDialog?.dismissLoadingDialogue()
                    Utills.showToast(this@ProductsActivity, resources.getText(R.string.connection_problem).toString())
                }
            })
        } else {
            Utills.showToast(this, resources.getText(R.string.no_internet_connection).toString())
        }
    }

    private fun filterProducts(surface: String){
        for(index in products?.size!!-1 downTo 0){

            if(surface == "Doors" || surface == "Windows"){
                if(!(products?.get(index)?.surfaces != null &&
                            (products?.get(index)?.surfaces!!.contains("Wood") || products?.get(index)?.surfaces!!.contains("Metal")))){
                    products?.removeAt(index)
                }
            } else {
                if(!(products?.get(index)?.surfaces != null && products?.get(index)?.surfaces!!.contains(surface))){
                    products?.removeAt(index)
                }
            }
        }
        populateProducts()
    }

    fun cacheImage(product: Product, index: Int) {
        if (product.localImagePath != null) return

        Glide.with(this)
            .downloadOnly()
            .load(Utills.getCompleteUrl(product.productImage))
            .into(object : CustomTarget<File>() {

                override fun onResourceReady(
                    resource: File,
                    transition: Transition<in File>?
                ) {
                    product.localImagePath = resource.absolutePath
                    Utills.updateProductImageUrl(this@ProductsActivity, resource.absolutePath, index)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    private fun dummyProducts(){

        products = ArrayList()

        var c1 = Colors()
        c1.colorName = "Yellow"
        c1.colorDescription = "Yellow is sunshine. It is a warm color, shining with optimism, enlightenment, and happiness and carrying the promise of a positive future. It instills energy and sparks creative thoughts. Its many shades can be used to effectively invoke different moods and create a pleasant atmosphere. While it works perfectly as the primary color, yellow often works best as a companion to other colors; to perk up a cooler, subdued palette, for a more natural, earthy flavor or outright flamboyance and excitement!"
        c1.colorCodeHex = "#FFD400"

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

        var c2 = Colors()
        c2.colorName = "Red"
        c2.colorDescription = "Romance, passion, danger, happiness…Red signifies intensity in all forms. The hottest of the warm colors, red is bold and beautiful. It has more personal associations than any other color. Recognized as a stimulant, red is inherently exciting and the amount of red is directly related to the level of energy perceived. Red draws attention and used as an accent can really make a great impact. It promotes enthusiasm, action and confidence."
        c2.colorCodeHex = "#FF0000"

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


        var product1 = Product()
        product1.productName = "Elegance Matt Emulsion"
//        product1.productImage = R.drawable.product_1.toString()
        product1.colors = ArrayList()
        product1.colors?.add(c1)
        product1.colors?.add(c2)
        product1.colors?.add(c3)
        product1.colors?.add(c4)
        product1.colors?.add(c5)
        product1.colors?.add(c6)
        product1.colors?.add(c7)
        product1.colors?.add(c8)
        product1.productColorCodeHex = "#ca2754"
        product1.features = ArrayList()
        product1.features?.add("Anti-Bacterial")
        product1.features?.add("Insect & Mosquito Repellent")
        product1.features?.add("Non-Hazardous")
        product1.features?.add("Lead Free")
        product1.features?.add("Excellent Finish")
        product1.coverageSqMeter = 15.0
        product1.coverageSqFeet = 161.0
        product1.recommendedCoats = 3
        product1.sizes = ArrayList()
        var size1 = Size()
        size1.size = 1.0
        size1.unit = "Ltr"
        var size2 = Size()
        size2.size = 4.0
        size2.unit = "Ltr"
        var size3 = Size()
        size3.size = 16.0
        size3.unit = "Ltr"
        product1.sizes?.add(size1)
        product1.sizes?.add(size2)
        product1.sizes?.add(size3)


        var product2 = Product()
        product2.productName = "Silk Emulsion"
//        product2.productImage = R.drawable.product_2.toString()
        product2.colors = ArrayList()
        product2.colors?.add(c1)
        product2.colors?.add(c2)
        product2.colors?.add(c3)
        product2.colors?.add(c4)
        product2.colors?.add(c5)
        product2.colors?.add(c8)
        product2.productColorCodeHex = "#a83e88"
        product2.features = ArrayList()
        product2.features?.add("Anti-fungal properties")
        product2.features?.add("Excellent coverage")
        product2.features?.add("Silk touch")
        product2.features?.add("Highly washable")
        product2.features?.add("Stain resistance")
        product2.coverageSqMeter = 18.5
        product2.coverageSqFeet = 199.0
        product2.recommendedCoats = 2
        product2.sizes = ArrayList()
        var size1p2 = Size()
        size1p2.size = 1.0
        size1p2.unit = "Ltr"
        var size2p2 = Size()
        size2p2.size = 4.0
        size2p2.unit = "Ltr"
        var size3p2 = Size()
        size3p2.size = 16.0
        size3p2.unit = "Ltr"
        product2.sizes?.add(size1p2)
        product2.sizes?.add(size2p2)


        var product3 = Product()
        product3.productName = "VIP Super Gloss Enamel - Metallic"
//        product3.productImage = R.drawable.product_3.toString()
        product3.colors = ArrayList()
        product3.colors?.add(c1)
        product3.colors?.add(c4)
        product3.colors?.add(c6)
        product3.productColorCodeHex = "#23519f"
        product3.features = ArrayList()
        product3.features?.add("Brilliant gloss finish")
        product3.features?.add("High scrub resistance")
        product3.features?.add("Long lasting color shades")
        product3.features?.add("Water resistance")
        product3.features?.add("Stain resistance")
        product3.coverageSqMeter = 14.0
        product3.coverageSqFeet = 151.0
        product3.recommendedCoats = 3
        product3.sizes = ArrayList()
        var size1p3 = Size()
        size1p3.size = 0.91
        size1p3.unit = "Ltr"
        var size2p3 = Size()
        size2p3.size = 3.64
        size2p3.unit = "Ltr"
        var size3p3 = Size()
        size3p3.size = 16.0
        size3p3.unit = "Ltr"
        product3.sizes?.add(size1p3)
        product3.sizes?.add(size2p3)


        var product4 = Product()
        product4.productName = "Weather Pro"
//        product4.productImage = R.drawable.product_4.toString()
        product4.colors = ArrayList()
        product4.colors?.add(c1)
        product4.colors?.add(c2)
        product4.colors?.add(c7)
        product4.productColorCodeHex = "#235a40"
        product4.features = ArrayList()
        product4.features?.add("Highly Durable")
        product4.features?.add("Excellent coverage")
        product4.coverageSqMeter = 13.0
        product4.coverageSqFeet = 140.0
        product4.recommendedCoats = 3
        product4.sizes = ArrayList()
        var size1p4 = Size()
        size1p4.size = 4.0
        size1p4.unit = "Ltr"
        var size2p4 = Size()
        size2p4.size = 4.0
        size2p4.unit = "Ltr"
        var size3p4 = Size()
        size3p4.size = 16.0
        size3p4.unit = "Ltr"
        product4.sizes?.add(size1p4)

        var product5 = Product()
        product5.productName = "Water Based Primer"
//        product5.productImage = R.drawable.product_5.toString()
        product5.colors = ArrayList()
        product5.colors?.add(c1)
        product5.productColorCodeHex = "#00cbcf"
        product5.features = ArrayList()
        product5.features?.add("Resists alkali")
        product5.features?.add("Prevents moisture from substrate.")
        product5.features?.add("Both for interior or exterior")
        product5.coverageSqMeter = 17.0
        product5.coverageSqFeet = 183.0
        product5.recommendedCoats = 1
        product5.sizes = ArrayList()
        var size1p5 = Size()
        size1p5.size = 3.64
        size1p5.unit = "Ltr"
        var size2p5 = Size()
        size2p5.size = 41.56
        size2p5.unit = "Ltr"
        var size3p5 = Size()
        size3p5.size = 16.0
        size3p5.unit = "Ltr"
        product5.sizes?.add(size1p5)
        product5.sizes?.add(size2p5)

        products?.add(product1)
        products?.add(product2)
        products?.add(product3)
        products?.add(product4)
        products?.add(product5)
        products?.add(product1)
        products?.add(product2)
        products?.add(product3)
        products?.add(product4)
        products?.add(product5)
        products?.add(product1)
        products?.add(product2)
        products?.add(product3)
        products?.add(product4)
        products?.add(product5)
        products?.add(product1)
        products?.add(product2)
        products?.add(product3)
        products?.add(product4)
        products?.add(product5)

        populateProducts()
    }

    private fun populateProducts(){
        if (products == null || products?.size == 0) {

        } else {
            productsRecycler.visibility = View.VISIBLE
            productsRecycler.layoutManager = GridLayoutManager(this, 3)
            productsAdapter = ProductsListAdapter(this, products!!, performSelect)
            productsRecycler.adapter = productsAdapter
            productsRecycler.invalidate()
        }
    }

    fun performSelect(selectedProduct: Product){
        val returnIntentOk = Intent()
        returnIntentOk.putExtra(Constants.INTENT_PRODUCT, selectedProduct)
        setResult(Activity.RESULT_OK, returnIntentOk)
        finish()
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
}