package com.berger.bergerXpressVisualiserPk.activities

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.core.view.ViewCompat
import com.berger.bergerXpressVisualiserPk.R
import com.berger.bergerXpressVisualiserPk.utill.Utills

class SplashActivity : AppCompatActivity() {

    private var mLastClickTime: Long = 0
    private val STARTUP_DELAY = 0.00
    private val ANIM_ITEM_DURATION = 1000.00
    private var logoSection: ConstraintLayout? = null
    private var icon: ImageView? = null
    private lateinit var bergerLabel: TextView
    private lateinit var guideline: Guideline

    private var animate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        super.onCreate(savedInstanceState)
        Utills.transparentToolbar(this, false)
        Utills.transparentNavigation(this, true, false)
        setContentView(R.layout.activity_splash)

        logoSection = findViewById(R.id.logo_section)
        icon = findViewById(R.id.icon)
        guideline = findViewById(R.id.middle_guideline)
        bergerLabel = findViewById(R.id.berger_label)
//        CustomFonts.setRegularFontOnTextView(this, cakeCottageLabel)

        if(!animate){
            animate()
        }
    }

    private fun startMain(){
        val i = Intent(this, HomeActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(i)
    }

    private fun animate() {
        val logoView = findViewById<ConstraintLayout>(R.id.logo_section)

        val orientation = resources.configuration.orientation

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ViewCompat.animate(logoView)
                .translationX((-250).toFloat())
                .setStartDelay(STARTUP_DELAY.toLong())
                .setDuration(ANIM_ITEM_DURATION.toLong()).setInterpolator(
                    DecelerateInterpolator(1.2f)
                ).withEndAction(Runnable {

                    afterAnimation()
                }).start()
        } else {
            ViewCompat.animate(logoView)
                .translationY((-200).toFloat())
                .setStartDelay(STARTUP_DELAY.toLong())
                .setDuration(ANIM_ITEM_DURATION.toLong()).setInterpolator(
                    DecelerateInterpolator(1.2f)
                ).withEndAction(Runnable {

                    afterAnimation()
                }).start()
        }
    }

    private fun afterAnimation(){

        startMain()
        animate = true
    }
}