package velord.university.repository.fetch

import android.content.Context
import com.himanshurawat.hasher.HashType
import com.himanshurawat.hasher.Hasher
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.MultipartBody
import okio.ByteString.Companion.decodeHex
import okio.ByteString.Companion.encode
import velord.university.R
import velord.university.application.settings.VkPreference
import velord.university.model.entity.vk.ResponseVk
import velord.university.model.entity.vk.VkPlaylist
import velord.university.model.entity.vk.VkSong
import velord.university.model.entity.vk.VkSongFetch
import velord.university.ui.fragment.vk.login.PASSWORD
import kotlin.math.log

object VkFetch : FetchJson() {

    suspend fun fetchPlaylist(context: Context): VkPlaylist {
        val login = "+380992345205"
        val userId = VkPreference(context).pageId
        val token = VkPreference(context).accessToken
        val newScope = "nohttps, audio"
        val version = "5.95"
        val deviceId = 7492736296047395

        val dataPart = arrayOf(
            MultipartBody.Part.createFormData(
                "User-Agent", "VkAndroidApp/4.13.1-1206 (Android 4.4.3; SDK 19; armeabi; ru)"
            ),
            MultipartBody.Part.createFormData(
                "Accept", "image/gif, image/x-xbitmap, image-jpeg, image-pjpeg, */*"
            ),
        )
        //v=5.80 in 5.26.2020 9:00 pm don't working
        //v=5.60 don't give album info
        //new valid url cause old retireve only 200 first audios 5.11.2020
        val urlSongList = "https://api.vk.com/method/audio.get?" +
                "access_token=${token}" +
                "&count=60000" +
                "&v=$version"


        //auth by official app vk
        val url = "https://oauth.vk.com/token?grant_type=password" +
                "&scope=$newScope" +
                "&client_id=2274003" +
                "&client_secret=hHbZxrka2uZ6jB1inYsH" +
                "&username=$login" +
                "&password=$PASSWORD" +
                "&v=$version"
        val response = makeResponse<AuthVk>(url, dataPart)
        val authVk = response

        //create hash
        val toHash = "/method/audio.get?" +
                "access_token=${authVk.accessToken}" +
                "&device_id=$deviceId" +
                "&v=$version"
        val md5Hash = Hasher.hash(toHash + authVk.secret, HashType.MD5).encode().hex()
        //create url with sig
        val urlSongWithSig = "https://api.vk.com$toHash" +
                "&sig=$md5Hash"
        //made response
        val responsdfse = makeResponse<ResponseVk>(urlSongWithSig, dataPart)
        val ursdfl = responsdfse.response.items.filter { it.url.isNotEmpty() }
        val sdfsdfsd = ursdfl
        return responsdfse.response
    }
}

@Serializable
data class AuthVk(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("expires_in")
    val expiresIn: Int,
    @SerialName("user_id")
    val userId: Int,
    val secret: String,
    @SerialName("https_required")
    val httpsRequired: Int
)