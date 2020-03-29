package velord.university.model.entity.vk

data class VkPlaylist(
    val count: Int,
    val items: Array<VkSong>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VkPlaylist

        if (count != other.count) return false
        if (!items.contentEquals(other.items)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = count
        result = 31 * result + items.contentHashCode()
        return result
    }
}