package com.berger.bergerXpressVisualiserPk.viewHolders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.berger.bergerXpressVisualiserPk.R

class PaintResultStepListViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView)  {

    var outputName: TextView = itemView.findViewById(R.id.output_name)
    var image: ImageView = itemView.findViewById(R.id.result_image)
}