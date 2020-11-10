package velord.university.repository.fetch

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import velord.university.model.coroutine.getScope
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import velord.university.model.converter.transliterate
import velord.university.model.entity.vk.fetch.VkDownloadFile
import velord.university.model.entity.vk.entity.VkSong

//https://imusic.я.wiki
data class IMusicFetch(private val context: Context,
                       val webView: WebView,
                       val song: VkSong
) {

    private val TAG = "IMusicFetchSequential"
    private var directSearchLink: String = ""
    private var directDownloadFileLink: String = ""

    private val scope = getScope()

    @SuppressLint("SetJavaScriptEnabled")
    private suspend fun interceptDirectSearchLink() =
        withContext(Dispatchers.Main) {
            val searchLink = "https://rmusic.я.wiki/search/"
            val fullUrl = "$searchLink${song.artist}-${song.title}"
            webView.apply {
                visibility = View.VISIBLE
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                webViewClient = object : WebViewClient() {
                    override fun shouldInterceptRequest(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): WebResourceResponse? {
                        val directUrl = request!!.url.toString()
                        if (directUrl.contains("/search/")) {
                            directSearchLink = directUrl
                            return null
                        }
                        return super.shouldInterceptRequest(view, request)
                    }

                }
                loadUrl(fullUrl)
            }
        }

    @SuppressLint("SetJavaScriptEnabled")
    suspend fun interceptDirectFileLink(url: String) =
        withContext(Dispatchers.Main) {
            val fullUrl = url
            webView.apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                val js = "javascript:(function(){" +
                        "l=document.getElementsByClassName('download-button')[0];" +
                        "l.click();" +
                        "})()"
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        view!!.evaluateJavascript(js) {
                                s -> val result = s
                        }
                    }

                    override fun shouldInterceptRequest(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): WebResourceResponse? {
                        val url = request!!.url.toString()
                        if (url.contains("download.xn--41a.wiki/download_")) {
                            Log.d(TAG, url)
                            directDownloadFileLink = url
                        }
                        return super.shouldInterceptRequest(view, request)
                    }

                }
                loadUrl(fullUrl)
            }
        }

    private suspend fun fetchSong(url: String): String? =
        withContext(Dispatchers.IO) {
        //register receiver on download completed
        val sdfsdds = url
        val sdfsdsss = url
        var downloaded = false
        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(ctxt: Context, intent: Intent) {
                downloaded = true
            }
        }
        context.registerReceiver(onComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        //ready steady go
        val vkDownloadFile = VkDownloadFile(song)
        Log.d(TAG, "To path: ${vkDownloadFile.fullPath}")
        //build
        val req = DownloadManager.Request(Uri.parse(url))
            .setTitle(vkDownloadFile.name)
            .setDescription("Downloading")
            .setVisibleInDownloadsUi(true)
            .setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationUri(vkDownloadFile.uriFromFile)
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or
                    DownloadManager.Request.NETWORK_MOBILE)
            .setAllowedOverRoaming(false)
        //download
        val downloadManager = context
            .getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        // enqueue puts the download request in the queue.
        downloadManager.enqueue(req)
        //wait 10 sec to download
        repeat(15) {
            if (downloaded.not()) delay(1000)
            else return@repeat
        }
        //unregister
        context.unregisterReceiver(onComplete)
        //if download is not success
        if (downloaded.not()) {
            return@withContext null
        }

        return@withContext vkDownloadFile.fullPath
    }

    private suspend fun filterSearchResultByUrl(url: String)
            : List<String> = withContext(Dispatchers.IO) {
        val doc: Document = Jsoup.connect(url).get()
        val links: Elements = doc.select("a")
        val urlStr = links.map {  it.attr("href") }
        return@withContext urlStr
            .filter {
                it.contains("/public/download_song.php?id=")
            }
            .filter { it.isNotBlank() }
    }

    private suspend fun fetchSearchLink(directSearchLink: String): List<String> =
        filterSearchResultBySong(filterSearchResultByUrl(directSearchLink))

    private suspend fun filterSearchResultBySong(url: List<String>): List<String> =
        withContext(Dispatchers.IO) {
            //site in links contain only artist not like a hashcode
            val onlyAlphabetAndDigit = Regex("[^a-z0-9]]")
            val transliterateArtist = song.artist.transliterate()
            val alphaArtist = onlyAlphabetAndDigit
                .replace(transliterateArtist, "")
                .split(' ')
            return@withContext url
                .filter { url ->
                    var cont = false
                    alphaArtist.forEach {
                        if (url.contains(it)) cont = true
                    }
                    cont
                }
                .filter { it.isNotBlank() }
        }

    private suspend fun getSearchLinkList(): List<String> {
        //intercept link
        interceptDirectSearchLink()
        //wait
        while (directSearchLink.isBlank())
            delay(500)
        //all filtered link by search result
        return fetchSearchLink(directSearchLink)
    }

    private fun createDownloadFileLink(link: String): String =
        "https://pmusic.я.wiki$link".replace("id", "link")

    suspend fun download(): String? {
        val searchLinkList = getSearchLinkList()
        //what we should do in next step ->
        //download or return
        if (searchLinkList.isNotEmpty()) {
            val downloadLink = createDownloadFileLink(searchLinkList.first())
            interceptDirectFileLink(downloadLink)

            repeat(15) {
                if (directDownloadFileLink == "") delay(1000)
                else return@repeat
            }
            if (directDownloadFileLink == "") return null

            return fetchSong(directDownloadFileLink)
        }
        return null
    }
}