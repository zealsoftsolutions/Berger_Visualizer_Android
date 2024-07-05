package com.berger.bergerXpressVisualiserPk.viewHolders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.berger.bergerXpressVisualiserPk.R

class SideMenuListViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView)  {

    var menuItem: ConstraintLayout = itemView.findViewById(R.id.menu_item_view)
    var icon: ImageView = itemView.findViewById(R.id.menu_item_icon)
    var label: TextView = itemView.findViewById(R.id.menu_item_label)
}