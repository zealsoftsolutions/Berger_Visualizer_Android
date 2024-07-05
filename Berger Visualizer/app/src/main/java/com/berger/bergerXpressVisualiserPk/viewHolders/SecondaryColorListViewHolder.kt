package com.berger.bergerXpressVisualiserPk.viewHolders

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.berger.bergerXpressVisualiserPk.R

class SecondaryColorListViewHolder  (itemView: View) : RecyclerView.ViewHolder(itemView)  {

    var shadeCard: CardView = itemView.findViewById(R.id.shade_card)
    var shadeItem: ConstraintLayout = itemView.findViewById(R.id.shade_item)
    var shade: View = itemView.findViewById(R.id.shade)
    var shadeLabel: TextView = itemView.findViewById(R.id.shade_label)
    var openVisualizer: ImageButton = itemView.findViewById(R.id.open_visualizer)
}