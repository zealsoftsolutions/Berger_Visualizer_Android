package com.berger.bergerXpressVisualiserPk.adapters

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.berger.bergerXpressVisualiserPk.R
import com.berger.bergerXpressVisualiserPk.models.Shade
import com.berger.bergerXpressVisualiserPk.viewHolders.ColorShadeProductListViewHolder

class ColorShadeProductListAdapter (val activity: Activity, private val shades: List<Shade>) : androidx.recyclerview.widget.RecyclerView.Adapter<ColorShadeProductListViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ColorShadeProductListViewHolder {
        return ColorShadeProductListViewHolder(LayoutInflater.from(activity).inflate(R.layout.single_shade_view_product_list_item, p0, false))
    }

    override fun getItemCount(): Int {
        return shades.size
    }

    override fun onBindViewHolder(holder: ColorShadeProductListViewHolder, p1: Int) {

        val item : Shade = shades[p1]

        holder.shade.setBackgroundColor(Color.parseColor(item.shadeCodeHex))
        holder.shadeLabel.text = item.shadeName
        holder.shadeLabel.isSelected = true
    }
}