package velord.university.ui.fragment.vk

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import velord.university.R
import velord.university.application.settings.VkPreference
import velord.university.ui.backPressed.BackPressedHandlerVkFirst

class VkAccessTokenFragment : Fragment(), BackPressedHandlerVkFirst {

    interface Callbacks {

        fun accessTokenConfirmed()

        fun accessTokenNotConfirmed()
    }

    private var callback: Callbacks? = null

    private val vkAdmin = 6121396
    private val vkOAuth: Uri = Uri.parse(
        "https://oauth.vk.com/authorize?client_id=$vkAdmin&scope=1073737727&" +
                "redirect_uri=https://oauth.vk.com/blank.html&display=page&response_type=token&revoke=1"
    )
    private lateinit var webView: WebView
    private lateinit var pb: ProgressBar

    override fun onBackPressed(): Boolean {
        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.vk_access_token_fragment,
            container, false).apply {
            initViews(this)
        }
    }

    private fun initViews(view: View) {
        initProgressBar(view)
        initWebView(view)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView(view: View) {
        webView = view.findViewById(R.id.web_view)
        webView.apply {
            settings.javaScriptEnabled = true
            webChromeClient = object : WebChromeClient() {

                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    if (newProgress == 100)
                        pb.visibility = View.GONE
                    else {
                        pb.visibility = View.VISIBLE
                        pb.progress = newProgress
                    }
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    (activity as AppCompatActivity).supportActionBar?.subtitle = title
                }
            }
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    retrieveAccessToken(url!!)
                }

            }
            loadUrl(vkOAuth.toString())
        }
    }

    private fun retrieveAccessToken(url: String) {
        val prefix = "https://oauth.vk.com/blank.html#access_token="
        if (url.startsWith(prefix)) {
            val expires = "&expires_in="
            val userId = "&user_id="
            val userEmail = "&email="

            val token = url.substringAfter(prefix).substringBefore(expires)
            VkPreference.setAccessToken(requireContext(), token)

            val id = url.substringAfter(userId).substringBefore(userEmail)
            VkPreference.setPageId(requireContext(), id.toInt())

            val email = url.substringAfter(userEmail)
            VkPreference.setEmail(requireContext(), email)

            callback?.accessTokenConfirmed()
        }
    }

    private fun initProgressBar(view: View) {
        pb = view.findViewById(R.id.pb_vk_page)
        pb.max = 100
    }

    companion object {
        fun newInstance(): VkAccessTokenFragment =
            VkAccessTokenFragment()
    }
}