package velord.university.ui.fragment.vk

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import velord.university.R
import velord.university.application.settings.VkPreference
import velord.university.ui.backPressed.BackPressedHandlerVkZero
import velord.university.ui.fragment.selfLifecycle.LoggerSelfLifecycleFragment
import java.io.IOException

class VkLoginFragment : LoggerSelfLifecycleFragment(), BackPressedHandlerVkZero {

    interface Callbacks {

        fun allCredentialsConfirmed()

        fun openGetAccessToken()
    }

    companion object {
        fun newInstance(): VkLoginFragment = VkLoginFragment()
    }

    override val TAG: String = "VkLoginFragment"

    private var scope = CoroutineScope(Job() + Dispatchers.Default)

    private var callback: Callbacks? = null

    private lateinit var credentialsConfirmed: ImageButton
    private lateinit var getAccessTokenTitle: TextView
    private lateinit var getAccessToken: Button
    private lateinit var userEmail: TextView
    private lateinit var userPage: TextView
    private lateinit var userBirth: TextView
    private lateinit var goBack: TextView

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

    override fun onStop() {
        super.onStop()
        scope.cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.vk_login_fragment,
            container, false).apply {
            initViews(this)
            checkToken()
        }
    }

    private fun initViews(view: View) {
        initGetAccessToken(view)
        credentialsConfirmed = view.findViewById(R.id.vk_login_credentials_confirmed)
        userBirth = view.findViewById(R.id.vk_login_name)
        userEmail = view.findViewById(R.id.vk_login_email)
        userPage = view.findViewById(R.id.vk_login_user_page_id)
        goBack = view.findViewById(R.id.vk_login_go_back)
    }

    private fun initGetAccessToken(view: View) {
        getAccessToken = view.findViewById(R.id.vk_login_get_access_token)
        getAccessToken.apply {
            setOnClickListener {
                tokenIsInvalid()
                scope.cancel()
                scope = CoroutineScope(Job() + Dispatchers.Default)
                callback?.openGetAccessToken()
            }
        }
        getAccessTokenTitle = view.findViewById(R.id.vk_login_get_access_token_title)
    }

    fun checkToken(
        token: String = VkPreference.getAccessToken(requireContext())) {
        if (token.isBlank()) getAccessToken.visibility = View.VISIBLE
        else checkCredentials(token)
    }

    private fun checkCredentials(token: String) {
        val userId = VkPreference.getPageId(requireContext())
        val baseUrl = "https://api.vk.com/method/"
        val birth = "${baseUrl}users.get?user_ids=$userId&fields=bdate&access_token=$token&v=5.80"
        checkBirth(birth)
    }

    private fun tokenIsInvalid() {
        scope.launch {
            VkPreference.setAccessToken(requireContext(), "")
            withContext(Dispatchers.Main) {
                credentialsConfirmed.apply {
                    visibility = View.VISIBLE
                    setImageResource(R.drawable.cancel)
                    VkPreference.setAccessToken(requireContext(), "")
                    VkPreference.setEmail(requireContext(), "")
                    VkPreference.setPageId(requireContext(), -1)
                }
            }
        }
    }

    private fun tokenValid(birth: String)  {
        scope.launch {
            withContext(Dispatchers.Main) {
                credentialsConfirmed.apply {
                    visibility = View.VISIBLE
                    setImageResource(R.drawable.apply)
                }
                userEmail.apply {
                    visibility = View.VISIBLE
                    text = getString(
                        R.string.vk_login_email,
                        VkPreference.getEmail(requireContext())
                    )
                }
                userPage.apply {
                    visibility = View.VISIBLE
                    text = getString(
                        R.string.vk_login_page,
                        VkPreference.getPageId(requireContext())
                    )

                }
                userBirth.apply {
                    visibility = View.VISIBLE
                    text = getString(R.string.vk_login_birth_date, birth)
                }
                goBack.visibility = View.VISIBLE
                goBackAt(5)
            }
        }
    }

    private fun checkBirth(url: String) {
        val valid: (Response) -> Unit = {
            val json = JSONObject(it.body!!.string())
            val jsonResponse = json
                .getJSONArray("response")
                .getJSONObject(0)
            val birth = jsonResponse.get("bdate")
            tokenValid(birth.toString())
        }
        val invalid: (IOException) -> Unit = { tokenIsInvalid() }
        makeRequest(url, valid, invalid)
    }

    private fun makeRequest(
        url: String,
        valid: (Response) -> Unit,
        invalid: (IOException) -> Unit) {

        val client = OkHttpClient()
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = "{}".toRequestBody(mediaType)
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        scope.launch {
            withContext(Dispatchers.IO) {
                client.newCall(request).enqueue(object : Callback {

                    override fun onFailure(call: Call, e: IOException) {
                        invalid(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        valid(response)
                    }
                })
            }
        }
    }

    private suspend fun goBackAt(count: Int) {
        var digit = 5
        repeat(count) {
            goBack.text = getString(R.string.vk_login_go_back_at, digit)
            --digit
            delay(1000)
        }
        callback?.allCredentialsConfirmed()
    }
}