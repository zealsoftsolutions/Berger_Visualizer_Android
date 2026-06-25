package com.berger.bergerXpressVisualiserPk.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Colors: Serializable{

    @SerializedName("colorName")
    @Expose
    var colorName: String? = null

    @SerializedName("hexcode")
    @Expose
    var colorCodeHex: String? = null

    var labelColorHex: String? = null

    @SerializedName("url")
    @Expose
    var imageUrl: String? = null

    var colorImageUrl: Int? = null

    @SerializedName("shades")
    @Expose
    var shades: ArrayList<Shade>? = null

    var colorDescription: String? = null


}