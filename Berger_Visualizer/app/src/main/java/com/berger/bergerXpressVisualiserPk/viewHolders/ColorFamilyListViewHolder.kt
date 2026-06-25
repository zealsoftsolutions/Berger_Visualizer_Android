package com.berger.bergerXpressVisualiserPk.viewHolders

import android.view.View
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.berger.bergerXpressVisualiserPk.R

class ColorFamilyListViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView)  {

    var colorSelectCard: CardView = itemView.findViewById(R.id.color_select_card)
    var colorFamilyCard: CardView = itemView.findViewById(R.id.color_family_card)
    var colorDisplay: View = itemView.findViewById(R.id.color_family_display)
}