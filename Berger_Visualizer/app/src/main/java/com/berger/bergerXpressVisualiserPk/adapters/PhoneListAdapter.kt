package com.berger.bergerXpressVisualiserPk.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.berger.bergerXpressVisualiserPk.R
import com.berger.bergerXpressVisualiserPk.utill.Utills
import com.berger.bergerXpressVisualiserPk.viewHolders.PhoneListViewHolder

class PhoneListAdapter (val activity: Activity, private val numbers: List<String>) : androidx.recyclerview.widget.RecyclerView.Adapter<PhoneListViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): PhoneListViewHolder {
        return PhoneListViewHolder(LayoutInflater.from(activity).inflate(R.layout.single_phone_list_item, p0, false))
    }

    override fun getItemCount(): Int {
        return numbers.size
    }

    override fun onBindViewHolder(holder: PhoneListViewHolder, p1: Int) {

        val item : String = numbers[p1]

        if(item != null && item.isNotEmpty()){
            holder.number.text = item

            holder.number.setOnClickListener {
                Utills.makeCall(activity, item)
            }
        }
    }
}