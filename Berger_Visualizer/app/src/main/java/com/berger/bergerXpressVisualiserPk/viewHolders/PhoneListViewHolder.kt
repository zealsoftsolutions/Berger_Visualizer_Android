package com.berger.bergerXpressVisualiserPk.viewHolders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.berger.bergerXpressVisualiserPk.R

class PhoneListViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView)  {

    var number: TextView = itemView.findViewById(R.id.phone)
}