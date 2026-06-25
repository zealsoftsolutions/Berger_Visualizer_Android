package com.berger.bergerXpressVisualiserPk.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Product: Serializable {

    @SerializedName("url")
    @Expose
    var productImage: String? = null

    var savedImageUrl: String? = null

    var localImagePath: String? = null

    @SerializedName("_id")
    @Expose
    var id: String? = null

    @SerializedName("name")
    @Expose
    var productName: String? = null

    @SerializedName("description")
    @Expose
    var productDescription: String? = null

    @SerializedName("uses")
    @Expose
    var uses: String? = null

    @SerializedName("surfacePreparation")
    @Expose
    var howToPrepare: String? = null

    @SerializedName("application")
    @Expose
    var howToApply: String? = null

    @SerializedName("dryingTime")
    @Expose
    var dryingTime: String? = null

    @SerializedName("safetyPrecautions")
    @Expose
    var safetyPrecautions: ArrayList<String>? = null

    @SerializedName("recommendedCoats")
    @Expose
    var recommendedCoats: Int? = null

    @SerializedName("packSize")
    @Expose
    var sizes: ArrayList<Size>? = null

    @SerializedName("colors")
    @Expose
    var colors: ArrayList<Colors>? = null

    @SerializedName("coverage")
    @Expose
    var coverage: String? = null

    @SerializedName("coverageInSquareFeet")
    @Expose
    var coverageSqFeet: Double? = null

    @SerializedName("coverageInSquareMeter")
    @Expose
    var coverageSqMeter: Double? = null

    @SerializedName("type")
    @Expose
    var productType: String? = null

    @SerializedName("surfaces")
    @Expose
    var surfaces: ArrayList<String>? = null

    var productColorCodeHex: String? = null
    var features: ArrayList<String>? = null
    var packSizes: ArrayList<Double>? = null
}