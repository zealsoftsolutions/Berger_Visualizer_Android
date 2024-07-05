package com.berger.bergerXpressVisualiserPk.adapters

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import com.berger.bergerXpressVisualiserPk.R
import com.berger.bergerXpressVisualiserPk.activities.ProductDetailsActivity
import com.berger.bergerXpressVisualiserPk.activities.ProductsActivity
import com.berger.bergerXpressVisualiserPk.models.Product
import com.berger.bergerXpressVisualiserPk.utill.Constants
import com.berger.bergerXpressVisualiserPk.utill.Utills
import com.berger.bergerXpressVisualiserPk.viewHolders.ProductsListViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.MediaStoreSignature
import java.util.*


class ProductsListAdapter  (val activity: Activity, private val products: List<Product>, private val performSelect: Boolean) : androidx.recyclerview.widget.RecyclerView.Adapter<ProductsListViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ProductsListViewHolder {
        return ProductsListViewHolder(LayoutInflater.from(activity).inflate(R.layout.single_product_list_item, p0, false))
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ProductsListViewHolder, p1: Int) {

        val item : Product = products[p1]

//        if(item.productColorCodeHex != null)
//            holder.productCard.setCardBackgroundColor(Color.parseColor(item.productColorCodeHex))

        if(!item.savedImageUrl.isNullOrEmpty()) {
            holder.productImage.setImageBitmap(Utills.stringToBitmap(item.savedImageUrl))
//            Glide
//                .with(activity)
//                .load(item.savedImageUrl)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .placeholder(R.drawable.place_holder_image)
//                .signature(MediaStoreSignature("", Calendar.getInstance().time.time, 0))
//                .into(holder.productImage)
        } else if(!item.productImage.isNullOrEmpty()){
            Glide
                .with(activity)
                .asBitmap()
                .load(Utills.getCompleteUrl(item.productImage))
                .apply(
                    RequestOptions()
                        .fitCenter()
                        .format(DecodeFormat.PREFER_ARGB_8888)
                        .override(Target.SIZE_ORIGINAL)
                )
                .listener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onResourceReady(resource: Bitmap?, model: Any?, target: com.bumptech.glide.request.target.Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        products[p1].savedImageUrl = Utills.bitmapToString(resource)
//                        products[p1].savedImageUrl = Utills.saveProductImagesOnDeviceGetUrl(resource, item.productName)
                        Utills.updateProductImagesDatabase(activity, products[p1].savedImageUrl, p1)
                        return false
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.place_holder_image)
                .signature(MediaStoreSignature("", Calendar.getInstance().time.time, 0))
                .into(holder.productImage)
        }

        holder.productName.text = item.productName

        holder.productCard.setOnClickListener {
            if(performSelect){
                if(activity is ProductsActivity)
                    activity.performSelect(item)
            } else {
                val intent = Intent(activity, ProductDetailsActivity::class.java)
                intent.putExtra(Constants.INTENT_PRODUCT, item)
                activity.startActivity(intent)
            }
        }
    }
}