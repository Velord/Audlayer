package velord.university.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import velord.university.R
import velord.university.ui.fragment.main.MainFragment
import velord.university.util.initFragment

private const val TAG ="MainActivity"

class MainActivity : AppCompatActivity() {

    private val fm = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        Log.d(TAG, "called onCreate")

        initFragment(fm, MainFragment(), R.id.main_container)
    }
}

