package com.example.parstagram

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.parstagram.fragments.ComposeFragment
import com.example.parstagram.fragments.FeedFragment
import com.example.parstagram.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.parse.*

/**
 * Let user create a post by taking a photo with their camera.
 */
class MainActivity : AppCompatActivity() {

    lateinit var flContainer: FrameLayout
    lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        flContainer = findViewById(R.id.flContainer)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // TODO: Logout Button
//        findViewById<Button>(R.id.btnLogOut).setOnClickListener {
//            ParseUser.logOut()
//            Log.i(TAG, "User logged out")
//            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
//            val intent = Intent(this@MainActivity, LoginActivity::class.java)
//            startActivity(intent)
//            finish()
//        }

        val fragmentManager: FragmentManager = supportFragmentManager

        bottomNavigationView.setOnItemSelectedListener { item ->
            var fragmentToShow: Fragment? = null
            when (item.itemId) {
                R.id.action_home -> {
                    // Navigate to the home screen / feed fragment
                    fragmentToShow = FeedFragment()
                }
                R.id.action_compose -> {
                    // Navigate to the compose screen
                    fragmentToShow = ComposeFragment()
                }
                R.id.action_profile -> {
                    // Navigate to the profile screen
                    fragmentToShow = ProfileFragment()
                }
            }

            if (fragmentToShow != null) {
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragmentToShow).commit()
            }

            // Return true to say that we've handled this user interaction on the item
            true
        }

        // Set default selection
        bottomNavigationView.selectedItemId = R.id.action_home
    }

    companion object {
        const val TAG = "MainActivity"
    }
}