package com.berger.bergerXpressVisualiserPk.viewHolders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.berger.bergerXpressVisualiserPk.R

class ProductsListViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView)  {

    var productCard: CardView = itemView.findViewById(R.id.product_card)
    var productImage: ImageView = itemView.findViewById(R.id.product_image)
    var productItem: ConstraintLayout = itemView.findViewById(R.id.product_item)
    var productName: TextView = itemView.findViewById(R.id.product_name)
}