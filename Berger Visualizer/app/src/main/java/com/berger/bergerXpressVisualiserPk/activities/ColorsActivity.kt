package com.berger.bergerXpressVisualiserPk.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.berger.bergerXpressVisualiserPk.utill.Constants
import com.berger.bergerXpressVisualiserPk.R
import com.berger.bergerXpressVisualiserPk.utill.Utills
import com.berger.bergerXpressVisualiserPk.adapters.PrimaryColorsListAdapter
import com.berger.bergerXpressVisualiserPk.models.Colors
import com.berger.bergerXpressVisualiserPk.models.Shade

class ColorsActivity : AppCompatActivity() {

    private lateinit var title: TextView
    private lateinit var back: ImageView

    private lateinit var colorsRecycler: RecyclerView
    private lateinit var colorsAdapter: PrimaryColorsListAdapter

    private var colors: ArrayList<Colors>? = null

    private var mLastClickTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_colors)

        setViews()
    }

    private fun setViews() {
        title = findViewById(R.id.title)
        title.text = resources.getText(R.string.title_primary_colors_screen)
        back = findViewById(R.id.back)

        colorsRecycler = findViewById(R.id.colors_recycler)

        Utills.changeNavigationBarColor(this, Constants.COLOR_THEME)
        dummyColors()
    }

    private fun dummyColors(){

        colors = ArrayList()

        //------------------------------------------------------------------------------------------
        // Yellow

        var c1 = Colors()
        c1.colorName = "Yellow"
        c1.colorDescription = "Yellow is sunshine. It is a warm color, shining with optimism, enlightenment, and happiness and carrying the promise of a positive future. It instills energy and sparks creative thoughts. Its many shades can be used to effectively invoke different moods and create a pleasant atmosphere. While it works perfectly as the primary color, yellow often works best as a companion to other colors; to perk up a cooler, subdued palette, for a more natural, earthy flavor or outright flamboyance and excitement!"
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
        c2.colorDescription = "Romance, passion, danger, happiness…Red signifies intensity in all forms. The hottest of the warm colors, red is bold and beautiful. It has more personal associations than any other color. Recognized as a stimulant, red is inherently exciting and the amount of red is directly related to the level of energy perceived. Red draws attention and used as an accent can really make a great impact. It promotes enthusiasm, action and confidence."
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
        c3.colorDescription = "A universal favorite regardless of age or gender, blue is seen as trustworthy, dependable, and committed. The color of ocean and sky, blue is perceived as a constant in our lives. As the collective color of the spirit, it invokes rest and calm. However, it can also be very bold and dramatic depending on the shade. The coolest of colors and the most versatile, blue is an ideal color for almost any location or atmosphere."
        c3.colorCodeHex = "#0000FF"
        c3.labelColorHex = "#FFFFFF"
//        c3.colorImageUrl = R.drawable.image_color_blue


        //------------------------------------------------------------------------------------------
        // Orange

        var c4 = Colors()
        c4.colorName = "Orange"
        c4.colorDescription = "The color orange radiates warmth and happiness, combining the physical energy and stimulation of red with the cheerfulness of yellow. It is an optimistic and uplifting color, rejuvenating our spirit. It invokes spontaneity and a positive outlook in life and is a great color to have around."
        c4.colorCodeHex = "#FF6600"
        c4.labelColorHex = "#FFFFFF"
//        c4.colorImageUrl = R.drawable.image_color_orange


        //------------------------------------------------------------------------------------------
        // Green

        var c5 = Colors()
        c5.colorName = "Green"
        c5.colorDescription = "The color most present in nature, green is refreshing and tranquil with a natural balance of warm and cool undertones. It has a calming effect due to its abundance in the natural world and relives stress. The second most popular color after blue, green brightens up every environment for a pleasant and serene effect."
        c5.colorCodeHex = "#00FF00"
        c5.labelColorHex = "#FFFFFF"
//        c5.colorImageUrl = R.drawable.image_color_green


        //------------------------------------------------------------------------------------------
        // Violet

        var c6 = Colors()
        c6.colorName = "Violet"
        c6.colorDescription = "Violet is the highest color in the visible spectrum and is one of the cool colors. Vibrant and vivid, violet promotes imagination and uniqueness. It has an inherent mystery and is often associated with fantasy and the future. A great choice for an artistic and relaxed atmosphere, violet also lends a distinctive quality of luxury and nobility."
        c6.colorCodeHex = "#C800FF"
        c6.labelColorHex = "#FFFFFF"
//        c6.colorImageUrl = R.drawable.image_color_violet


        //------------------------------------------------------------------------------------------
        // Brown

        var c7 = Colors()
        c7.colorName = "Brown"
        c7.colorDescription = "Brown is one of the most versatile colors due to its neutrality. The color of earth and wood, it has an instant effect of wholesomeness and stability. Brown promotes comfort and simplicity. Intelligently used brown can give a very welcoming impression of openness and honesty, as well as sophistication and elegance. That is why it is usually a good choice to use brown in drawing/dining rooms."
        c7.colorCodeHex = "#A52A2A"
        c7.labelColorHex = "#FFFFFF"
//        c7.colorImageUrl = R.drawable.image_color_brown


        //------------------------------------------------------------------------------------------
        // White

        var c8 = Colors()
        c8.colorName = "White"
        c8.colorDescription = "The color of light, white projects purity and clarity. It discourages clutter and brings order and freshness. It stands for wholeness and completion and also represents openness and truth. Smart usage of white in the living space promotes a positive, glowing outlook on life. It contains an equal balance of all the colors in the spectrum and therefore, is an excellent complement to any color."
        c8.colorCodeHex = "#FFFFFF"
        c8.labelColorHex = "#000000"
//        c8.colorImageUrl = R.drawable.image_color_white

        colors?.add(c1)
        colors?.add(c2)
        colors?.add(c3)
        colors?.add(c4)
        colors?.add(c5)
        colors?.add(c6)
        colors?.add(c7)
        colors?.add(c8)

        populateColors()
    }

    private fun populateColors(){
        if (colors == null || colors?.size == 0) {

        } else {
            colorsRecycler.visibility = View.VISIBLE
            colorsRecycler.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 2)
            colorsAdapter = PrimaryColorsListAdapter(this, colors!!)
            colorsRecycler.adapter = colorsAdapter
            colorsRecycler.invalidate()
        }
    }


}