package com.berger.bergerXpressVisualiserPk.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Shade: Serializable {

    @SerializedName("_id")
    @Expose
    var id: String? = null

    @SerializedName("shadeName")
    @Expose
    var shadeName: String? = null

    @SerializedName("shadeCode")
    @Expose
    var shadeCode: String? = null

    @SerializedName("hexCode")
    @Expose
    var shadeCodeHex: String? = null

    @SerializedName("labelHexCode")
    @Expose
    var labelColorHex: String? = null

    var colorDescription: String? = null
    var colorImageUrl: Int? = null

    var r: Int? = null
    var g: Int? = null
    var b: Int? = null
}