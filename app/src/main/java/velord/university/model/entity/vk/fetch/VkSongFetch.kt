package velord.university.model.entity.vk.fetch

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import velord.university.model.entity.vk.entity.VkAlbum
import velord.university.model.entity.vk.entity.VkSong

@Serializable
data class Ads(
    @SerialName("content_id")
    val contentId: String,
    val duration: Int,
    @SerialName("account_age_type")
    val accountAgeType: Int,
    val puid22: Int
)

@Serializable
data class MainArtist(
    val name: String,
    val domain: String,
    val id: String
)

@Serializable
data class FeaturedArtist(
    val name: String,
    val domain: String,
    val id: String
)

@Serializable
data class VkSongFetch(
    var artist: String,
    @SerialName("owner_id")
    val ownerId: Int,
    var title: String,
    val duration: Int,
    @SerialName("access_key")
    val accessKey: String? = null,
    @SerialName("is_explicit")
    val isExplicit: Boolean,
    @SerialName("is_licensed")
    val isLicensed: Boolean,
    val date: Int,
    @SerialName("is_hq")
    val isHq: Boolean,
    @SerialName("track_genre_id")
    val trackGenreId: Int? = null,
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
    val album: VkAlbum? = null,
    val ads: Ads? = null,
    @SerialName("track_code")
    val trackCode: String,
    @SerialName("main_artists")
    val mainArtistList: Array<MainArtist>?= null,
    @SerialName("featured_artists")
    val featuredArtistList: Array<FeaturedArtist>? = null,
    val subtitle: String? = null
) {

    fun toVkSong(): VkSong = toVkSong(this)

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
                    trackGenreId ?: -1,
                    id,
                    albumId,
                    url,
                    path
                )
            }
    }
}