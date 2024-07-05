package com.berger.bergerXpressVisualiserPk.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Params {

    @SerializedName("productId")
    @Expose
    var productId: String? = null

    @SerializedName("category")
    @Expose
    var category: String? = null
}