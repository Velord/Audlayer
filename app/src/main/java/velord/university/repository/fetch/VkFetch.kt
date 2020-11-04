package velord.university.repository.fetch

import android.content.Context
import kotlinx.serialization.Serializable
import okhttp3.MultipartBody
import velord.university.R
import velord.university.application.settings.VkPreference
import velord.university.model.entity.vk.ResponseVk
import velord.university.model.entity.vk.VkPlaylist
import velord.university.model.entity.vk.VkSong
import velord.university.model.entity.vk.VkSongFetch

object VkFetch : FetchJson() {

    suspend fun fetchPlaylist(context: Context): VkPlaylist {
        val dataPart = arrayOf(
            MultipartBody.Part.createFormData(
                "", ""
            )
        )
        val userId = VkPreference(context).pageId
        val token = VkPreference(context).accessToken
        val baseUrl = context.getString(R.string.vk_base_url)
        //v=5.80 in 5.26.2020 9:00 pm don't working
        //v=5.60 don't give album info
        val url = "${baseUrl}audio.get?user_ids=$userId&access_token=$token&v=5.80"

        val response = makeResponse<ResponseVk>(url, dataPart)

        return response.response
    }
}