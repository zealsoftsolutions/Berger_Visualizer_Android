package com.berger.bergerXpressVisualiserPk.adapters

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import com.berger.bergerXpressVisualiserPk.utill.Constants
import com.berger.bergerXpressVisualiserPk.R
import com.berger.bergerXpressVisualiserPk.activities.ColorShadesActivity
import com.berger.bergerXpressVisualiserPk.models.Colors
import com.berger.bergerXpressVisualiserPk.viewHolders.PrimaryColorsListViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.signature.MediaStoreSignature
import java.util.*
import kotlin.Exception

class PrimaryColorsListAdapter (val activity: Activity, private val timingsList: List<Colors>) : androidx.recyclerview.widget.RecyclerView.Adapter<PrimaryColorsListViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): PrimaryColorsListViewHolder {
        return PrimaryColorsListViewHolder(LayoutInflater.from(activity).inflate(R.layout.single_color_display_item, p0, false))
    }

    override fun getItemCount(): Int {
        return timingsList.size
    }

    override fun onBindViewHolder(holder: PrimaryColorsListViewHolder, p1: Int) {

        val item : Colors = timingsList[p1]

        holder.colorCard.setCardBackgroundColor(Color.parseColor(item.colorCodeHex))
        holder.colorLabel.text = item.colorName

        if(item.colorImageUrl != null){
            Glide
                    .with(activity)
                    .load(item.colorImageUrl)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            return false
                        }
                    })
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.berger_logo)
                    .signature(MediaStoreSignature("", Calendar.getInstance().time.time, 0))
                    .into(holder.colorImage)
        }

        if(item.colorDescription != null)
            holder.colorDescription.text = item.colorDescription

        if(item.labelColorHex != null && item.labelColorHex!!.isNotEmpty()) {

//            if(item.labelColorHex == "#FFFFFF"){
//                holder.colorLabel.setTextColor(Color.parseColor("#000000"))
//            }
            try {
                holder.colorLabel.setTextColor(Color.parseColor(item.labelColorHex))
            } catch (e: Exception) { }
        }

        if(item.colorCodeHex != null && item.colorCodeHex!!.isNotEmpty()){
            try {
                holder.colorLabelBackground.setBackgroundColor(Color.parseColor(item.labelColorHex))
            } catch (e: Exception) { }
        }

        holder.colorCard.setOnClickListener {
            val intent = Intent(activity, ColorShadesActivity::class.java)
            intent.putExtra(Constants.INTENT_COLOR, item)
            activity.startActivity(intent)
        }
    }
}