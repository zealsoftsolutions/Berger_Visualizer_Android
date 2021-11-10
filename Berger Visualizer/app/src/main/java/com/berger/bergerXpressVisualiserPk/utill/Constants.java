package com.berger.bergerXpressVisualiserPk.utill;

import android.graphics.Point;

import com.berger.bergerXpressVisualiserPk.models.Product;
import com.berger.bergerXpressVisualiserPk.models.Shade;

import java.util.ArrayList;

public class Constants {

    public static final String SERVER_IP = "http://3.15.41.136/";         // Test Server
//    public static final String SERVER_IP = "http://10.2.1.79:3002/";         // Local Server

    public static final String COLOR_BLACK = "black";
    public static final String COLOR_WHITE = "white";
    public static final String COLOR_THEME = "theme";

    public static final int PERFORMANCE_LOW = 5;
    public static final int PERFORMANCE_MEDIUM = 4;
    public static final int PERFORMANCE_HIGH = 1;


    public static final String INTENT_COLOR = "color";
    public static final String INTENT_PRODUCTS = "products";
    public static final String INTENT_PRODUCT = "product";
    public static final String INTENT_PRODUCT_ID = "product_id";
    public static final String INTENT_PERFORM_SELECT = "performSelect";
    public static final String INTENT_SURFACE = "surface";
    public static final String INTENT_IMAGE_URL = "imageUrl";
    public static final String INTENT_IMAGE_BITMAP = "imageBitmap";
    public static final String INTENT_URL_TYPE = "type";
    public static final String INTENT_RESIZING = "resizing";
    public static final String INTENT_GALLERY = "gallery";
    public static final String INTENT_CAMERA = "camera";
    public static final String INTENT_SHADE = "shades";
    public static final String INTENT_COLORS = "colors";
    public static final String INTENT_MY_IDEAS = "my_ideas";
    public static final String INTENT_MY_IDEA = "my_idea";
    public static final String INTENT_MY_IDEA_SHADES = "my_idea_shades";
    public static final String INTENT_IDEA_INDEX = "idea_index";
    public static final String INTENT_FROM_VISUALIZER = "from_visualizer";


    // Social Links

    public static final String SOCIAL_WEB = "WEB";
    public static final String SOCIAL_FB = "FB";
    public static final String SOCIAL_TWITTER = "TWITTER";
    public static final String SOCIAL_INSTA = "INSTA";
    public static final String SOCIAL_PINT = "PINT";
    public static final String SOCIAL_GOOGLE = "GOOGLE";

    public static Shade SELECTED_SHADE = null;
    public static int SELECTED_POSITION = -1;
    public static String SELECTED_PRODUCT_ID = null;

    public static ArrayList<Product> products = new ArrayList();


    // Tags to save/retrieve data from Shared Preferences

    public static final String MY_IDEAS_TAG_SP = "MY_IDEAS_TAG_SP";
    public static final String DB_VERSION_TAG_SP = "DB_VERSION_TAG_SP";
    public static final String DB_TAG_SP = "DB_TAG_SP";
    public static final String PRODUCTS_TAG_SP = "PRODUCTS_TAG_SP";
    public static final String COLOR_PALLET_TAG_SP = "COLOR_PALLET_TAG_SP";
    public static final String SURFACES_TAG_SP = "SURFACES_TAG_SP";
    public static final String ABOUT_US_TAG_SP = "ABOUT_US_TAG_SP";
    public static final String CONTACT_US_TAG_SP = "CONTACT_US_TAG_SP";

    public static final String MY_IDEAS_TAG_MODEL = "MY_IDEAS_TAG_MODEL";
    public static final String DB_VERSION_TAG_MODEL = "DB_VERSION_TAG_MODEL";
    public static final String DB_TAG_MODEL = "DB_TAG_MODEL";
    public static final String PRODUCTS_TAG_MODEL = "PRODUCTS_TAG_MODEL";
    public static final String COLOR_PALLET_TAG_MODEL = "COLOR_PALLET_TAG_MODEL";
    public static final String SURFACES_TAG_MODEL = "SURFACES_TAG_MODEL";
    public static final String ABOUT_US_TAG_MODEL = "ABOUT_US_TAG_MODEL";
    public static final String CONTACT_US_TAG_MODEL = "CONTACT_US_TAG_MODEL";


    public static final String TYPE_INSPIRATIONAL_IDEAS = "TYPE_INSPIRATIONAL_IDEAS";
    public static final String TYPE_MY_IDEAS = "TYPE_MY_IDEAS";


    public static ArrayList<Point> points = null;

    public static String IMAGE_BITMAP = null;
}
