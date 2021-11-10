package com.berger.bergerXpressVisualiserPk.utill;

import android.content.Context;
import android.content.SharedPreferences;

import com.berger.bergerXpressVisualiserPk.models.DB;
import com.berger.bergerXpressVisualiserPk.models.Idea;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Preferences {

    //-----------------------------------------------------------------------------------------------------------
    // Add, Get and Remove NearByPharmacies from Shared Preferences

    public static void addMyIdeasToSharedPreferences(Context context, List<Idea> ideas){
        Gson gson = new Gson();
        String jsonString = gson.toJson(ideas);
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.MY_IDEAS_TAG_SP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.MY_IDEAS_TAG_MODEL, jsonString);
        editor.apply();
    }

    public static List<Idea> getMyIdeasFromSharedPreferences(Context context){
        Gson gson = new Gson();
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.MY_IDEAS_TAG_SP, Context.MODE_PRIVATE);
        Type type = new TypeToken<List<Idea>>() {}.getType();
        return gson.fromJson(sharedPref.getString(Constants.MY_IDEAS_TAG_MODEL, null), type);
    }

    public static void removeMyIdeasFromSharedPreferences(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.MY_IDEAS_TAG_SP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(Constants.MY_IDEAS_TAG_MODEL).apply();
    }

    //-----------------------------------------------------------------------------------------------------------
    // Add, Get and Remove Database Version from Shared Preferences

    public static void addDatabaseVersionToSharedPreferences(Context context, int version){
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.DB_VERSION_TAG_SP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(Constants.DB_VERSION_TAG_MODEL, version);
        editor.apply();
    }

    public static int getDatabaseVersionFromSharedPreferences(Context context){
        Gson gson = new Gson();
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.DB_VERSION_TAG_SP, Context.MODE_PRIVATE);
        return sharedPref.getInt(Constants.DB_VERSION_TAG_MODEL, 0);
    }

    public static void removeDatabaseVersionFromSharedPreferences(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.DB_VERSION_TAG_SP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(Constants.DB_VERSION_TAG_MODEL).apply();
    }

    //-----------------------------------------------------------------------------------------------------------
    // Add, Get and Remove Data of Database from Shared Preferences

    public static void addDataOfDatabaseToSharedPreferences(Context context, DB database){
        Gson gson = new Gson();
        String jsonString = gson.toJson(database);
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.DB_TAG_SP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.DB_TAG_MODEL, jsonString);
        editor.apply();
    }

    public static DB getDataOfDatabaseFromSharedPreferences(Context context){
        Gson gson = new Gson();
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.DB_TAG_SP, Context.MODE_PRIVATE);
        Type type = new TypeToken<DB>() {}.getType();
        return gson.fromJson(sharedPref.getString(Constants.DB_TAG_MODEL, null), type);
    }

    public static void removeDataOfDatabaseFromSharedPreferences(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.DB_TAG_SP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(Constants.DB_TAG_MODEL).apply();
    }
}
