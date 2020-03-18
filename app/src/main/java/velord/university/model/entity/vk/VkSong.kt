package velord.university.model.entity.vk

data class VkSong(
    val artist: String,
    val id: Int,
    val owner_id: Int,
    val title: String,
    val duration: Int,
    val access_key: String,
    val is_licensed: Boolean,
    val url: String,
    val date: Int,
    val isHQ: Boolean,
    val album: VkAlbum?,
    val track_genre_id: Int
)