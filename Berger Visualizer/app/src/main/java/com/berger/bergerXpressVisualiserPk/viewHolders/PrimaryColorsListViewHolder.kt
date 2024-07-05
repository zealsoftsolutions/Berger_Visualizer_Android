package com.berger.bergerXpressVisualiserPk.viewHolders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.berger.bergerXpressVisualiserPk.R

class PrimaryColorsListViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView)  {

    var colorCard: CardView = itemView.findViewById(R.id.color_card)
    var colorItem: ConstraintLayout = itemView.findViewById(R.id.color_item)
    var colorImage: ImageView = itemView.findViewById(R.id.color_image)
    var colorLabelBackground: View = itemView.findViewById(R.id.label_background)
    var colorLabel: TextView = itemView.findViewById(R.id.color_label)
    var colorDescription: TextView = itemView.findViewById(R.id.color_description)
}