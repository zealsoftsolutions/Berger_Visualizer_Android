package com.berger.bergerXpressVisualiserPk.customWidgets;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.berger.bergerXpressVisualiserPk.R;
import com.berger.bergerXpressVisualiserPk.activities.MyIdeaDetailsActivity;
import com.berger.bergerXpressVisualiserPk.activities.MyIdeasActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.MediaStoreSignature;

import java.util.Calendar;

public class CustomDialogs {

    private Context context;

    // Dialog to show loading all over in the app
    private Dialog loadingDialogue;
    private ImageView loadingView;


    // General Dialog
    private ConstraintLayout generalDialogView;
    private Dialog generalDialog;
    private TextView dTitle, dMessage;
    private Button no;
    private Button yes;



    public CustomDialogs(Context context){
        this.context = context;
    }

    //----------------------------------------------------------------------------------------------
    // Loading Dialog

    public void setUpLoadingDialogue(){
        if (context != null && loadingDialogue == null) {
            loadingDialogue = new Dialog(context, R.style.CustomDialog);
            loadingDialogue.setContentView(R.layout.custom_loading_progress);
            loadingDialogue.setCancelable(false);
            loadingDialogue.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            loadingView = loadingDialogue.findViewById(R.id.loading);

            Glide
                    .with(context)
                    .load(R.drawable.berger_loading)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .signature(new MediaStoreSignature("", Calendar.getInstance().getTime().getTime(), 0))
                    .into(loadingView);

//            loadingDialogue.setOnKeyListener(new Dialog.OnKeyListener() {
//
//                @Override
//                public boolean onKey(DialogInterface arg0, int keyCode,
//                                     KeyEvent event) {
//                    // TODO Auto-generated method stub
//                    if (keyCode == KeyEvent.KEYCODE_BACK) {
//
//                        dismissLoadingDialogue();
//                    }
//                    return true;
//                }
//            });
        }
    }

    public void dismissLoadingDialogue(){
        if(loadingDialogue != null && loadingDialogue.isShowing()){
            try {
                loadingDialogue.dismiss();
            } catch (Exception e){ }
        }
    }

    public void showLoadingDialogue(){
        if(loadingDialogue == null)
            setUpLoadingDialogue();

        if(loadingDialogue != null && !loadingDialogue.isShowing()){
            loadingDialogue.show();
            Window window = loadingDialogue.getWindow();
            try {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            } catch (Exception e){ }
        }
    }



    //----------------------------------------------------------------------------------------------
    // General Dialog

    public void setUpGeneralDialog(int index){
        if (context != null && generalDialog == null) {
            generalDialog = new Dialog(context, R.style.CustomDialog);
            generalDialog.setContentView(R.layout.custom_dialog_general);
            generalDialog.setCancelable(false);
            generalDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            generalDialogView = generalDialog.findViewById(R.id.general_dialog);
            dMessage = generalDialog.findViewById(R.id.dialog_message);
            no = generalDialog.findViewById(R.id.no);
            yes = generalDialog.findViewById(R.id.yes);

            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissGeneralDialog();
                }
            });

            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(context instanceof MyIdeasActivity){
                        ((MyIdeasActivity) context).deleteImage(index);
                    }

                    if(context instanceof MyIdeaDetailsActivity){
                        ((MyIdeaDetailsActivity) context).deleteImage(index);
                    }

                    dismissGeneralDialog();
                }
            });
        }
    }

    public void dismissGeneralDialog(){
        if(generalDialog != null && generalDialog.isShowing()){
            try {
                generalDialog.dismiss();
            } catch (Exception e){ }
        }
    }

    public void showGeneralDialog(int index){
        if(generalDialog == null)
            setUpGeneralDialog(index);

        if(generalDialog !=null && !generalDialog.isShowing()){
            generalDialog.show();
//            Window window = generalDialog.getWindow();
//            try {
//                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            } catch (Exception e){ }
        }
    }


}
