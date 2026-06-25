package com.berger.bergerXpressVisualiserPk.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.berger.bergerXpressVisualiserPk.R
import com.berger.bergerXpressVisualiserPk.viewHolders.SizesListViewHolder

class SurfacesListAdapter (val activity: Activity, private val surfaces: List<String>) : androidx.recyclerview.widget.RecyclerView.Adapter<SizesListViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SizesListViewHolder {
        return SizesListViewHolder(LayoutInflater.from(activity).inflate(R.layout.single_sizes_list_item, p0, false))
    }

    override fun getItemCount(): Int {
        return surfaces.size
    }

    override fun onBindViewHolder(holder: SizesListViewHolder, p1: Int) {

        val item : String = surfaces[p1]

        if(item != null){
            holder.size.text = item
        }
    }
}