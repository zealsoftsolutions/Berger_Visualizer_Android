package com.berger.bergerXpressVisualiserPk.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import com.berger.bergerXpressVisualiserPk.R
import com.berger.bergerXpressVisualiserPk.activities.ViewImageActivity
import com.berger.bergerXpressVisualiserPk.models.Idea
import com.berger.bergerXpressVisualiserPk.utill.Constants
import com.berger.bergerXpressVisualiserPk.utill.Utills
import com.berger.bergerXpressVisualiserPk.viewHolders.AlbumListViewHolder
import com.bumptech.glide.Glide

class InspirationalIdeasAdapter  (val activity: Activity, private val images: List<Idea>) : androidx.recyclerview.widget.RecyclerView.Adapter<AlbumListViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AlbumListViewHolder {
//        if(p1%2 == 1){
            return AlbumListViewHolder(LayoutInflater.from(activity).inflate(R.layout.single_album_list_item, p0, false))
//        } else {
//            return AlbumListViewHolder(LayoutInflater.from(activity).inflate(R.layout.single_album_list_item_flat, p0, false))
//        }
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: AlbumListViewHolder, p1: Int) {

        val item : Idea = images[p1]

        if(item != null){

            Glide.with(holder.itemView)
                .load(item.localImageUrl ?: Utills.getCompleteUrl(item.imageUrl))
                .placeholder(R.drawable.place_holder_image)
                .error(R.drawable.place_holder_image)
                .fitCenter()
                .into(holder.albumImage)

//            if(!item.savedImageUrl.isNullOrEmpty()) {
//                holder.albumImage.setImageBitmap(Utills.stringToBitmap(item.savedImageUrl))
////                Glide
////                    .with(activity)
////                    .load(item.savedImageUrl)
////                    .diskCacheStrategy(DiskCacheStrategy.NONE)
////                    .placeholder(R.drawable.place_holder_image)
////                    .signature(MediaStoreSignature("", Calendar.getInstance().time.time, 0))
////                    .into(holder.albumImage)
//            } else if(!item.imageUrl.isNullOrEmpty()) {
//                Glide
//                    .with(activity)
//                    .asBitmap()
//                    .load(Utills.getCompleteUrl(item.imageUrl))
//                    .apply(
//                        RequestOptions()
//                            .fitCenter()
//                            .format(DecodeFormat.PREFER_ARGB_8888)
//                            .override(Target.SIZE_ORIGINAL)
//                    )
//                    .listener(object : RequestListener<Bitmap> {
//
//                        override fun onLoadFailed(
//                            e: GlideException?,
//                            model: Any?,
//                            target: Target<Bitmap>,
//                            isFirstResource: Boolean
//                        ): Boolean {
//                            return false
//                        }
//
//                        override fun onResourceReady(
//                            resource: Bitmap,
//                            model: Any,
//                            target: Target<Bitmap>?,
//                            dataSource: DataSource,
//                            isFirstResource: Boolean
//                        ): Boolean {
//                            images[p1].savedImageUrl = Utills.bitmapToString(resource)
////                            images[p1].savedImageUrl = Utills.saveInspirationalIdeasOnDeviceGetUrl(resource)
//                            Utills.updateInspirationalIdeasImagesDatabase(activity, images[p1].savedImageUrl, p1)
//                            return false
//                        }
//                    })
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .placeholder(R.drawable.place_holder_image)
//                    .signature(MediaStoreSignature("", Calendar.getInstance().time.time, 0))
//                    .into(holder.albumImage)
//            }
        }

        holder.albumCard.setOnClickListener {
            val viewImage = Intent(activity, ViewImageActivity::class.java)
            if(!item.savedImageUrl.isNullOrEmpty()) {
                Constants.IMAGE_BITMAP = item.savedImageUrl
                viewImage.putExtra(Constants.INTENT_IMAGE_BITMAP, true)
            } else if(!item.imageUrl.isNullOrEmpty()){
                viewImage.putExtra(Constants.INTENT_IMAGE_URL, item.imageUrl)
            }
            viewImage.putExtra(Constants.INTENT_URL_TYPE, Constants.TYPE_INSPIRATIONAL_IDEAS)
            activity.startActivity(viewImage)
        }
    }
}