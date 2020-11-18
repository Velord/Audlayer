package velord.university.model.entity.fileType.file

import android.media.MediaMetadataRetriever
import velord.university.model.entity.music.song.main.AudlayerSong
import java.io.File
import java.util.*


private const val MAX_DATE: Long = 2678400000L // 1 month in milliseconds

object FileRetrieverConverter {

    fun filterOnlyAudio(file: File): List<File> {
        val filesInFolder = file.listFiles() ?: arrayOf()
        return filesInFolder.filter {
            FileExtension.getFileExtension(it) ==
                    FileExtensionModifier.AUDIO
        }
    }
    //only work if extension exist
    fun filterOnlyAudio(files: Array<File>): List<File> = files.filter {
        FileExtension.getFileExtension(it) == FileExtensionModifier.AUDIO
    }

    fun File.filterBySearchQuery(query: String): Boolean {
        val extension = FileExtension.getFileExtension(this) !=
                FileExtensionModifier.NOT_COMPATIBLE

        if (query.isEmpty()) return true

        val nameTitle = FileNameParser.removeExtension(this)
            .substringAfterLast('/')

        val contQuery = filterByArtistTitle(
            nameTitle,
            query
        )

        return extension && contQuery
    }

    val filterByArtistTitle: (String, String) -> Boolean = { nameTitle, query ->
        nameTitle.toUpperCase(Locale.ROOT)
            .contains(query.toUpperCase(Locale.ROOT))
    }

    fun File.getTitle(): String = FileNameParser.getSongTitle(this)

    fun File.getArtist(): String = FileNameParser.getSongArtist(this)

    fun File.getLastDateModified(): Long = this.lastModified()

    fun File.getDuration(mediaRetriever: MediaMetadataRetriever): Long {
        mediaRetriever.setDataSource(this.path)
        val duration = mediaRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        return duration!!.toLong()
    }

    fun File.getSize(): Long = (this.length() / 1024)

    fun File.toAudlayerSong(
        mediaMetadataRetriever: MediaMetadataRetriever
    ): AudlayerSong = AudlayerSong(
        getArtist(),
        getTitle(),
        getDuration(mediaMetadataRetriever).toInt()
    )

    fun File.isAudio(): Boolean = FileExtension.isAudio(this.extension)

    fun Array<File>.toAudlayer(
        mediaRetriever: MediaMetadataRetriever
    ): List<AudlayerSong> = this.map { it.toAudlayerSong(mediaRetriever) }

    fun List<File>.toAudlayer(
        mediaRetriever: MediaMetadataRetriever
    ): List<AudlayerSong> = this.map { it.toAudlayerSong(mediaRetriever) }
}