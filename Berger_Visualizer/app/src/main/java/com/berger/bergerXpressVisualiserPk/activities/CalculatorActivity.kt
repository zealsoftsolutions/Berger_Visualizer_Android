package com.berger.bergerXpressVisualiserPk.activities

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import com.berger.bergerXpressVisualiserPk.R
import com.berger.bergerXpressVisualiserPk.customWidgets.CustomDialogs
import com.berger.bergerXpressVisualiserPk.models.GeneralResponse
import com.berger.bergerXpressVisualiserPk.models.Product
import com.berger.bergerXpressVisualiserPk.restApis.RestApis
import com.berger.bergerXpressVisualiserPk.restApis.RetroClient
import com.berger.bergerXpressVisualiserPk.utill.Constants
import com.berger.bergerXpressVisualiserPk.utill.Internet
import com.berger.bergerXpressVisualiserPk.utill.Utills
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CalculatorActivity : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener,AdapterView.OnItemSelectedListener {

    private lateinit var title: TextView
    private lateinit var back: ImageView

    private lateinit var calculatorScroll: ScrollView

    private lateinit var surfaceDropDownHint: TextView
    private lateinit var surfaceDropDown: Spinner

    private lateinit var paintDropDownHint: TextView
    private lateinit var paintDropDown: Spinner

    private lateinit var coatsNo: EditText
//    private lateinit var add: ImageView
//    private lateinit var minus: ImageView

    private lateinit var sqFeet: TextView
    private lateinit var sqMeter: TextView

    private lateinit var length: EditText
    private lateinit var enterWidth: EditText
    private lateinit var enterArea: EditText

    private lateinit var result: TextView
    private lateinit var resultIsEst: TextView

    private lateinit var clear: Button
    private lateinit var calculate: Button

    private var inSqFeet = true
    private var inSqMeter = false

    private var selectedSurface = "-1"
    private var surfacesList: ArrayList<String>? = null
    private var selectedProduct: Product? = null

    private val SELECT_PRODUCT_ACTIVITY = 1

    private var customDialog: CustomDialogs? = null
    private var mLastClickTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        setViews()
    }

    private fun setViews() {
        title = findViewById(R.id.title)
        title.text = resources.getText(R.string.title_calculator_screen)
        back = findViewById(R.id.back)
        back.setOnClickListener(this)

        calculatorScroll = findViewById(R.id.calculator_scroll)

        surfaceDropDownHint = findViewById(R.id.surface_dropdown_hint)
        surfaceDropDown = findViewById(R.id.surface_dropdown)

        paintDropDownHint = findViewById(R.id.paint_dropdown_hint)
        paintDropDownHint.setOnClickListener(this)
        paintDropDown = findViewById(R.id.paint_dropdown)

        coatsNo = findViewById(R.id.coats)
//        add = findViewById(R.id.add)
//        add.setOnClickListener(this)
//        minus = findViewById(R.id.minus)
//        minus.setOnClickListener(this)

        sqFeet = findViewById(R.id.sq_feet)
        sqFeet.setOnClickListener(this)
        sqMeter = findViewById(R.id.sq_meter)
        sqMeter.setOnClickListener(this)

        length = findViewById(R.id.length)
        length.onFocusChangeListener = this
        enterWidth = findViewById(R.id.width)
        enterWidth.onFocusChangeListener = this
        enterArea = findViewById(R.id.area)
        enterArea.setOnClickListener(this)
        enterArea.onFocusChangeListener = this

        result = findViewById(R.id.result)
        resultIsEst = findViewById(R.id.result_is_est)

        clear = findViewById(R.id.clear)
        clear.setOnClickListener(this)
        calculate = findViewById(R.id.calculate)
        calculate.setOnClickListener(this)

        customDialog = CustomDialogs(this)

        Utills.changeNavigationBarColor(this, Constants.COLOR_THEME)

        surfacesList = Utills.getSurfacesFromDatabase(this)

        if(surfacesList != null){
            setSurfaceSpinner()
        } else {
            getSurfacesListCall()
        }
        setUnitSelection()
        setTextChangeListeners()
    }

    private fun setSurfaceSpinner(){
        if(surfacesList == null) {
            surfacesList = ArrayList()
            surfacesList?.addAll(resources.getStringArray(R.array.surface_list))
        } else {
            surfacesList?.add(0, resources.getStringArray(R.array.surface_list)[0])
        }

        surfaceDropDown.onItemSelectedListener = this

        val surfaceAdapter = ArrayAdapter(this, R.layout.custom_spinner_item, surfacesList!!)
        surfaceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        surfaceDropDown.adapter = surfaceAdapter
    }

    private fun setPaintSpinner(){
        paintDropDown.onItemSelectedListener = this

        var paintAdapter = ArrayAdapter(this, R.layout.custom_spinner_item, resources.getStringArray(R.array.surface_list))
        paintAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        paintDropDown.adapter = paintAdapter
    }

    private fun setUnitSelection(){
        if(inSqFeet){
            sqFeet.setBackgroundResource(R.drawable.background_button_small_dark)
            sqFeet.setTextColor(resources.getColor(R.color.white))
            sqMeter.setBackgroundResource(R.drawable.background_button_small_light)
            sqMeter.setTextColor(resources.getColor(R.color.grey))
        } else if(inSqMeter){
            sqFeet.setBackgroundResource(R.drawable.background_button_small_light)
            sqFeet.setTextColor(resources.getColor(R.color.grey))
            sqMeter.setBackgroundResource(R.drawable.background_button_small_dark)
            sqMeter.setTextColor(resources.getColor(R.color.white))
        }
        removeResult()
    }

    private fun setTextChangeListeners() {

        coatsNo.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (s.length > 0) {
                    coatsNo.setBackgroundResource(R.drawable.background_et_round)
                    removeResult()
//                    performCalculation(false)
                }
            }
        })

        length.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (s.length > 0) {
                    length.setBackgroundResource(R.drawable.background_et_round)
                    enterArea.setBackgroundResource(R.drawable.background_et_round)
                    enterArea.setText("")
                    removeResult()
                }
//                performCalculation(false)
            }
        })

        enterWidth.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (s.length > 0) {
                    enterWidth.setBackgroundResource(R.drawable.background_et_round)
                    enterArea.setBackgroundResource(R.drawable.background_et_round)
                    enterArea.setText("")
                    removeResult()
                }
//                performCalculation(false)
            }
        })

        enterArea.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (s.length > 0) {
                    length.setBackgroundResource(R.drawable.background_et_round)
                    enterWidth.setBackgroundResource(R.drawable.background_et_round)
                    enterArea.setBackgroundResource(R.drawable.background_et_round)
                    length.setText("")
                    enterWidth.setText("")
                    removeResult()
                }
//                performCalculation(false)
            }
        })
    }

    private fun getSurfacesListCall() {

        if (Internet.isAvailable(this)) {

            customDialog?.showLoadingDialogue()

            val restApis = RetroClient.getClient().create(RestApis::class.java)

            val sendSurfacesListCall = restApis.productCategoriesListCall
            sendSurfacesListCall.enqueue(object : Callback<GeneralResponse> {
                override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                    customDialog?.dismissLoadingDialogue()
                    if (response.isSuccessful) {
                        if(response.body() != null && response.body()!!.results != null &&
                            response.body()!!.results?.surfaceCategories != null && response.body()!!.results?.surfaceCategories!!.size > 0){

                                surfacesList = response.body()!!.results?.surfaceCategories
                            Utills.updateSurfacesDatabase(this@CalculatorActivity, surfacesList)
                            setSurfaceSpinner()
                        }
                    } else {
                        Utills.showToast(this@CalculatorActivity, resources.getString(R.string.calculator_toast_no_surface_list))
                    }
                }

                override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                    customDialog?.dismissLoadingDialogue()
                    Utills.showToast(this@CalculatorActivity, resources.getText(R.string.connection_problem).toString())
                }
            })

        } else {
            Utills.showToast(this, resources.getText(R.string.no_internet_connection).toString())
        }
    }

    private fun animateTextView(initialValue: Double, finalValue: Double, textview: TextView) {
        val valueAnimator: ValueAnimator = ValueAnimator.ofFloat(initialValue.toFloat(), finalValue.toFloat())
        valueAnimator.duration = 100
        valueAnimator.addUpdateListener {
                valueAnimator -> textview.text = valueAnimator.animatedValue.toString()
        }
        valueAnimator.start()

        val anim: Animation = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 150
        anim.startOffset = 20
        anim.repeatMode = Animation.REVERSE
        anim.repeatCount = 2

        if(textview.id == R.id.result) {
            valueAnimator.doOnEnd {
                result.text = Utills.formatResultValue(finalValue) + " " + resources.getString(R.string.calculator_label_lts_required)
                resultIsEst.visibility = View.VISIBLE
                resultIsEst.startAnimation(anim)
            }
        }
    }

    private fun removeResult(){
        result.text = ""
        resultIsEst.visibility = View.GONE
    }

    private fun isValid(): Boolean{

        var valid = true

        if(selectedProduct == null){
            paintDropDownHint.setBackgroundResource(R.drawable.background_et_round_red)
        }

        if(coatsNo.text.isNullOrEmpty()){
            valid = false
            coatsNo.setBackgroundResource(R.drawable.background_et_round_red)
        }

        if(length.text.isNullOrEmpty() || enterWidth.text.isNullOrEmpty()){
           if(length.text.isNullOrEmpty() && enterWidth.text.isNullOrEmpty()) {
               if(enterArea.text.isNullOrEmpty()){
                   valid = false
                   if(length.text.isNullOrEmpty()){
                       length.setBackgroundResource(R.drawable.background_et_round_red)
                       valid = false
                   }

                   if(enterWidth.text.isNullOrEmpty()){
                       enterWidth.setBackgroundResource(R.drawable.background_et_round_red)
                       valid = false
                   }

                   if(enterArea.text.isNullOrEmpty()){
                       enterArea.setBackgroundResource(R.drawable.background_et_round_red)
                       valid = false
                   }
               }
           } else {
               if(length.text.isNullOrEmpty()){
                   length.setBackgroundResource(R.drawable.background_et_round_red)
                   valid = false
               }

               if(enterWidth.text.isNullOrEmpty()){
                   enterWidth.setBackgroundResource(R.drawable.background_et_round_red)
                   valid = false
               }
           }
        }

        return valid
    }

    private fun performCalculation(hideKeyboard: Boolean, showToast: Boolean){
        var requiredPaint = 0.0

        var coats: Int? = null

        try {
            coats = coatsNo.text.toString().toInt()
        } catch (e: Exception){ }

        var area : Double? = null

        if(!length.text.isNullOrEmpty() && !enterWidth.text.isNullOrEmpty()){
            area = length.text.toString().toDouble() * enterWidth.text.toString().toDouble()
            enterArea.isFocusableInTouchMode = false
        } else if(!enterArea.text.isNullOrEmpty()){
            area = enterArea.text.toString().toDouble()
        }

        if(coats != null && area != null){
            if(inSqFeet) {
                if (selectedProduct?.coverageSqFeet != null) {
                    requiredPaint = area / (selectedProduct?.coverageSqFeet!! / coats)
                    animateTextView(0.0, requiredPaint, result)
                }
            } else if(inSqMeter){
                if (selectedProduct?.coverageSqMeter != null) {
                    requiredPaint = area / (selectedProduct?.coverageSqMeter!! / coats)
                    animateTextView(0.0, requiredPaint, result)
//                    result.text = Utills.formatResultValue(requiredPaint) + " " + resources.getString(R.string.calculator_label_lts_required)
//                    resultIsEst.visibility = View.VISIBLE
                }
            }

            if(hideKeyboard) {
                Utills.hideKeyboardFrom(this, enterArea)
                calculatorScroll.fullScroll(View.FOCUS_DOWN)
            }
        } else {
            removeResult()
            if(showToast)
                Utills.showToast(this, resources.getString(R.string.calculator_toast_fill_all))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            SELECT_PRODUCT_ACTIVITY -> {
                if (resultCode == Activity.RESULT_OK) {
                    selectedProduct = data!!.getSerializableExtra(Constants.INTENT_PRODUCT) as Product

                    if(selectedProduct != null) {

                        if(selectedProduct?.productName != null) {
                            paintDropDownHint.text = selectedProduct?.productName
                            paintDropDownHint.setTextColor(resources.getColor(R.color.black))
                        }

                        if(selectedProduct?.recommendedCoats != null)
                            coatsNo.setText(selectedProduct?.recommendedCoats.toString())

                        paintDropDownHint.setBackgroundResource(R.drawable.background_et_round)
                        removeResult()
//                        performCalculation(false)
                    }
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.sq_feet ->{
                inSqFeet = true
                inSqMeter = false
                setUnitSelection()
                performCalculation(true, false)
//                if(isValid()){
//                    performCalculation(false)
//                }
            }

            R.id.sq_meter -> {
                inSqFeet = false
                inSqMeter = true
                setUnitSelection()
                performCalculation(true, false)
//                if(isValid()){
//                    performCalculation(false)
//                }
            }

            R.id.area -> {
                if(!enterArea.isFocusableInTouchMode) {
                    enterArea.isFocusableInTouchMode = true
                    enterArea.requestFocus()
                    Utills.openKeyboard(this, enterArea)
                }
            }

//            R.id.add -> {
//                try {
//                    coatsNo.setText((coatsNo.text.toString().toInt()+1).toString())
//                } catch (e: Exception) {}
//            }
//
//            R.id.minus -> {
//                try {
//                    if (coatsNo.text.toString().toInt() > 1) {
//                        coatsNo.setText((coatsNo.text.toString().toInt()-1).toString())
//                    }
//                } catch (e: Exception) {}
//            }

            R.id.clear -> {
                length.setText("")
                length.setBackgroundResource(R.drawable.background_et_round)
                enterWidth.setText("")
                enterWidth.setBackgroundResource(R.drawable.background_et_round)
                enterArea.setText("")
                enterArea.setBackgroundResource(R.drawable.background_et_round)
                removeResult()
            }

            R.id.calculate -> {
                if(isValid()){
                    performCalculation(true, true)
                } else {
                    Utills.showToast(this, resources.getString(R.string.calculator_toast_fill_all))
                }
            }

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

            R.id.back -> finish()

            R.id.paint_dropdown_hint -> {
                val intent = Intent(this, ProductsActivity::class.java)
                intent.putExtra(Constants.INTENT_PERFORM_SELECT, true)
                intent.putExtra(Constants.INTENT_SURFACE, selectedSurface)
                startActivityForResult(intent, SELECT_PRODUCT_ACTIVITY)
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.id) {
            R.id.surface_dropdown -> {
                if(selectedSurface == "-1"){
                    selectedSurface = ""
                } else {
                    if (position > 0)
                        selectedSurface = resources.getStringArray(R.array.surface_list)[position]
                    else
                        selectedSurface = ""

                    surfaceDropDownHint.visibility = View.GONE
                    removeResult()
//                    performCalculation(false)
//                } else {
//                    selectedSurface = ""
//                    surfaceDropDownHint.visibility = View.VISIBLE
//                }
                }
            }

            R.id.paint_dropdown -> {

            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        when (v?.id) {
            R.id.length -> {
                if(hasFocus){
                    sqFeet.text = resources.getString(R.string.calculator_label_feet)
                    sqMeter.text = resources.getString(R.string.calculator_label_meter)
                }
            }

            R.id.width -> {
                if(hasFocus){
                    sqFeet.text = resources.getString(R.string.calculator_label_feet)
                    sqMeter.text = resources.getString(R.string.calculator_label_meter)
                }
            }

            R.id.area -> {
                if(hasFocus){
                    sqFeet.text = resources.getString(R.string.calculator_label_sq_feet)
                    sqMeter.text = resources.getString(R.string.calculator_label_sq_meter)
                }
            }
        }
    }
}