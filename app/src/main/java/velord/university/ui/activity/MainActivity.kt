package velord.university.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import velord.university.R
import velord.university.application.initFragment
import velord.university.application.miniPlayer.service.MiniPlayerServiceBroadcastReceiver
import velord.university.ui.fragment.BackPressedHandler
import velord.university.ui.fragment.main.MainFragment


private const val TAG ="MainActivity"

class MainActivity : AppCompatActivity() {

    private val fm = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "called onCreate")
        super.onCreate(savedInstanceState)
        //service
        startService(Intent(this, MiniPlayerServiceBroadcastReceiver().javaClass))
        //self view
        setContentView(R.layout.main_activity)
        //fragment
        initFragment(
            fm,
            MainFragment(),
            R.id.main_container
        )
    }

    override fun onBackPressed() {
       Log.d(TAG, "onBackPressed")

        val fragments = supportFragmentManager.fragments

        var handled = false
        for (fragment in fragments) {
            if (fragment is BackPressedHandler) {
                handled = fragment.onBackPressed()
                if (handled) {
                    break
                }
            }
        }

        if (!handled) {
            //Because single activity architecture
            //When first invoke onBackPressed occurred we returned to MainActivity
            //But we need close app, for this goal we invoke onBackPressed again
            super.onBackPressed()
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "called onDestroy")
        super.onDestroy()
        stopService(Intent(this, MiniPlayerServiceBroadcastReceiver().javaClass))
    }
}

