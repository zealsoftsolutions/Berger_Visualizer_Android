package com.berger.bergerXpressVisualiserPk.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ResultResponses {

    @SerializedName("version")
    @Expose
    val dbVersion: Int? = null

    @SerializedName("colors")
    @Expose
    val colors: ArrayList<Colors>? = null

    @SerializedName("categories")
    @Expose
    val surfaceCategories: ArrayList<String>? = null

    @SerializedName("products")
    @Expose
    val products: ArrayList<Product>? = null

    @SerializedName("ideas")
    @Expose
    val inspirationalIdeas: ArrayList<Idea>? = null

    @SerializedName("aboutUs")
    @Expose
    val aboutUs: String? = null

    @SerializedName("contactUs")
    @Expose
    val contactUsList: ArrayList<Contact>? = null
}