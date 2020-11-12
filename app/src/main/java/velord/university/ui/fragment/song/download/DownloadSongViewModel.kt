package velord.university.ui.fragment.song.download

import android.app.Application
import android.content.Context
import android.webkit.WebView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import velord.university.application.notification.DownloadNotification
import velord.university.model.entity.music.song.DownloadSong
import velord.university.model.entity.openFragment.returnResult.OpenFragmentForResult
import velord.university.model.entity.openFragment.returnResult.OpenFragmentForResultWithData
import velord.university.repository.hub.DownloadRepository
import velord.university.repository.hub.HubRepository.downloadRepository

class DownloadSongViewModel(
    private val app: Application
) : AndroidViewModel(app) {

    lateinit var forResult: OpenFragmentForResultWithData<Array<DownloadSong>>

    val downloadedLive: MutableLiveData<DownloadSong> = MutableLiveData()

    val endLive: MutableLiveData<Boolean> = MutableLiveData()

    fun initViewModel(result: OpenFragmentForResultWithData<Array<DownloadSong>>) {
        if (::forResult.isInitialized.not())
            forResult = result
    }

    suspend fun download(webView: WebView) {
        downloadAll(app, webView, forResult.data)
    }

    private suspend fun downloadAll(context: Context,
                            webView: WebView,
                            toDownload: Array<DownloadSong>) {
        //build notification
        DownloadNotification.build(context)
        //help variable
        var downloadedCount = 0

        DownloadNotification.reassignmentDownloadState()

        toDownload.forEachIndexed { index, song ->
            //if user cancel download
            if (DownloadNotification.downloadIsCanceled()) return
            //download file
            val newPath = DownloadRepository.download(context, webView, song)
            newPath?.let {
                val newSong = DownloadSong(
                    song.artist,
                    song.title,
                    it
                )
                downloadedLive.postValue(newSong)
                endLive.postValue(false)
                ++downloadedCount
            }
            //refresh notification
            val songCount = toDownload.size
            val progress = "All: $songCount, " +
                    "no link: ${index - downloadedCount + 1}, " +
                    "downloaded: $downloadedCount"
            DownloadNotification.setText(context, progress)
        }
        //finalize notification
        val downloadedText = "Audlayer Downloaded!"
        DownloadNotification.setText(context, downloadedText)
        //al downloaded infrom ui
        endLive.postValue(true)
    }


    override fun onCleared() {
        super.onCleared()

        viewModelScope.cancel()
    }
}