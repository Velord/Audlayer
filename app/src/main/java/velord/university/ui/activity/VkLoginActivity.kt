package velord.university.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import velord.university.R
import velord.university.ui.backPressed.BackPressedHandlerVkFirst
import velord.university.ui.backPressed.BackPressedHandlerVkZero
import velord.university.ui.fragment.vk.VkAccessTokenFragment
import velord.university.ui.fragment.vk.VkLoginFragment
import velord.university.ui.util.addFragment
import velord.university.ui.util.initFragment

class VkLoginActivity : AppCompatActivity(),
    VkAccessTokenFragment.Callbacks,
    VkLoginFragment.Callbacks{

    private val sfm = supportFragmentManager

    private  val TAG ="VkLoginActivity"

    private val vkLogin by lazy { VkLoginFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vk_login_activity)
        initFragment()
    }

    override fun onBackPressed() {
        Log.d(TAG, "onBackPressed")

        backPressedFirstLevel()

        val handled = backPressedZeroLevel()

        if (handled) {
            //Because single activity architecture
            //When first invoke onBackPressed occurred we returned to VKLoginActivity
            //But we need close activity, for this goal we invoke onBackPressed again
            super.onBackPressed()
            super.onBackPressed()
        }
    }

    override fun accessTokenConfirmed() {
        backPressedFirstLevel()
        vkLogin.checkToken()
    }

    override fun accessTokenNotConfirmed() {
        Toast.makeText(baseContext, "Something wrong", Toast.LENGTH_LONG).show()
        onBackPressed()
    }

    override fun allCredentialsConfirmed() {
        onBackPressed()
    }

    private fun backPressedFirstLevel(): Boolean {
        val fragments = supportFragmentManager.fragments
        var handled = false

        for (fragment in fragments) {
            if (fragment is BackPressedHandlerVkFirst) {
                handled = fragment.onBackPressed()
                if (handled) {
                    sfm.popBackStackImmediate()
                    return true
                }
            }
        }

        return handled
    }

    private fun backPressedZeroLevel(): Boolean {
        val fragments = supportFragmentManager.fragments
        var handled = false

        for (fragment in fragments) {
            if (fragment is BackPressedHandlerVkZero) {
                handled = fragment.onBackPressed()
                if (handled) {
                    return true
                }
            }
        }

        return handled
    }

    override fun openGetAccessToken() {

        addFragment(sfm,
            VkAccessTokenFragment(),
            R.id.fragment_container_vk_login
        )
    }

    private fun initFragment() =
        initFragment(sfm,
            vkLogin,
            R.id.fragment_container_vk_login
        )

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, VkLoginActivity::class.java)
        }
    }
}