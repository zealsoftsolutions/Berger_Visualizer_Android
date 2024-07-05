package com.berger.bergerXpressVisualiserPk.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Size: Serializable {

    @SerializedName("quantity")
    @Expose
    var size: Double? = null

    @SerializedName("unit")
    @Expose
    var unit: String? = null
}