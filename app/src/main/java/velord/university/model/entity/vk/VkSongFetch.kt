package velord.university.model.entity.vk

import androidx.room.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VkSongFetch(
    var artist: String,
    @SerialName("owner_id")
    val ownerId: Int,
    var title: String,
    val duration: Int,
    @SerialName("access_key")
    val accessKey: String? = null,
    @SerialName("is_licensed")
    val isLicensed: Boolean,
    val date: Int,
    @SerialName("is_hq")
    val isHq: Boolean,
    @SerialName("track_genre_id")
    val trackGenreId: Int,
    val id: Int,
    @SerialName("album_id")
    var albumId: Int? = null,
    var url: String = "",
    var path: String = "",
    @SerialName("short_videos_allowed")
    val shortVideosAllowed: Boolean? = false,
    @SerialName("stories_allowed")
    val storiesAllowed: Boolean? = false,
    @SerialName("stories_cover_allowed")
    val storiesCoverAllowed: Boolean? = false,
    @SerialName("no_search")
    val noSearch: Int? = -1,
    @SerialName("content_restricted")
    val contentRestricted: Int? = -1,
    @SerialName("genre_id")
    val genreId: Int = -1,
    @SerialName("lyrics_id")
    val lyricsId: Int = -1,
    val album: VkAlbum? = null
) {

    fun toVkSong(): VkSong = Companion.toVkSong(this)

    companion object {

        fun toVkSong(song: VkSongFetch): VkSong =
            song.run {
                VkSong(
                    artist,
                    ownerId,
                    title,
                    duration,
                    accessKey,
                    isLicensed,
                    date,
                    isHq,
                    trackGenreId,
                    id,
                    albumId,
                    url,
                    path
                )
            }
    }
}