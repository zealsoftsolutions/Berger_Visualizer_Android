package com.berger.bergerXpressVisualiserPk.adapters

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.berger.bergerXpressVisualiserPk.R
import com.berger.bergerXpressVisualiserPk.models.Shade
import com.berger.bergerXpressVisualiserPk.utill.Utills
import com.berger.bergerXpressVisualiserPk.viewHolders.ColorFamilyListViewHolder

class ShadesUsedListAdapter (val activity: Activity, private val shades: List<Shade>) : androidx.recyclerview.widget.RecyclerView.Adapter<ColorFamilyListViewHolder>() {

    var selectedColor = -1

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ColorFamilyListViewHolder {
        return ColorFamilyListViewHolder(LayoutInflater.from(activity).inflate(R.layout.single_color_family_item, p0, false))
    }

    override fun getItemCount(): Int {
        return shades.size
    }

    override fun onBindViewHolder(holder: ColorFamilyListViewHolder, p1: Int) {

        val item : Shade = shades[p1]

        holder.colorDisplay.setBackgroundColor(Color.parseColor(Utills.getValidHex(item.shadeCodeHex)))
//        holder.colorDisplay.setColorFilter(Color.parseColor(item.colorCodeHex))

        if(p1 == selectedColor){
            holder.colorSelectCard.setCardBackgroundColor(Color.parseColor(Utills.getValidHex(item.shadeCodeHex)))
            holder.colorSelectCard.cardElevation = activity.resources.getDimension(R.dimen.elevation_home_card)
            holder.colorFamilyCard.cardElevation = 0F
        } else {
            holder.colorSelectCard.setCardBackgroundColor(Color.TRANSPARENT)
            holder.colorSelectCard.cardElevation = 0F
            holder.colorFamilyCard.cardElevation = activity.resources.getDimension(R.dimen.elevation_home_card)
        }
    }
}