package com.berger.bergerXpressVisualiserPk.viewHolders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.berger.bergerXpressVisualiserPk.R

class FeaturesListViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView)  {

    var featureIcon: ImageView = itemView.findViewById(R.id.check_icon)
    var feature: TextView = itemView.findViewById(R.id.feature)
}