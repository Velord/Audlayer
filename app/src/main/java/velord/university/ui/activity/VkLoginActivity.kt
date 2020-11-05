package velord.university.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import velord.university.R
import velord.university.ui.backPressed.BackPressedHandler
import velord.university.ui.backPressed.BackPressedHandlerVkFirst
import velord.university.ui.backPressed.BackPressedHandlerVkZero
import velord.university.ui.fragment.vk.login.VkAccessTokenFragment
import velord.university.ui.fragment.vk.login.VkLoginFragment
import velord.university.ui.util.addFragment
import velord.university.ui.util.initFragment

class VkLoginActivity : AppCompatActivity(),
    VkAccessTokenFragment.Callbacks,
    VkLoginFragment.Callbacks{

    private val fm = supportFragmentManager

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

    private fun backPressedFirstLevel(): Boolean =
        checkFragmentBackStack<BackPressedHandlerVkFirst> {
            it.popBackStackImmediate()
            true
        }

    private fun backPressedZeroLevel(): Boolean =
        checkFragmentBackStack<BackPressedHandlerVkZero> { true }

    private inline fun <reified T: BackPressedHandler>
            checkFragmentBackStack(f: (FragmentManager) -> Boolean): Boolean {
        var handled = false
        fm.apply {
            fragments.forEach {
                if (it is T) {
                    handled = it.onBackPressed()
                    return f(this)
                }
            }
        }
        return handled
    }

    override fun openGetAccessToken() {

        addFragment(fm,
            VkAccessTokenFragment(),
            R.id.fragment_container_vk_login
        )
    }

    private fun initFragment() =
        initFragment(fm,
            vkLogin,
            R.id.fragment_container_vk_login
        )

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, VkLoginActivity::class.java)
        }
    }
}