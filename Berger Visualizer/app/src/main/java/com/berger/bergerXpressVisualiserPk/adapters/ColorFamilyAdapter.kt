package com.berger.bergerXpressVisualiserPk.adapters

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.berger.bergerXpressVisualiserPk.R
import com.berger.bergerXpressVisualiserPk.activities.ProductDetailsActivity
import com.berger.bergerXpressVisualiserPk.models.Colors
import com.berger.bergerXpressVisualiserPk.utill.Utills
import com.berger.bergerXpressVisualiserPk.viewHolders.ColorFamilyListViewHolder

class ColorFamilyAdapter  (val activity: Activity, private val colors: List<Colors>) : androidx.recyclerview.widget.RecyclerView.Adapter<ColorFamilyListViewHolder>() {

    var selectedColor = -1

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ColorFamilyListViewHolder {
        return ColorFamilyListViewHolder(LayoutInflater.from(activity).inflate(R.layout.single_color_family_item, p0, false))
    }

    override fun getItemCount(): Int {
        return colors.size
    }

    override fun onBindViewHolder(holder: ColorFamilyListViewHolder, p1: Int) {

        val item : Colors = colors[p1]

        holder.colorDisplay.setBackgroundColor(Color.parseColor(Utills.getValidHex(item.colorCodeHex)))
//        holder.colorDisplay.setColorFilter(Color.parseColor(item.colorCodeHex))

        if(p1 == selectedColor){
            holder.colorSelectCard.setCardBackgroundColor(Color.parseColor(Utills.getValidHex(item.colorCodeHex)))
            holder.colorSelectCard.cardElevation = activity.resources.getDimension(R.dimen.elevation_home_card)
            holder.colorFamilyCard.cardElevation = 0F
        } else {
            holder.colorSelectCard.setCardBackgroundColor(Color.TRANSPARENT)
            holder.colorSelectCard.cardElevation = 0F
            holder.colorFamilyCard.cardElevation = activity.resources.getDimension(R.dimen.elevation_home_card)
        }

        holder.colorDisplay.setOnClickListener {
            if(activity is ProductDetailsActivity) {
                activity.selectColor(item)
            }
            if(selectedColor == p1) {
                selectedColor = -1
            } else {
                selectedColor = p1
            }

            notifyDataSetChanged()
        }
    }
}