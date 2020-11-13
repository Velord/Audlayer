package velord.university.repository.hub

import android.content.Context
import android.webkit.WebView
import velord.university.application.notification.DownloadNotification
import velord.university.model.entity.music.song.download.DownloadSong
import velord.university.repository.fetch.IMusicFetch
import velord.university.repository.fetch.SefonFetchSequential

object DownloadRepository : BaseRepository() {

    suspend fun downloadViaSefon(context: Context,
                                 webView: WebView,
                                 song: DownloadSong
    ): String? = fetch("downloadViaSefon") {
        SefonFetchSequential(context, webView, song).download()
    }

    suspend fun downloadViaIMusic(context: Context,
                                  webView: WebView,
                                  song: DownloadSong
    ): String? = fetch("downloadViaIMusic") {
        IMusicFetch(context, webView, song).download()
    }


    suspend fun download(context: Context,
                         webView: WebView,
                         song: DownloadSong
    ): String? = fetch("download") {
        downloadViaSefon(context, webView, song) ?:
        downloadViaIMusic(context, webView, song)
    }

    suspend fun downloadAll(context: Context,
                            webView: WebView,
                            toDownload: Array<DownloadSong>): Array<DownloadSong> {
        //build notification
        DownloadNotification.build(context)
        //help variable
        var downloadedCount = 0
        val songCount = toDownload.size
        val downloaded = mutableListOf<DownloadSong>()

        DownloadNotification.reassignmentDownloadState()

        toDownload.forEachIndexed { index, song ->
            //if user cancel download
            if (DownloadNotification.downloadIsCanceled())
                return downloaded.toTypedArray()
            //download file
            val newPath = download(context, webView, song)
            newPath?.let {
                val newSong = DownloadSong(
                    song.artist,
                    song.title,
                    it
                )
                downloaded.add(newSong)
                ++downloadedCount
            }
            //refresh notification
            val progress = "All: $songCount, " +
                    "no link: ${index - downloadedCount + 1}, " +
                    "downloaded: $downloadedCount"
            DownloadNotification.setText(context, progress)
        }
        //finalize notification
        val downloadedText = "Audlayer Downloaded!"
        DownloadNotification.setText(context, downloadedText)

        return downloaded.toTypedArray()
    }
}