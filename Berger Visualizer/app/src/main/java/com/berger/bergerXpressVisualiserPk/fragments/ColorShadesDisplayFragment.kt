package com.berger.bergerXpressVisualiserPk.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.SystemClock
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.berger.bergerXpressVisualiserPk.R
import com.berger.bergerXpressVisualiserPk.activities.SourceSelectActivity
import com.berger.bergerXpressVisualiserPk.adapters.SecondaryColorsListAdapter
import com.berger.bergerXpressVisualiserPk.models.Colors
import com.berger.bergerXpressVisualiserPk.utill.Utills
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.signature.MediaStoreSignature
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"

/**
 * A simple [Fragment] subclass.
 * Use the [ColorShadesDisplayFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ColorShadesDisplayFragment : Fragment(), View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var colors: Colors? = null
    private var performSelect = false

    private lateinit var colorCard: CardView
    private lateinit var colorItem: ConstraintLayout
    private lateinit var color: ImageView
    private lateinit var colorName: TextView
    private lateinit var colorNameBg: View

//    private lateinit var shadesCard: CardView
    private lateinit var shadesItem: ConstraintLayout
    private lateinit var shadesRecycler: RecyclerView
    private lateinit var shadesAdapter: SecondaryColorsListAdapter
    private lateinit var visualize: Button

    private var mLastClickTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            colors = it.getSerializable(ARG_PARAM2) as Colors
            performSelect = it.getBoolean(ARG_PARAM3)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_color_shades_display, container, false)

        setViews(view)
        return view
    }

    private fun setViews(view: View){

        colorCard = view.findViewById(R.id.color_card)
        colorItem = view.findViewById(R.id.color_item)
        color = view.findViewById(R.id.color_image)
        colorName = view.findViewById(R.id.color_label)
        colorNameBg = view.findViewById(R.id.label_background)

//        shadesCard = view.findViewById(R.id.shades_card)
        shadesItem = view.findViewById(R.id.shades_item)
        shadesRecycler = view.findViewById(R.id.shades_recycler)
        visualize = view.findViewById(R.id.visualize)
        visualize.setOnClickListener(this)

        populateDetails()
    }

    private fun populateDetails(){
        if(colors != null){

            colorCard.setCardBackgroundColor(Color.parseColor(Utills.getValidHex(colors?.colorCodeHex)))

            if(!colors?.colorName.isNullOrBlank()) {
                colorName.text = colors?.colorName
                colorCard.visibility = View.VISIBLE
            } else{
                colorCard.visibility = View.GONE
            }

            if(colors?.colorImageUrl != null && context != null){
                Glide
                    .with(requireContext())
                    .load(colors?.colorImageUrl)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            return false
                        }
                    })
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.berger_logo)
                    .signature(MediaStoreSignature("", Calendar.getInstance().time.time, 0))
                    .into(color)
            }

            if(colors?.labelColorHex != null && colors?.labelColorHex!!.isNotEmpty()) {

                try {
                    colorName.setTextColor(Color.parseColor(colors?.labelColorHex))
                } catch (e: Exception) { }
            }

            if(colors?.colorCodeHex != null && colors?.colorCodeHex!!.isNotEmpty()){
                try {
                    colorNameBg.setBackgroundColor(Color.parseColor(colors?.colorCodeHex))
                    if(colors?.colorCodeHex == "#FFFFFF") {
                        colorName.setTextColor(Color.parseColor("#000000"))
                    }
                } catch (e: Exception) { }
            }

            populateColors()
        }
    }

    private fun populateColors(){
        if (colors?.shades == null || colors?.shades?.size == 0) {

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
            shadesAdapter = SecondaryColorsListAdapter(context as Activity, colors?.shades!!, performSelect, true)
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
         * @return A new instance of fragment ColorShadesDisplayFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: Colors, param3: Boolean) =
            ColorShadesDisplayFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putSerializable(ARG_PARAM2, param2)
                    putBoolean(ARG_PARAM3, param3)
                }
            }
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            else -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                performClick(v)
            }
        }
    }

    private fun performClick(v: View?) {
        when (v?.id) {
            R.id.visualize -> {
                startActivity(Intent(context, SourceSelectActivity::class.java))
            }
        }
    }
}