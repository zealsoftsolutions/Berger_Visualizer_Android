package com.berger.bergerXpressVisualiserPk.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.berger.bergerXpressVisualiserPk.R
import com.berger.bergerXpressVisualiserPk.activities.HomeActivity
import com.berger.bergerXpressVisualiserPk.models.SideMenuItem
import com.berger.bergerXpressVisualiserPk.viewHolders.SideMenuListViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.MediaStoreSignature
import java.util.*

class SideMenuListAdapter (val activity: Activity, private val sideMenuList: List<SideMenuItem>) : androidx.recyclerview.widget.RecyclerView.Adapter<SideMenuListViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SideMenuListViewHolder {
        return SideMenuListViewHolder(LayoutInflater.from(activity).inflate(R.layout.single_side_menu_list_item, p0, false))
    }

    override fun getItemCount(): Int {
        return sideMenuList.size
    }

    override fun onBindViewHolder(holder: SideMenuListViewHolder, p1: Int) {

        val item : SideMenuItem = sideMenuList[p1]

        if(item != null){
            if(item.itemIcon != null){
                Glide
                        .with(activity)
                        .load(item.itemIcon)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .placeholder(R.drawable.berger_logo)
                        .signature(MediaStoreSignature("", Calendar.getInstance().time.time, 0))
                        .into(holder.icon)
            }

            if(item.itemLabel != null){
                holder.label.text = item.itemLabel
            }

        }

        holder.menuItem.setOnClickListener {

            if(item.itemLabel != null && activity is HomeActivity){
                when(item.itemLabel){
                    activity.resources.getString(R.string.side_menu_visualizer) -> activity.openVisualizer()
                    activity.resources.getString(R.string.side_menu_colors) -> activity.openColors()
                    activity.resources.getString(R.string.side_menu_products) -> activity.openProducts()
                    activity.resources.getString(R.string.side_menu_calculator) -> activity.openCalculator()
                    activity.resources.getString(R.string.side_menu_inspirational) -> activity.openInspirationalIdeas()
                    activity.resources.getString(R.string.side_menu_album) -> activity.openMyIdeas()
                    activity.resources.getString(R.string.side_menu_dealer) -> activity.openDealer()

                    activity.resources.getString(R.string.side_menu_about_us) -> activity.openAboutUs()
                    activity.resources.getString(R.string.side_menu_contact_us) -> activity.openContactUs()
                }
                activity.closeDrawer()
            }
        }
    }
}