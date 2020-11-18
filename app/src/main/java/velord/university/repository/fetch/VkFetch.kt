package velord.university.repository.fetch

import android.content.Context
import com.himanshurawat.hasher.HashType
import com.himanshurawat.hasher.Hasher
import okhttp3.MultipartBody
import velord.university.application.settings.VkPreference
import velord.university.model.entity.vk.fetch.AuthVk
import velord.university.model.entity.vk.fetch.ResponseVk
import velord.university.model.entity.vk.fetch.VkPlaylist
import velord.university.model.entity.vk.fetch.VkSongFetch

object VkFetch : FetchJson() {

    private const val version: String = "5.95"

    suspend fun fetchPlaylist(context: Context): Array<VkSongFetch> =
        getAudio(VkPreference(context).accessToken).items

    private fun getAudio(
        token: String,
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

    private fun authByPretendViaOfficialAndroidVkApp(context: Context): AuthVk {
        val dataPart = getPretendableDataPart()
        //auth by official app vk
        val login = VkPreference(context).login
        val password = VkPreference(context).password
        //add 'nohttps, if need test with sig parameter'
        val newScope = "audio"
        val url = "https://oauth.vk.com/token?grant_type=password" +
                "&scope=$newScope" +
                "&client_id=2274003" +
                "&client_secret=hHbZxrka2uZ6jB1inYsH" +
                "&username=$login" +
                "&password=$password" +
                "&v=$version"

        return makeResponse(url, dataPart)
    }

    //not work
     suspend fun getAudioByHash(context: Context): VkPlaylist {
        //create auth session
        val authVk = authByPretendViaOfficialAndroidVkApp(context)
        //create hash
        val md5Hash = Hasher.hash(
            getUrlToHash(authVk) + authVk.secret,
            HashType.MD5
        )
        //create sig
        val sig = "&sig=$md5Hash"
        //create url with sig
        val urlSongWithSig = "http://api.vk.com" + getUrlToHash(authVk) + sig
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
    ): String {
        val deviceId = 7492736296047395
        return  "/method/audio.get?" +
                "access_token=${authVk.accessToken}" +
                "&device_id=$deviceId" +
                "&v=$version"
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