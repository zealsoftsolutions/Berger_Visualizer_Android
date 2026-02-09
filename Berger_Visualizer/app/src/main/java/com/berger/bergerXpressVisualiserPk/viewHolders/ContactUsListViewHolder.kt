package com.berger.bergerXpressVisualiserPk.viewHolders

import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.berger.bergerXpressVisualiserPk.R

class ContactUsListViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView)  {

    var contactUsCard: CardView = itemView.findViewById(R.id.contact_us_card)
    var contactUsItem: ConstraintLayout = itemView.findViewById(R.id.contact_us_item)
    var nameLabel: TextView = itemView.findViewById(R.id.name_label)
    var name: TextView = itemView.findViewById(R.id.name)
    var addressLabel: TextView = itemView.findViewById(R.id.address_label)
    var address: TextView = itemView.findViewById(R.id.address)
    var phoneLabel: TextView = itemView.findViewById(R.id.phone_label)
    var phone: RecyclerView = itemView.findViewById(R.id.phone)
    var faxLabel: TextView = itemView.findViewById(R.id.fax_label)
    var fax: TextView = itemView.findViewById(R.id.fax)
    var telecardLabel: TextView = itemView.findViewById(R.id.telecard_label)
    var telecard: TextView = itemView.findViewById(R.id.telecard)
}