package com.berger.bergerXpressVisualiserPk.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.RecyclerView
import com.berger.bergerXpressVisualiserPk.R
import com.berger.bergerXpressVisualiserPk.adapters.SideMenuListAdapter
import com.berger.bergerXpressVisualiserPk.models.GeneralResponse
import com.berger.bergerXpressVisualiserPk.models.SideMenuItem
import com.berger.bergerXpressVisualiserPk.models.SideMenuPersonalization
import com.berger.bergerXpressVisualiserPk.restApis.RestApis
import com.berger.bergerXpressVisualiserPk.restApis.RetroClient
import com.berger.bergerXpressVisualiserPk.utill.Constants
import com.berger.bergerXpressVisualiserPk.utill.Internet
import com.berger.bergerXpressVisualiserPk.utill.Preferences
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class HomeActivity : AppCompatActivity(), View.OnClickListener, NavigationView.OnNavigationItemSelectedListener{

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var logo: ImageView
    private lateinit var menuIcon: ImageView

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var menuView: LinearLayout
    private var sideMenuBackgroundsList: ArrayList<SideMenuPersonalization>? = null
    private var sideMenuBackgroundIndex = -1

    private lateinit var menuRecycler: RecyclerView
    private lateinit var menuAdapter: SideMenuListAdapter
    private lateinit var aboutUsDivider: View
    private lateinit var aboutUsLabel: TextView
    private lateinit var aboutUsMenuRecycler: RecyclerView
    private lateinit var aboutUsMenuAdapter: SideMenuListAdapter
    private lateinit var followUsDivider: View
    private lateinit var socialBarLabel: TextView
    private lateinit var socialFB: ImageView
    private lateinit var socialTwitter: ImageView
    private lateinit var socialInsta: ImageView
    private lateinit var socialPint: ImageView
    private lateinit var socialGoogle: ImageView
    private var menuList: ArrayList<SideMenuItem>? = null
    private var aboutUsMenuList: ArrayList<SideMenuItem>? = null

    private lateinit var visualizerCard: CardView
    private lateinit var colorsCard: CardView
    private lateinit var calculatorCard: CardView
    private lateinit var productsCard: CardView
    private lateinit var albumCard: CardView
    private lateinit var inspirationalIdeasCard: CardView
    private lateinit var aboutUsCard: CardView
    private lateinit var contactUsCard: CardView

    private var mLastClickTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setViews()
    }

    private fun setViews(){
        getDatabaseVersion()

        drawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.addDrawerListener(object : DrawerListener {
            override fun onDrawerSlide(view: View, v: Float) {}
            override fun onDrawerOpened(view: View) {}
            override fun onDrawerClosed(view: View) {
                setSideMenuBackground()
            }
            override fun onDrawerStateChanged(i: Int) {}
        })
        navView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)

        personalizeSideMenu()


        logo = findViewById(R.id.logo_toolbar)
        logo.visibility = View.VISIBLE
        title = findViewById(R.id.title)
//        title.visibility = View.GONE
//        back = findViewById(R.id.back)
//        back.visibility = View.GONE
        menuIcon = findViewById(R.id.menu)
        menuIcon.setOnClickListener(this)
        menuIcon.visibility = View.VISIBLE

        setSideMenu()

//        showMenu(menuView)

        visualizerCard = findViewById(R.id.visualizer_card)
        visualizerCard.setOnClickListener(this)
        calculatorCard = findViewById(R.id.calculator_card)
        calculatorCard.setOnClickListener(this)
        colorsCard = findViewById(R.id.colors_card)
        colorsCard.setOnClickListener(this)
        productsCard = findViewById(R.id.products_card)
        productsCard.setOnClickListener(this)
        albumCard = findViewById(R.id.album_card)
        albumCard.setOnClickListener(this)
        inspirationalIdeasCard = findViewById(R.id.inspirational_ideas_card)
        inspirationalIdeasCard.setOnClickListener(this)
        aboutUsCard = findViewById(R.id.about_us_card)
        aboutUsCard.setOnClickListener(this)
        contactUsCard = findViewById(R.id.contact_us_card)
        contactUsCard.setOnClickListener(this)

//        Utills.changeNavigationBarColor(this, Constants.COLOR_THEME)
    }

    private fun personalizeSideMenu(){
        sideMenuBackgroundsList = ArrayList()

        sideMenuBackgroundsList?.add(SideMenuPersonalization(R.drawable.background_side_menu_r, resources.getColor(R.color.side_menu_top_color_r)))
        sideMenuBackgroundsList?.add(SideMenuPersonalization(R.drawable.background_side_menu_g, resources.getColor(R.color.side_menu_top_color_g)))
        sideMenuBackgroundsList?.add(SideMenuPersonalization(R.drawable.background_side_menu_b, resources.getColor(R.color.side_menu_top_color_b)))
        sideMenuBackgroundsList?.add(SideMenuPersonalization(R.drawable.background_side_menu_y, resources.getColor(R.color.side_menu_top_color_y)))
        sideMenuBackgroundsList?.add(SideMenuPersonalization(R.drawable.background_side_menu_p, resources.getColor(R.color.side_menu_top_color_p)))
    }

    private fun setSideMenu(){
        menuRecycler = navView.findViewById(R.id.menu_recycler)
        aboutUsDivider = navView.findViewById(R.id.about_us_divider)
        aboutUsLabel = navView.findViewById(R.id.about_us_label)
        aboutUsMenuRecycler = navView.findViewById(R.id.about_us_menu_recycler)
        followUsDivider = navView.findViewById(R.id.follow_us_divider)
        socialBarLabel = navView.findViewById(R.id.social_bar_label)
        val socialWeb = navView.findViewById<ImageView>(R.id.web)
        socialWeb.setOnClickListener { openSocialLink( Constants.SOCIAL_WEB) }
        socialFB = navView.findViewById(R.id.fb)
        socialFB.setOnClickListener { openSocialLink(Constants.SOCIAL_FB) }
        socialTwitter = navView.findViewById(R.id.twitter)
        socialTwitter.setOnClickListener { openSocialLink(Constants.SOCIAL_TWITTER) }
        socialInsta = navView.findViewById(R.id.insta)
        socialInsta.setOnClickListener { openSocialLink(Constants.SOCIAL_INSTA) }
        socialPint = navView.findViewById(R.id.pint)
        socialPint.setOnClickListener { openSocialLink(Constants.SOCIAL_PINT) }
        socialGoogle = navView.findViewById(R.id.google)
        socialGoogle.setOnClickListener { openSocialLink(Constants.SOCIAL_GOOGLE) }

        menuList = ArrayList()

        val menuItem1 = SideMenuItem()
        menuItem1.itemIcon = R.drawable.background_card_visualizer
        menuItem1.itemLabel = resources.getString(R.string.side_menu_visualizer)

        val menuItem2 = SideMenuItem()
        menuItem2.itemIcon = R.drawable.background_card_colors
        menuItem2.itemLabel = resources.getString(R.string.side_menu_colors)

        val menuItem3 = SideMenuItem()
        menuItem3.itemIcon = R.drawable.background_card_products
        menuItem3.itemLabel = resources.getString(R.string.side_menu_products)

        val menuItem4 = SideMenuItem()
        menuItem4.itemIcon = R.drawable.background_card_calculate_paint
        menuItem4.itemLabel = resources.getString(R.string.side_menu_calculator)

        val menuItem5 = SideMenuItem()
        menuItem5.itemIcon = R.drawable.background_card_inspirational
        menuItem5.itemLabel = resources.getString(R.string.side_menu_inspirational)

        val menuItem6 = SideMenuItem()
        menuItem6.itemIcon = R.drawable.background_card_my_ideas
        menuItem6.itemLabel = resources.getString(R.string.side_menu_album)

        val menuItem7 = SideMenuItem()
        menuItem7.itemIcon = R.drawable.icon_side_menu_option
        menuItem7.itemLabel = resources.getString(R.string.side_menu_dealer)

        menuList?.add(menuItem1)
        menuList?.add(menuItem2)
        menuList?.add(menuItem3)
        menuList?.add(menuItem4)
        menuList?.add(menuItem5)
        menuList?.add(menuItem6)

        val menuItemAbout1 = SideMenuItem()
        menuItemAbout1.itemIcon = R.drawable.background_card_about_us
        menuItemAbout1.itemLabel = resources.getString(R.string.side_menu_about_us)

        val menuItemAbout2 = SideMenuItem()
        menuItemAbout2.itemIcon = R.drawable.background_card_contact_us
        menuItemAbout2.itemLabel = resources.getString(R.string.side_menu_contact_us)

        aboutUsMenuList = ArrayList()

        aboutUsMenuList?.add(menuItemAbout1)
        aboutUsMenuList?.add(menuItemAbout2)

        populateMenus()
    }

    private fun populateMenus(){
        if (menuList == null || menuList?.size == 0) {

        } else {
            menuRecycler.visibility = View.VISIBLE
            menuRecycler.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            menuAdapter = SideMenuListAdapter(this, menuList!!)
            menuRecycler.adapter = menuAdapter
            menuRecycler.invalidate()
        }

        if (aboutUsMenuList == null || aboutUsMenuList?.size == 0) {

        } else {
            aboutUsMenuRecycler.visibility = View.VISIBLE
            aboutUsMenuRecycler.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            aboutUsMenuAdapter = SideMenuListAdapter(this, aboutUsMenuList!!)
            aboutUsMenuRecycler.adapter = aboutUsMenuAdapter
            aboutUsMenuRecycler.invalidate()
        }
    }

    private fun openSocialLink(open: String){
        when(open){
            Constants.SOCIAL_WEB -> {
                var intent = Intent()
                try {
                    intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://berger.com.pk/"))
                    startActivity(intent)
                } catch (e: Exception) { }
            }

            Constants.SOCIAL_FB -> {
                var intent = Intent()

                try {
                    packageManager.getPackageInfo("com.facebook.katana", 0)
                    intent = Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/berger.pak"))
                    startActivity(intent)
                } catch (e: Exception) {
                    try {
                        intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/berger.pak"))
                        startActivity(intent)
                    } catch (e: Exception) {

                    }
                }
            }

            Constants.SOCIAL_TWITTER -> {
                var intent: Intent? = null

                try {
                    // get the Twitter app if possible
                    packageManager.getPackageInfo("com.twitter.android", 0)
                    intent = Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=BergerPakistan"))
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } catch (e: java.lang.Exception) {
                    // no Twitter app, revert to browser
                        try{
                            intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/BergerPakistan"))
                            startActivity(intent)
                        } catch (e: Exception) { }
                }
            }

            Constants.SOCIAL_INSTA -> {
                var intent: Intent? = null

                try {
                    // get the Twitter app if possible
                    packageManager.getPackageInfo("com.instagram.android", 0)
                    intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/_u/bergerpaintspak"))
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } catch (e: java.lang.Exception) {
                    // no Twitter app, revert to browser
                        try {
                            intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/bergerpaintspak/"))
                            startActivity(intent)
                        } catch (e: Exception) { }
                }
            }

            Constants.SOCIAL_PINT -> {
                var intent: Intent? = null

                try {
                    intent = Intent(Intent.ACTION_VIEW, Uri.parse("pinterest://www.pinterest.com/bergerpaintspak"))
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } catch (e: java.lang.Exception) {
                    try {
                        intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.pinterest.com/bergerpaintspak/"))
                        startActivity(intent)
                    } catch (e: Exception) { }
                }
            }
        }

        closeDrawer()
    }

    fun closeDrawer(){
        drawerLayout.closeDrawer(GravityCompat.START)
//        setSideMenuBackground()
    }

    private fun showMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.inflate(R.menu.activity_main_drawer)
        val menu: Menu = popupMenu.getMenu()
//        popupMenu.setOnMenuItemClickListener(this)
        popupMenu.show()
    }

    fun openVisualizer(){
        val intent = Intent(this, SourceSelectActivity::class.java)
        startActivity(intent)
    }

    fun openColors(){
        val intent = Intent(this, ColorShadesDisplayActivity::class.java)
        startActivity(intent)
    }

    fun openCalculator(){
        val intent = Intent(this, CalculatorActivity::class.java)
        startActivity(intent)
    }

    fun openProducts(){
        val intent = Intent(this, ProductsActivity::class.java)
        startActivity(intent)
    }

    fun openInspirationalIdeas(){
        val intent = Intent(this, InspirationalIdeasActivity::class.java)
        startActivity(intent)
    }

    fun openMyIdeas(){
        val intent = Intent(this, MyIdeasActivity::class.java)
        startActivity(intent)
    }

    fun openDealer(){
        val intent = Intent(this, DealerLocatorActivity::class.java)
        startActivity(intent)
    }

    fun openAboutUs(){
        val intent = Intent(this, AboutUsActivity::class.java)
        startActivity(intent)
    }

    fun openContactUs(){
        val intent = Intent(this, ContactUsActivity::class.java)
        startActivity(intent)
    }

    private fun getDatabaseVersion() {

        if (Internet.isAvailable(this)) {

            val restApis = RetroClient.getClient().create(RestApis::class.java)

            val sendCartCall = restApis.databaseVersion
            sendCartCall.enqueue(object : Callback<GeneralResponse> {
                override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                    if (response.isSuccessful) {
                        if(response.body() != null && response.body()!!.results != null &&
                            response.body()!!.results?.dbVersion != null){
                            Preferences.addDatabaseVersionToSharedPreferences(this@HomeActivity, response.body()!!.results?.dbVersion!!)
                        }
                    }
                }

                override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
//                    Utills.showToast(this@HomeActivity, resources.getText(R.string.connection_problem).toString())
                }
            })

        } else {
//            Utills.showToast(this, resources.getText(R.string.no_internet_connection).toString())
        }

    }

    override fun onClick(v: View?) {
        when(v?.id){

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

            R.id.menu -> {
                if (!drawerLayout.isDrawerOpen(GravityCompat.START))
                    drawerLayout.openDrawer(GravityCompat.START)
            }

            R.id.visualizer_card ->
                openVisualizer()

            R.id.calculator_card ->
                openCalculator()

            R.id.colors_card ->
                openColors()

            R.id.products_card ->
                openProducts()

            R.id.album_card ->
                openMyIdeas()

            R.id.inspirational_ideas_card ->
                openInspirationalIdeas()

            R.id.about_us_card ->
                openAboutUs()

            R.id.contact_us_card ->
                openContactUs()
        }
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId){

            R.id.nav_visualizer ->
                openVisualizer()

            R.id.nav_calculator ->
                openCalculator()

            R.id.nav_colors ->
                openColors()

            R.id.nav_products ->
                openProducts()

            R.id.nav_album ->
                openMyIdeas()

            R.id.nav_inspirational ->
                openInspirationalIdeas()

            R.id.nav_about_us ->
                openAboutUs()

            R.id.nav_contact_us ->
                openContactUs()
        }

        closeDrawer()
//        drawerLayout.closeDrawer(GravityCompat.START)
//        setSideMenuBackground()
        return false
    }

    private fun setSideMenuBackground(){
        if(sideMenuBackgroundsList != null && sideMenuBackgroundsList?.size!! > 0) {
            try {

                sideMenuBackgroundIndex = getRandomIndex(sideMenuBackgroundIndex)

                navView.setBackgroundResource(sideMenuBackgroundsList?.get(sideMenuBackgroundIndex)!!.sideMenuBackground)
                aboutUsDivider.setBackgroundColor(sideMenuBackgroundsList?.get(sideMenuBackgroundIndex)!!.sideMenuBackgroundColor)
                followUsDivider.setBackgroundColor(sideMenuBackgroundsList?.get(sideMenuBackgroundIndex)!!.sideMenuBackgroundColor)
            } catch (e: Exception) {}
        }
    }

    private fun getRandomIndex(previousIndex: Int): Int{
        val randomizer = Random()
        var index = 0
        do {
             index = randomizer.nextInt(sideMenuBackgroundsList?.size!!)
        } while (index == previousIndex)

        return index
    }
}