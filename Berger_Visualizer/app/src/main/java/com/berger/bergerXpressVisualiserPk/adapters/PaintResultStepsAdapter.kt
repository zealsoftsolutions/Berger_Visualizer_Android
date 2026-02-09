package com.berger.bergerXpressVisualiserPk.adapters

import android.app.Activity
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import com.berger.bergerXpressVisualiserPk.R
import com.berger.bergerXpressVisualiserPk.models.PaintStep
import com.berger.bergerXpressVisualiserPk.viewHolders.PaintResultStepListViewHolder
import org.opencv.android.Utils
import java.lang.Exception

class PaintResultStepsAdapter (val activity: Activity, private val timingsList: List<PaintStep>) : androidx.recyclerview.widget.RecyclerView.Adapter<PaintResultStepListViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): PaintResultStepListViewHolder {
        return PaintResultStepListViewHolder(LayoutInflater.from(activity).inflate(R.layout.single_paint_result_step_list_item, p0, false))
    }

    override fun getItemCount(): Int {
        return timingsList.size
    }

    override fun onBindViewHolder(holder: PaintResultStepListViewHolder, p1: Int) {

        val item : PaintStep = timingsList[p1]

        if(item != null) {
            try {
                val mBitmap = Bitmap.createBitmap(item.image!!.cols(), item.image!!.rows(), Bitmap.Config.ARGB_8888)
                Utils.matToBitmap(item.image, mBitmap)
                holder.image.setImageBitmap(mBitmap)
            } catch (e: Exception) { }

            if(!item.outputName.isNullOrEmpty()){
                holder.outputName.text = item.outputName
            }
        }
    }
}