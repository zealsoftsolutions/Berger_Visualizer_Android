package com.berger.bergerXpressVisualiserPk.adapters

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.berger.bergerXpressVisualiserPk.R
import com.berger.bergerXpressVisualiserPk.activities.MyIdeaDetailsActivity
import com.berger.bergerXpressVisualiserPk.activities.MyIdeasActivity
import com.berger.bergerXpressVisualiserPk.models.Idea
import com.berger.bergerXpressVisualiserPk.utill.Constants
import com.berger.bergerXpressVisualiserPk.viewHolders.AlbumListViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.signature.MediaStoreSignature
import java.util.*

class MyIdeasListAdapter (val activity: Activity, private val images: List<Idea>) : androidx.recyclerview.widget.RecyclerView.Adapter<AlbumListViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AlbumListViewHolder {
        return AlbumListViewHolder(LayoutInflater.from(activity).inflate(R.layout.single_album_list_item, p0, false))
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: AlbumListViewHolder, p1: Int) {

        val item : Idea = images[p1]

        holder.deleteImage.visibility = View.VISIBLE
        holder.paintImage.visibility = View.VISIBLE

        if(item != null) {
            Glide
                .with(activity)
                .load(item.imageUrl)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: DataSource?, isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.place_holder_image)
                .signature(MediaStoreSignature("", Calendar.getInstance().time.time, 0))
                .into(holder.albumImage)


            if(item.shadesUsed != null && item.shadesUsed?.size!! > 0){
                holder.shadesRecycler.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                var colorsAdapter = ShadesUsedListAdapter(activity, item.shadesUsed!!)
                holder.shadesRecycler.adapter = colorsAdapter
                holder.shadesRecycler.invalidate()
                holder.shadesRecycler.visibility = View.VISIBLE
            } else {
                holder.shadesRecycler.visibility = View.GONE
            }

            holder.albumCard.setOnClickListener {
                val details = Intent(activity, MyIdeaDetailsActivity::class.java)
                details.putExtra(Constants.INTENT_MY_IDEA, item.imageUrl)
                if(item.shadesUsed != null && item.shadesUsed!!.size > 0)
                    details.putExtra(Constants.INTENT_MY_IDEA_SHADES, item.shadesUsed)
                Constants.points = item.maskingTapes
                details.putExtra(Constants.INTENT_IDEA_INDEX, p1)
                activity.startActivity(details)

//                val viewImage = Intent(activity, ViewImageActivity::class.java)
//                viewImage.putExtra("imageUrl", item.imageUrl)
//                viewImage.putExtra("type", Constants.TYPE_MY_IDEAS)
//                activity.startActivity(viewImage)
            }

            holder.deleteImage.setOnClickListener {
                if (activity is MyIdeasActivity)
                    activity.showDeleteDialog(p1)
            }

            holder.paintImage.setOnClickListener {
                if (activity is MyIdeasActivity)
                    activity.startPainting(item)
            }
        }
    }
}