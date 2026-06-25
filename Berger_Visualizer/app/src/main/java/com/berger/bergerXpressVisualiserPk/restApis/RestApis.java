package com.berger.bergerXpressVisualiserPk.restApis;

import com.berger.bergerXpressVisualiserPk.models.GeneralResponse;
import com.berger.bergerXpressVisualiserPk.models.Params;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface RestApis {

    //----------------------------------------------------------------------------------------------
    // Get Database Version Call

    @POST("berger/version")
    @Headers("Content-Type: application/json")
    Call<GeneralResponse> getDatabaseVersion();

    //----------------------------------------------------------------------------------------------
    // Get Color Pallet Call

    @POST("berger/shades/all")
    @Headers("Content-Type: application/json")
    Call<GeneralResponse> getColorPalletCall(@Body Params productId);

    //----------------------------------------------------------------------------------------------
    // Get Surfaces List Call

    @POST("berger/products/categories")
    @Headers("Content-Type: application/json")
    Call<GeneralResponse> getProductCategoriesListCall();

    //----------------------------------------------------------------------------------------------
    // Get Products Call

    @POST("berger/products/all")
    @Headers("Content-Type: application/json")
    Call<GeneralResponse> getProductsCall(@Body Params category);


    //----------------------------------------------------------------------------------------------
    // Get Inspirational Ideas Call

    @POST("berger/products/ideas")
    @Headers("Content-Type: application/json")
    Call<GeneralResponse> getInspirationalIdeasCall();


    //----------------------------------------------------------------------------------------------
    // Get About Us Call

    @POST("berger/about-us")
    @Headers("Content-Type: application/json")
    Call<GeneralResponse> getAboutUsCall();

    //----------------------------------------------------------------------------------------------
    // Get Contact Us Call

    @POST("berger/contact-us")
    @Headers("Content-Type: application/json")
    Call<GeneralResponse> getContactUsCall();

}