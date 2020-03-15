package velord.university.model

import android.media.MediaMetadataRetriever
import java.io.File
import java.util.*


private const val MAX_DATE: Long = 2678400000L // 1 month in milliseconds

object FileFilter {

    fun filterOnlyAudio(file: File): List<File> {
        val filesInFolder = file.listFiles() ?: arrayOf()
        return filesInFolder.filter {
            FileExtension.checkCompatibleFileExtension(it) ==
                    FileExtensionModifier.AUDIO
        }
    }

    fun filterOnlyAudio(files: Array<File>): List<File> =
        files.filter {
            FileExtension.checkCompatibleFileExtension(it) ==
                    FileExtensionModifier.AUDIO
        }

    val filterBySearchQuery: (File, String) -> Boolean = { file, query ->
        val extension =
            FileExtension.checkCompatibleFileExtension(file) !=
                    FileExtensionModifier.NOT_COMPATIBLE
        val contQuery =
            FileNameParser.removeExtension(file)
                .substringAfterLast('/')
                .toUpperCase(Locale.ROOT)
                .contains(query.toUpperCase(Locale.ROOT))

        extension && contQuery
    }

    val filterByEmptySearchQuery: (File, String) -> Boolean = { file, _ ->
        FileExtension.checkCompatibleFileExtension(file) !=
                FileExtensionModifier.NOT_COMPATIBLE
    }

    val getName: (File) -> String = {
        FileNameParser.getSongName(it)
    }

    val getArtist: (File) -> String = {
        FileNameParser.getSongArtist(it)
    }

    val getLastDateModified: (File) -> Long = {
        it.lastModified()
    }

    val getDuration: (MediaMetadataRetriever, File) -> Long = { metaRetriever, song ->
        metaRetriever.setDataSource(song.path)
        val duration = metaRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        duration.toLong()
    }

    val getSize: (File) -> Long = {
        val fileSize: Long = (it.length() / 1024)
        fileSize
    }

    //sort by last month
    fun recentlyModified(files: List<File>,
                         f: (File) -> Boolean = {
                             it.lastModified() + MAX_DATE > System.currentTimeMillis()
                         }
    ): List<File> = files.filter { it.path.isNotEmpty() }.sortedBy(f)

}