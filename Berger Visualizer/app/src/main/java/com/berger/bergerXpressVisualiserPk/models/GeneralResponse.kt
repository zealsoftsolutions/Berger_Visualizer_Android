package com.berger.bergerXpressVisualiserPk.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GeneralResponse {

    @SerializedName("success")
    @Expose
    private val success = false

    @SerializedName("message")
    @Expose
    private val msg: String? = null

    @SerializedName("result")
    @Expose
    val results: ResultResponses? = null

    @SerializedName("count")
    @Expose
    var count: Int? = null
}