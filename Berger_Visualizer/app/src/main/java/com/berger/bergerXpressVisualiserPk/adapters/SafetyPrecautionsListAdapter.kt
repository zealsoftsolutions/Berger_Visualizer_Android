package com.berger.bergerXpressVisualiserPk.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.berger.bergerXpressVisualiserPk.R
import com.berger.bergerXpressVisualiserPk.viewHolders.SafetyPrecautionsListViewHolder

class SafetyPrecautionsListAdapter (val activity: Activity, private val precautions: List<String>) : androidx.recyclerview.widget.RecyclerView.Adapter<SafetyPrecautionsListViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SafetyPrecautionsListViewHolder {
        return SafetyPrecautionsListViewHolder(LayoutInflater.from(activity).inflate(R.layout.single_safety_precaution_list_item, p0, false))
    }

    override fun getItemCount(): Int {
        return precautions.size
    }

    override fun onBindViewHolder(holder: SafetyPrecautionsListViewHolder, p1: Int) {

        val item : String = precautions[p1]

        if(item != null && item.isNotEmpty()){
            holder.precaution.text = "• " + item
        }
    }
}