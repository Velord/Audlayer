package velord.university.model.entity.vk.fetch

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import velord.university.model.entity.music.newGeneration.song.AudlayerSong

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
data class Album(
    val id: Int,
    val title: String,
    @SerialName("owner_id")
    val ownerId: Int,
    @SerialName("access_key")
    val accessKey: String,
    val thumb: Thumb? = null
)

@Serializable
data class Thumb(
    val width: Int,
    val height: Int,
    @SerialName("photo_34")
    val photo34: String,
    @SerialName("photo_68")
    val photo68: String,
    @SerialName("photo_135")
    val photo135: String,
    @SerialName("photo_270")
    val photo270: String,
    @SerialName("photo_300")
    val photo300: String,
    @SerialName("photo_600")
    val photo600: String,
    @SerialName("photo_1200")
    val photo1200: String,
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
    val ads: Ads? = null,
    @SerialName("track_code")
    val trackCode: String,
    @SerialName("main_artists")
    val mainArtistList: Array<MainArtist>?= null,
    val subtitle: String? = null,
    val album: Album ? = null,
    @SerialName("featured_artists")
    val featuredArtistList: Array<FeaturedArtist>? = null,
) {

    fun toAudlayerSong(): AudlayerSong = toVkSong(this)

    fun getImgUrl(): String =
        this.album?.thumb?.photo1200 ?: ""

    fun getFullName(): String = "$artist$title"

    companion object {

        fun toVkSong(song: VkSongFetch): AudlayerSong =
            song.run {
                AudlayerSong(
                    artist,
                    title,
                    duration,
                    getImgUrl()
                )
            }
    }
}