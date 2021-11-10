package com.berger.bergerXpressVisualiserPk.adapters

import android.app.Activity
import android.util.DisplayMetrics
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.berger.bergerXpressVisualiserPk.R
import com.berger.bergerXpressVisualiserPk.models.Contact
import com.berger.bergerXpressVisualiserPk.utill.Utills
import com.berger.bergerXpressVisualiserPk.viewHolders.ContactUsListViewHolder

class ContactUsListAdapter (val activity: Activity, private val contactsList: List<Contact>) : androidx.recyclerview.widget.RecyclerView.Adapter<ContactUsListViewHolder>() {

    val display: Display = activity.windowManager.defaultDisplay
    val outMetrics = DisplayMetrics()


    var density = activity.resources.displayMetrics.density
    var dpWidth = outMetrics.widthPixels / density

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ContactUsListViewHolder {
        display.getMetrics(outMetrics)
        density = activity.resources.displayMetrics.density
        dpWidth = outMetrics.widthPixels / density
        return ContactUsListViewHolder(LayoutInflater.from(activity).inflate(R.layout.single_contact_us_list_item, p0, false))
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }

    override fun onBindViewHolder(holder: ContactUsListViewHolder, p1: Int) {

        val item : Contact = contactsList[p1]

        if(item != null) {

            if(!item.name.isNullOrEmpty()){
                holder.name.text = item.name
                holder.name.visibility = View.VISIBLE
            } else {
                holder.nameLabel.visibility = View.GONE
                holder.name.visibility = View.GONE
            }

            if(!item.address.isNullOrEmpty()){
                holder.address.text = item.address
                holder.addressLabel.visibility = View.VISIBLE
                holder.address.visibility = View.VISIBLE
            } else {
                holder.addressLabel.visibility = View.GONE
                holder.address.visibility = View.GONE
            }

            if(item.phone != null && item.phone?.size!! > 0){

                var width = dpWidth/5

                var span = 2

                if(width > 100){
                    span = 3
                } else {
                    span = 2
                }


                holder.phone.layoutManager = androidx.recyclerview.widget.GridLayoutManager(activity, span)
                var phoneListAdapter = PhoneListAdapter(activity, item.phone!!)
                holder.phone.adapter = phoneListAdapter
                holder.phone.invalidate()

                holder.phoneLabel.visibility = View.VISIBLE
                holder.phone.visibility = View.VISIBLE
            } else {
                holder.phoneLabel.visibility = View.GONE
                holder.phone.visibility = View.GONE
            }

            if(!item.fax.isNullOrEmpty()){
                holder.fax.text = item.fax
                holder.faxLabel.visibility = View.VISIBLE
                holder.fax.visibility = View.VISIBLE
            } else {
                holder.faxLabel.visibility = View.GONE
                holder.fax.visibility = View.GONE
            }

            if(!item.telecard.isNullOrEmpty()){
                holder.telecard.text = item.telecard
                holder.telecardLabel.visibility = View.VISIBLE
                holder.telecard.visibility = View.VISIBLE
            } else {
                holder.telecardLabel.visibility = View.GONE
                holder.telecard.visibility = View.GONE
            }

            holder.address.setOnClickListener {
                if(item.lat != null && item.lon != null){
                    Utills.getDirections(activity, item.lat, item.lon)
                }
            }
        }
    }
}