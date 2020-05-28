package velord.university.repository.fetch

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import velord.university.model.converter.transliterate
import velord.university.model.entity.vk.VkSong
import java.io.File

//https://imusic.я.wiki
data class IMusicFetch(private val context: Context,
                       val webView: WebView,
                       val song: VkSong) {

    private val TAG = "SefonFetchSequential"
    private var directSearchLink: String = ""

    @SuppressLint("SetJavaScriptEnabled")
    private suspend fun fetchDirectSearchLink() =
        withContext(Dispatchers.Main) {
            val searchLink = "https://imusic.я.wiki/search/"
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

    private suspend fun fetchSong(url: String): File = withContext(Dispatchers.IO) {
        val ext = ".mp3"
        val name = "${song.artist} - ${song.title}"
        val vkDir = "${Environment.getExternalStorageDirectory().path}/Audlayer/Vk"
        val downloadedFile = File(vkDir, "$name$ext")

        val req = DownloadManager.Request(Uri.parse(url))
            .setTitle(name)
            .setDescription("Downloading")
            .setVisibleInDownloadsUi(true)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setDestinationUri(Uri.fromFile(downloadedFile))
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setAllowedOverRoaming(false)


        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(req) // enqueue puts the download request in the queue.
        return@withContext downloadedFile
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

    suspend fun getSearchLinkList(): List<String> {
        //intercept link
        fetchDirectSearchLink()
        //wait
        while (directSearchLink.isBlank())
            delay(500)
        //all filtered link by search result
        return fetchSearchLink(directSearchLink)
    }

    private fun createDirectFileLink(link: String): String =
        "https://imusic.xn--41a.wiki/$link"

    suspend fun downloadFileByLink(link: String): File =
        fetchSong(createDirectFileLink(link))


    suspend fun download(): File? {
        val searchLinkList = getSearchLinkList()
        //what we should do in next step ->
        //download or return
        if (searchLinkList.isNotEmpty()) {
            return downloadFileByLink(searchLinkList[0])
        }
        return null
    }
}