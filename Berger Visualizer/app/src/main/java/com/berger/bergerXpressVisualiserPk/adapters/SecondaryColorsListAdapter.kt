package com.berger.bergerXpressVisualiserPk.adapters

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.berger.bergerXpressVisualiserPk.R
import com.berger.bergerXpressVisualiserPk.activities.SourceSelectActivity
import com.berger.bergerXpressVisualiserPk.models.Shade
import com.berger.bergerXpressVisualiserPk.utill.Constants
import com.berger.bergerXpressVisualiserPk.viewHolders.SecondaryColorListViewHolder
import java.lang.Exception

class SecondaryColorsListAdapter (val activity: Activity, private val shadesList: List<Shade>,
                                  private var performSelect: Boolean, private var showVisualizerOption: Boolean) : androidx.recyclerview.widget.RecyclerView.Adapter<SecondaryColorListViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SecondaryColorListViewHolder {
        return SecondaryColorListViewHolder(LayoutInflater.from(activity).inflate(R.layout.single_color_shade_view_item, p0, false))
    }

    override fun getItemCount(): Int {
        return shadesList.size
    }

    override fun onBindViewHolder(holder: SecondaryColorListViewHolder, p1: Int) {

        val item : Shade = shadesList[p1]

        try {
            holder.shade.setBackgroundColor(Color.parseColor(item.shadeCodeHex))
        } catch (e: Exception){ }

        var name = ""

        if(item.shadeName != null)
            name = item.shadeName!!
        if(item.shadeCode != null)
            name += " " + item.shadeCode

        holder.shadeLabel.text = name
        holder.shadeLabel.isSelected = true

        if(showVisualizerOption && !performSelect){
            holder.openVisualizer.visibility = View.VISIBLE
            holder.openVisualizer.setOnClickListener{
                Constants.SELECTED_SHADE = item
                Constants.SELECTED_POSITION = -1
                Constants.SELECTED_PRODUCT_ID = null
                activity.startActivity(Intent(activity, SourceSelectActivity::class.java))
            }
        } else {
            holder.openVisualizer.visibility = View.GONE
        }

        holder.shadeCard.setOnClickListener{
            if(performSelect){
                Constants.SELECTED_SHADE = item
                activity.finish()
            }
        }
    }
}