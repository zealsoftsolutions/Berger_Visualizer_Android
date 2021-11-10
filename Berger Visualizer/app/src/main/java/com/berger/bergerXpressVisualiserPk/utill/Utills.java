package com.berger.bergerXpressVisualiserPk.utill;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.berger.bergerXpressVisualiserPk.R;
import com.berger.bergerXpressVisualiserPk.activities.PaintActivity;
import com.berger.bergerXpressVisualiserPk.models.Colors;
import com.berger.bergerXpressVisualiserPk.models.Contact;
import com.berger.bergerXpressVisualiserPk.models.DB;
import com.berger.bergerXpressVisualiserPk.models.Idea;
import com.berger.bergerXpressVisualiserPk.models.Product;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static java.lang.Integer.parseInt;

public class Utills {

    public static Toast toast;

    public static void transparentToolbar(Activity activity, boolean showShade) {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);

            if (showShade) {
                activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.bottomBarColor));
            } else {
                activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        }
    }

    public static void transparentNavigation(Activity activity, boolean transparent, boolean showShadow) {

        if (transparent) {
            if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
                setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, true);
            }
            if (Build.VERSION.SDK_INT >= 19) {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            }
            if (Build.VERSION.SDK_INT >= 21) {
                setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, false);
                if (showShadow) {
                    activity.getWindow().setNavigationBarColor(activity.getResources().getColor(R.color.bottomBarColor));
                } else {
                    activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);
                }
            }
        } else {
            if (Build.VERSION.SDK_INT >= 21) {
                activity.getWindow().setNavigationBarColor(activity.getResources().getColor(R.color.bottomBarColor));
            }
        }
    }

    public static void changeNavigationBarColor(Activity activity, String color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (color.equals(Constants.COLOR_THEME)) {
                activity.getWindow().setNavigationBarColor(activity.getResources().getColor(R.color.themeStartColorBerger));
            } else if(color.equals(Constants.COLOR_BLACK)){
                activity.getWindow().setNavigationBarColor(activity.getResources().getColor(R.color.bottomBarColor));
            } else if(color.equals(Constants.COLOR_WHITE)){
                activity.getWindow().setNavigationBarColor(activity.getResources().getColor(R.color.white));
            } else {
                activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);
            }
//            activity.getWindow().setNavigationBarColor(activity.getResources().getColor(R.color.green));
        }
    }

    public static void setNavigationBarColor(Activity activity, String color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                activity.getWindow().setNavigationBarColor(Color.parseColor(color));
            } catch (Exception e) {}
        }
    }

    private static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public static void showToast(Activity activity, String text){

        if(toast != null){
            toast.cancel();
        }

        toast = new Toast(activity);

        LayoutInflater inflater = activity.getLayoutInflater();

        View layout = inflater.inflate(R.layout.custom_toast_design,
                (ViewGroup) ((Activity) activity).findViewById(R.id.toast_root));
        TextView toastTextView = (TextView) layout.findViewById(R.id.toast_text);
        toastTextView.setText(text);

        if(activity instanceof PaintActivity)
            toast.setGravity(Gravity.CENTER, 0, 0);

        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public static String getCompleteUrl(String url){

        String prefix = Constants.SERVER_IP;
        prefix = prefix.substring(0, prefix.length() -1);

        if(url != null && !url.equals("")){
            if(url.substring(0,1).equals("/")){
                url = prefix + url;
            }
        }

        return url;
    }

    public static String getMyIdeaFileName(){

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format1 = new SimpleDateFormat("ddMMyyyy_hhmmss");

        return "my_idea_" + format1.format(cal.getTime()) + ".png";
    }

    public static Boolean isValidHex(String hexCode){
        try {
            Color.parseColor(hexCode);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public static String getValidHex(String hexCode){
        if(hexCode.startsWith("#")){
            return hexCode;
        } else {
            return "#" + hexCode;
        }
    }

    public static String formatResultValue(Double result){
        try {
            DecimalFormat formatter;
            formatter = new DecimalFormat("#,###,##0.00");
            return formatter.format(result);
        } catch (Exception e){
            return result.toString();
        }
    }

    public static String setLabelColorAccordingToBackground(Context context, String hexCode){
        try {
            int r = parseInt(hexCode.substring(1, 3), 16);
            int g = parseInt(hexCode.substring(3, 5), 16);
            int b = parseInt(hexCode.substring(5, 7), 16);
            int luma = ((r * 299) + (g * 587) + (b * 114)) / 1000;

            if (luma > 155) {
                return context.getString(R.string.label_color_with_light_background);
            } else {
                return context.getString(R.string.label_color_with_dark_background);
            }
        } catch (Exception e){
            return context.getString(R.string.label_color_with_light_background);
        }
    }

    public static boolean makeCall(Context context, String phone) {
        if(phone != null && !phone.equals("")) {
            Intent callIntent = new Intent(Intent.ACTION_VIEW);
            callIntent.setData(Uri.parse("tel:" + phone));
            context.startActivity(callIntent);
            return true;
        } else {
            return false;
        }
    }

    public static void openBrowser(Context context, String urll){
        String url = urll;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }

    public static void openKeyboard(Context context, View view) {
        InputMethodManager imgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static Bitmap rotateImage(Bitmap source, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public static Bitmap handleSamplingAndRotationBitmap(Context context, Uri selectedImage)
            throws IOException {
        int MAX_HEIGHT = 1024;
        int MAX_WIDTH = 1024;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIfRequired(img, selectedImage);
        return img;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {

        ExifInterface ei = new ExifInterface(selectedImage.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    public static ArrayList<Colors> getPalletFromDatabase(Context context){
        DB data = Preferences.getDataOfDatabaseFromSharedPreferences(context);

        if(data != null && data.getColors() != null && data.getColors().size() > 0 &&
                Preferences.getDatabaseVersionFromSharedPreferences(context) == data.getColorsDbVersion()){
            return data.getColors();
        } else {
            return null;
        }
    }

    public static void updatePalletDatabase(Context context, ArrayList<Colors> colors){
        DB data = Preferences.getDataOfDatabaseFromSharedPreferences(context);

        if(data == null)
            data = new DB();

        data.setColors(colors);
        data.setColorsDbVersion(Preferences.getDatabaseVersionFromSharedPreferences(context));

        Preferences.addDataOfDatabaseToSharedPreferences(context, data);
    }

    public static ArrayList<Product> getProductFromDatabase(Context context){
        DB data = Preferences.getDataOfDatabaseFromSharedPreferences(context);

        if(data != null && data.getProducts() != null && data.getProducts().size() > 0 &&
                Preferences.getDatabaseVersionFromSharedPreferences(context) == data.getProductsDbVersion()){
            return data.getProducts();
        } else {
            return null;
        }
    }

    public static void updateProductsDatabase(Context context, ArrayList<Product> products){
        DB data = Preferences.getDataOfDatabaseFromSharedPreferences(context);

        if(data == null)
            data = new DB();

        data.setProducts(products);
        data.setProductsDbVersion(Preferences.getDatabaseVersionFromSharedPreferences(context));

        Preferences.addDataOfDatabaseToSharedPreferences(context, data);
    }

    public static void updateProductImagesDatabase(Context context, String bitmap, int index){
        DB data = Preferences.getDataOfDatabaseFromSharedPreferences(context);

        if(data == null)
            data = new DB();

        if(data.getProducts() != null && index < data.getProducts().size()) {
            data.getProducts().get(index).setSavedImageUrl(bitmap);
        }

        Preferences.addDataOfDatabaseToSharedPreferences(context, data);
    }

    public static ArrayList<String> getSurfacesFromDatabase(Context context){
        DB data = Preferences.getDataOfDatabaseFromSharedPreferences(context);

        if(data != null && data.getSurfaces() != null && data.getSurfaces().size() > 0 &&
                Preferences.getDatabaseVersionFromSharedPreferences(context) == data.getSurfacesDbVersion()){
            return data.getSurfaces();
        } else {
            return null;
        }
    }

    public static void updateSurfacesDatabase(Context context, ArrayList<String> surfaces){
        DB data = Preferences.getDataOfDatabaseFromSharedPreferences(context);

        if(data == null)
            data = new DB();

        data.setSurfaces(surfaces);
        data.setSurfacesDbVersion(Preferences.getDatabaseVersionFromSharedPreferences(context));

        Preferences.addDataOfDatabaseToSharedPreferences(context, data);
    }

    public static ArrayList<Idea> getInspirationalIdeasFromDatabase(Context context){
        DB data = Preferences.getDataOfDatabaseFromSharedPreferences(context);

        if(data != null && data.getInspirationalIdeas() != null && data.getInspirationalIdeas().size() > 0 &&
                Preferences.getDatabaseVersionFromSharedPreferences(context) == data.getInspirationalIdeasDbVersion()){
            return data.getInspirationalIdeas();
        } else {
            return null;
        }
    }

    public static void updateInspirationalIdeasDatabase(Context context, ArrayList<Idea> inspirationalIdeas){
        DB data = Preferences.getDataOfDatabaseFromSharedPreferences(context);

        if(data == null)
            data = new DB();

        data.setInspirationalIdeas(inspirationalIdeas);
        data.setInspirationalIdeasDbVersion(Preferences.getDatabaseVersionFromSharedPreferences(context));

        Preferences.addDataOfDatabaseToSharedPreferences(context, data);
    }

    public static void updateInspirationalIdeasImagesDatabase(Context context, String bitmap, int index){
        DB data = Preferences.getDataOfDatabaseFromSharedPreferences(context);

        if(data == null)
            data = new DB();

        if(data.getInspirationalIdeas() != null && index < data.getInspirationalIdeas().size()) {
            data.getInspirationalIdeas().get(index).setSavedImageUrl(bitmap);
        }

        Preferences.addDataOfDatabaseToSharedPreferences(context, data);
    }

    public static ArrayList<Contact> getContactUsFromDatabase(Context context){
        DB data = Preferences.getDataOfDatabaseFromSharedPreferences(context);

        if(data != null && data.getContactUs() != null && data.getContactUs().size() > 0 &&
                Preferences.getDatabaseVersionFromSharedPreferences(context) == data.getContactUsDbVersion()){
            return data.getContactUs();
        } else {
            return null;
        }
    }

    public static void updateContactUsDatabase(Context context, ArrayList<Contact> contactUs){
        DB data = Preferences.getDataOfDatabaseFromSharedPreferences(context);

        if(data == null)
            data = new DB();

        data.setContactUs(contactUs);
        data.setContactUsDbVersion(Preferences.getDatabaseVersionFromSharedPreferences(context));

        Preferences.addDataOfDatabaseToSharedPreferences(context, data);
    }

    public static String getAboutUsFromDatabase(Context context){
        DB data = Preferences.getDataOfDatabaseFromSharedPreferences(context);

        if(data != null && data.getAboutUs() != null && !data.getAboutUs().isEmpty() &&
                Preferences.getDatabaseVersionFromSharedPreferences(context) == data.getAboutUsDbVersion()){
            return data.getAboutUs();
        } else {
            return null;
        }
    }

    public static void updateAboutUsDatabase(Context context, String aboutUs){
        DB data = Preferences.getDataOfDatabaseFromSharedPreferences(context);

        if(data == null)
            data = new DB();

        data.setAboutUs(aboutUs);
        data.setAboutUsDbVersion(Preferences.getDatabaseVersionFromSharedPreferences(context));

        Preferences.addDataOfDatabaseToSharedPreferences(context, data);
    }

    public static String bitmapToString(Bitmap bitmap){
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            return Base64.encodeToString(b, Base64.DEFAULT);
        } catch (Exception e){
            return null;
        }
    }

    public static Bitmap stringToBitmap(String image){
        try {
            byte[] imageAsBytes = Base64.decode(image.getBytes(), Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        } catch (Exception e){
            return null;
        }
    }

    public static String saveInspirationalIdeasOnDeviceGetUrl(Bitmap bitmap){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            File direct = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + "/Berger");
            if (!direct.exists()) {
                File mainDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + "/Berger");
                mainDirectory.mkdirs();
            }

            File subDirect = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + "/Berger/InspirationalIdeas");
            if (!subDirect.exists()) {
                File subDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + "/Berger/InspirationalIdeas");
                subDirectory.mkdirs();
            }

            File file = new File(
                    new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + "/Berger/InspirationalIdeas"),
                    getInspirationalIdeaFileName()
            );
            if (file.exists()) {
                file.delete();
            }

            if(file != null){
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                    out.flush();
                    out.close();
                    return file.getAbsolutePath();
                } catch (Exception e) {
                    return "";
                }
            }

            return "";
        } else {
            File direct = new File(Environment.getExternalStorageDirectory().toString() + "/Berger");
            if (!direct.exists()) {
                File mainDirectory = new File("/sdcard/Berger/");
                mainDirectory.mkdirs();
            }

            File subDirect = new File(Environment.getExternalStorageDirectory().toString() + "/Berger/InspirationalIdeas");
            if (!subDirect.exists()) {
                File subDirectory = new File("/sdcard/Berger/InspirationalIdeas");
                subDirectory.mkdirs();
            }

            File file = new File(new File("/sdcard/Berger/InspirationalIdeas"), getInspirationalIdeaFileName());
            if (file.exists()) {
                file.delete();
            }

            if(file != null){
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                    out.flush();
                    out.close();
                    return file.getAbsolutePath();
                } catch (Exception e) {
                    return "";
                }
            }

            return "";
        }
    }

    public static String saveProductImagesOnDeviceGetUrl(Bitmap bitmap, String productName){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            File direct = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + "/Berger");
            if (!direct.exists()) {
                File mainDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + "/Berger");
                mainDirectory.mkdirs();
            }

            File subDirect = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + "/Berger/Products");
            if (!subDirect.exists()) {
                File subDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + "/Berger/Products");
                subDirectory.mkdirs();
            }

            File file = new File(
                    new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + "/Berger/Products"),
                    getProductFileName(productName)
            );
            if (file.exists()) {
                file.delete();
            }

            if(file != null){
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                    out.flush();
                    out.close();
                    return file.getAbsolutePath();
                } catch (Exception e) {
                    return "";
                }
            }
        } else {
            File direct = new File(Environment.getExternalStorageDirectory().toString() + "/Berger");
            if (!direct.exists()) {
                File mainDirectory = new File("/sdcard/Berger/");
                mainDirectory.mkdirs();
            }

            File subDirect = new File(Environment.getExternalStorageDirectory().toString() + "/Berger/Products");
            if (!subDirect.exists()) {
                File subDirectory = new File("/sdcard/Berger/Products");
                subDirectory.mkdirs();
            }

            File file = new File(new File("/sdcard/Berger/Products"), getProductFileName(productName));
            if (file.exists()) {
                file.delete();
            }

            if(file != null){
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                    out.flush();
                    out.close();
                    return file.getAbsolutePath();
                } catch (Exception e) {
                    return "";
                }
            }
        }
        return "";
    }

    public static String getInspirationalIdeaFileName(){

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format1 = new SimpleDateFormat("ddMMyyyy_hhmmss");

        return "inspirational_idea_" + format1.format(cal.getTime()) + ".png";
    }

    public static String getProductFileName(String productName){

        return productName.replace(" ", "_") + ".png";
    }

    public static void getDirections(Context context, Double lat, Double lng) {
        if(lat != null && lng != null && lat != 0.0 && lng != 0.0) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority("www.google.com").appendPath("maps").appendPath("dir").appendPath("").appendQueryParameter("api", "1")
                    .appendQueryParameter("destination", lat + "," + lng);
            String url = builder.build().toString();
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            context.startActivity(i);
        }
    }
}
