package com.berger.bergerXpressVisualiserPk.viewHolders

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.berger.bergerXpressVisualiserPk.R

class AlbumListViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView)  {

    var albumCard: CardView = itemView.findViewById(R.id.album_card)
    var albumItem: ConstraintLayout = itemView.findViewById(R.id.album_item)
    var albumImage: ImageView = itemView.findViewById(R.id.album_image)
    var deleteImage: ImageButton = itemView.findViewById(R.id.delete_image)
    var paintImage: ImageButton = itemView.findViewById(R.id.paint_image)
    var shadesRecycler: RecyclerView = itemView.findViewById(R.id.shades_recycler)
}