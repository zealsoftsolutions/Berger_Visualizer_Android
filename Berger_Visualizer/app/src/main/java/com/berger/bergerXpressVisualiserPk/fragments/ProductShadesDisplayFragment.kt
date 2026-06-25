package com.berger.bergerXpressVisualiserPk.fragments

import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.berger.bergerXpressVisualiserPk.R
import com.berger.bergerXpressVisualiserPk.adapters.SecondaryColorsListAdapter
import com.berger.bergerXpressVisualiserPk.models.Product
import com.berger.bergerXpressVisualiserPk.utill.Utills
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.MediaStoreSignature
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"

/**
 * A simple [Fragment] subclass.
 * Use the [ProductShadesDisplayFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProductShadesDisplayFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var product: Product? = null
    private var performSelect = false

    private lateinit var productCard: CardView
    private lateinit var productItem: ConstraintLayout
    private lateinit var productImage: ImageView
    private lateinit var productName: TextView
    private lateinit var productNameBg: View

    //    private lateinit var shadesCard: CardView
    private lateinit var shadesItem: ConstraintLayout
    private lateinit var shadesRecycler: RecyclerView
    private lateinit var shadesAdapter: SecondaryColorsListAdapter

    private var mLastClickTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            product = it.getSerializable(ARG_PARAM2) as Product
            performSelect = it.getBoolean(ARG_PARAM3)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_product_shades_display, container, false)

        setViews(view)
        return view
    }

    private fun setViews(view: View){

        productCard = view.findViewById(R.id.product_card)
        productItem = view.findViewById(R.id.product_item)
        productImage = view.findViewById(R.id.product_image)
        productName = view.findViewById(R.id.product_label)
        productNameBg = view.findViewById(R.id.label_background)

//        shadesCard = view.findViewById(R.id.shades_card)
        shadesItem = view.findViewById(R.id.shades_item)
        shadesRecycler = view.findViewById(R.id.shades_recycler)

        populateDetails()
    }

    private fun populateDetails(){
        if(product != null){

            if(!product?.productName.isNullOrBlank()) {
                productName.text = product?.productName
                productCard.visibility = View.VISIBLE
            } else{
                productCard.visibility = View.GONE
            }

            Glide.with(this)
                .load(product?.localImagePath ?: Utills.getCompleteUrl(product?.productImage))
                .placeholder(R.drawable.place_holder_image)
                .error(R.drawable.place_holder_image)
                .fitCenter()
                .into(productImage)

//            if(!product?.savedImageUrl.isNullOrEmpty()) {
//                productImage.setImageBitmap(Utills.stringToBitmap(product?.savedImageUrl))
//            } else if(product?.productImage != null && context != null){
//                Glide
//                    .with(requireContext())
//                    .load(Utills.getCompleteUrl(product?.productImage))
//                    .listener(object : RequestListener<Drawable> {
//                        override fun onLoadFailed(
//                            e: GlideException?,
//                            model: Any?,
//                            target: Target<Drawable>,
//                            isFirstResource: Boolean
//                        ): Boolean {
//                            return false
//                        }
//
//                        override fun onResourceReady(
//                            resource: Drawable,
//                            model: Any,
//                            target: Target<Drawable>?,
//                            dataSource: DataSource,
//                            isFirstResource: Boolean
//                        ): Boolean {
//                            return false
//                        }
//
//                    })
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .placeholder(R.drawable.berger_logo)
//                    .signature(MediaStoreSignature("", Calendar.getInstance().time.time, 0))
//                    .into(productImage)
//            }

            populateColors()
        }
    }

    fun selectPosition(){

    }

    private fun populateColors(){
        if (product?.colors == null || product?.colors?.size == 0 || product?.colors?.get(0)?.shades == null) {

        } else {
            shadesRecycler.visibility = View.VISIBLE
//            shadesRecycler.layoutManager = CircleLayoutManager(context)
//            shadesRecycler.layoutManager = CircleLayoutManager.Builder(context)
//                .setAngleInterval(20)
//                .setMaxRemoveAngle(20F)
//                .setMinRemoveAngle(10F)
//                .setMoveSpeed(2)
//                .setRadius(10)
//                .build()
//            shadesRecycler.layoutManager = WheelLayoutManager(requireContext(), 5)
            shadesRecycler.layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 3)
            shadesAdapter = SecondaryColorsListAdapter(context as Activity, product?.colors?.get(0)?.shades!!, performSelect, true)
            shadesRecycler.adapter = shadesAdapter
            shadesRecycler.invalidate()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProductShadesDisplayFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic fun newInstance(param1: String, param2: Product, param3: Boolean) =
                ProductShadesDisplayFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putSerializable(ARG_PARAM2, param2)
                        putBoolean(ARG_PARAM3, param3)
                    }
                }
    }
}