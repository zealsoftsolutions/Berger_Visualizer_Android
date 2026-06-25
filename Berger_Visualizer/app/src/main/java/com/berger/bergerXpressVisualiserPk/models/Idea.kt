package com.berger.bergerXpressVisualiserPk.models

import android.graphics.Point
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Idea: Serializable {

    @SerializedName("url")
    @Expose
    var imageUrl: String? = null

    var savedImageUrl: String? = null

    var localImageUrl: String? = null

    var shadesUsed: ArrayList<Shade>? = null

    var maskingTapes: ArrayList<Point>? = null
}