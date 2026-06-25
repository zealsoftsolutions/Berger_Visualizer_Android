package com.berger.bergerXpressVisualiserPk.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.berger.bergerXpressVisualiserPk.R
import com.berger.bergerXpressVisualiserPk.viewHolders.FeaturesListViewHolder

class FeaturesListAdapter (val activity: Activity, private val features: List<String>) : androidx.recyclerview.widget.RecyclerView.Adapter<FeaturesListViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): FeaturesListViewHolder {
        return FeaturesListViewHolder(LayoutInflater.from(activity).inflate(R.layout.single_feature_list_item, p0, false))
    }

    override fun getItemCount(): Int {
        return features.size
    }

    override fun onBindViewHolder(holder: FeaturesListViewHolder, p1: Int) {

        val item : String = features[p1]

        if(item != null){
            holder.feature.text = item
        }
    }
}