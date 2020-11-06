package velord.university.repository.fetch

import android.content.Context
import com.himanshurawat.hasher.HashType
import com.himanshurawat.hasher.Hasher
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.MultipartBody
import okio.ByteString.Companion.encode
import velord.university.application.settings.VkPreference
import velord.university.model.entity.vk.fetch.AuthVk
import velord.university.model.entity.vk.fetch.ResponseVk
import velord.university.model.entity.vk.fetch.VkPlaylist
import velord.university.ui.fragment.vk.login.PASSWORD

object VkFetch : FetchJson() {

    suspend fun fetchPlaylist(context: Context): VkPlaylist {
        //vk Admin rudiments
        val userId = VkPreference(context).pageId
        val token = VkPreference(context).accessToken

        val version = "5.95"
        return getAudio(token, version)
    }

    //not work
     suspend fun getAudioByHash(version: String): VkPlaylist {
        //create auth session
        val authVk = authByPretendViaOfficialAndroidVkApp(version)
        //create hash
        val md5Hash = Hasher.hash(
            getUrlToHash(authVk, version) + authVk.secret,
            HashType.MD5
        )
        //create sig
        val sig = "&sig=$md5Hash"
        //create url with sig
        val urlSongWithSig = "http://api.vk.com" +
                getUrlToHash(authVk, version) + sig
        //made response
        val response = makeResponse<ResponseVk>(
            urlSongWithSig,
            getPretendableDataPart()
        )
        val urlList = response.response.items.filter { it.url.isNotEmpty() }
        return response.response
    }

    private fun getUrlToHash(
        authVk: AuthVk,
        version: String,
    ): String {
        val deviceId = 7492736296047395
        return  "/method/audio.get?" +
                "access_token=${authVk.accessToken}" +
                "&device_id=$deviceId" +
                "&v=$version"
    }

    private fun getAudio(
        token: String,
        version: String
    ): VkPlaylist {
        val dataPart = getPretendableDataPart()
        //v=5.80 in 5.26.2020 9:00 pm don't working
        //v=5.60 don't give album info
        //new valid url cause old retrieved being with only 200 first audios 5.11.2020
        val urlSongList = "https://api.vk.com/method/audio.get?" +
                "access_token=${token}" +
                "&count=60000" +
                "&v=$version"

        val response = makeResponse<ResponseVk>(urlSongList, dataPart)

        return response.response
    }

    private fun authByPretendViaOfficialAndroidVkApp(
        version: String
    ): AuthVk {
        val dataPart = getPretendableDataPart()
        //auth by official app vk
        val login = "+380992345205"
        val newScope = "nohttps, audio"
        val url = "https://oauth.vk.com/token?grant_type=password" +
                "&scope=$newScope" +
                "&client_id=2274003" +
                "&client_secret=hHbZxrka2uZ6jB1inYsH" +
                "&username=$login" +
                "&password=$PASSWORD" +
                "&v=$version"
        return makeResponse<AuthVk>(url, dataPart)
    }

    private fun getPretendableDataPart(): Array<MultipartBody.Part> {
        val userAgent = getUserAgent()
        val accept = getAccept()
        return arrayOf(
            MultipartBody.Part.createFormData(
                userAgent.first, userAgent.second
            ),
            MultipartBody.Part.createFormData(
                accept.first, accept.second
            ),
        )
    }

    private fun getEmptyDataPart(): Array<MultipartBody.Part> =
        arrayOf(
            MultipartBody.Part.createFormData(
                "", ""
            )
        )

    private fun getAccept(): Pair<String, String> =
        Pair("Accept", "image/gif, image/x-xbitmap, image-jpeg, image-pjpeg, */*")

    private fun getUserAgent(): Pair<String, String> =
        Pair("User-Agent", "VkAndroidApp/4.13.1-1206 (Android 4.4.3; SDK 19; armeabi; ru)")

}