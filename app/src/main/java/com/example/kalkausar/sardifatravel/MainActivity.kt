package com.example.kalkausar.sardifatravel

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import com.example.kalkausar.sardifatravel.Helper.BottomNavigationHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val manager = supportFragmentManager

    private val mOnNavigationItemSelectedListener by lazy {
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val transaction = manager.beginTransaction()
                    val fragment = HomeFragment()
                    transaction.replace(R.id.content, fragment)
                    changeToolbarTitle(getString(R.string.Home))
                    transaction.commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_inbox -> {
                    val transaction = manager.beginTransaction()
                    val fragment = InboxFragment()
                    transaction.replace(R.id.content, fragment)
                    changeToolbarTitle(getString(R.string.Inbox))
                    transaction.commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_booking -> {
                    val transaction = manager.beginTransaction()
                    val fragment = BookingFragment()
                    transaction.replace(R.id.content, fragment)
                    changeToolbarTitle(getString(R.string.Booking))
                    transaction.commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_account -> {
                    val transaction = manager.beginTransaction()
                    val fragment = AccountFragment()
                    transaction.replace(R.id.content, fragment)
                    changeToolbarTitle(getString(R.string.Account))
                    transaction.commit()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initBottomNavigationContainer()
    }

    private fun initBottomNavigationContainer() {
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        //set FragmentHome
        val transaction = manager.beginTransaction()
        val fragment = HomeFragment()
        transaction.replace(R.id.content, fragment)
        transaction.commit()

        val bottomNavigationView = findViewById(R.id.navigation) as BottomNavigationView
        BottomNavigationHelper.disableShiftMode(bottomNavigationView)
    }

    private fun changeToolbarTitle(title: String) {
        setTitle(title)
    }
}
