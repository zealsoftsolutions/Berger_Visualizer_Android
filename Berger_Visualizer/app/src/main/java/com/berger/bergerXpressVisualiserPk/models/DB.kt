package com.berger.bergerXpressVisualiserPk.models

class DB {

    var colorsDbVersion = 0
    var colors: ArrayList<Colors>? = null

    var productsDbVersion = 0
    var products: ArrayList<Product>? = null

    var surfacesDbVersion = 0
    var surfaces: ArrayList<String>? = null

    var inspirationalIdeasDbVersion = 0
    var inspirationalIdeas: ArrayList<Idea>? = null

    var aboutUsDbVersion = 0
    var aboutUs: String? = null

    var contactUsDbVersion = 0
    var contactUs: ArrayList<Contact>? = null
}