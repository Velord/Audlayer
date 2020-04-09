package velord.university.repository.fetch

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import velord.university.model.converter.transliterate
import velord.university.model.entity.vk.VkSong
import java.io.File
import java.util.*


data class SefonFetchAsync(private val context: Context,
                           val webView: WebView,
                           val song: VkSong,
                           val successF: (File) -> Unit) {

    private val TAG = "SefonFetchAsync"

    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    @SuppressLint("SetJavaScriptEnabled")
    suspend fun getDirectFileLink(url: String) =
        withContext(Dispatchers.Main) {
        val fullUrl = "https:/sefon.pro$url"
        webView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            val js = "javascript:(function(){" +
                    "l=document.getElementsByClassName('b_btn download no-ajix')[0];" +
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
                    if (url.contains("sefon.pro/api/mp3_download/direct")) {
                        Log.d(TAG, url)
                        scope.launch {
                            val file = downloadSong(url, song.artist, song.title)
                            successF(file)
                        }
                    }
                    return super.shouldInterceptRequest(view, request)
                }

            }
            loadUrl(fullUrl)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    suspend fun getDirectSearchLink() =
        withContext(Dispatchers.Main) {
        val withoutRemix = song.title.substringBefore('(')
        val fullUrl = "https://sefon.pro/search/?q=${song.artist} $withoutRemix"
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
                    if (directUrl.contains("/search/?q=")) {
                        scope.launch {
                            val links = filterSearchResult(
                                fetchSearchResult(directUrl))
                            if (links.isNotEmpty())
                                getDirectFileLink(links[0])
                            else withContext(Dispatchers.Main) {
                                Toast.makeText(context,
                                    "Sorry we did not found any link", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    return super.shouldInterceptRequest(view, request)
                }

            }
            loadUrl(fullUrl)
        }
    }

    suspend fun downloadSong(url: String,
                             artist: String,
                             title: String): File = withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .build()
        val response = client.newCall(request).execute()

        val ext = ".mp3"
        val vkDir = "${Environment.getExternalStorageDirectory().path}/Audlayer/Vk"
        val downloadedFile = File(vkDir, "$artist - $title$ext")
        val sink = downloadedFile.sink().buffer()
        sink.writeAll(response.body!!.source())
        sink.close()

        return@withContext downloadedFile
    }

    suspend fun download() {
        getDirectSearchLink()
    }

    suspend fun filterSearchResult(url: List<String>): List<String> =
        withContext(Dispatchers.IO) {
        val onlyAlphabetAndDigit = Regex("[^a-z0-9]]")
        val lowerTitle = song.title.transliterate().toLowerCase(Locale.ROOT)
        val lowerArtist = song.artist.transliterate().toLowerCase(Locale.ROOT)
        val alphaTitle = onlyAlphabetAndDigit
            .replace(lowerTitle, "")
            .split(' ')
        val alphaArtist = onlyAlphabetAndDigit
            .replace(lowerArtist, "")
            .split(' ')
        return@withContext url
            .filter { url ->
                var cont = false
                alphaTitle.forEach {
                    if (url.contains(it)) cont = true
                }
                cont
            }
            .filter { url ->
                var cont = false
                alphaArtist.forEach {
                    if (url.contains(it)) cont = true
                }
                cont
            }
            .filter { it.isNotBlank() }
    }

    suspend fun fetchSearchResult(url: String): List<String> =
        withContext(Dispatchers.IO) {
        val doc: Document = Jsoup.connect(url).get()
        val links: Elements = doc.select("a")
        val urlStr = links.map {  it.attr("href") }
        return@withContext urlStr
            .filter {
                it.contains("/mp3/")
            }
            .filter { it.isNotBlank() }
    }
}

data class SefonFetchSequential(private val context: Context,
                                val webView: WebView,
                                val song: VkSong) {

    private val TAG = "SefonFetchSequential"

    private var directSearchLink: String = ""
    private var directFileLink: String = ""

    @SuppressLint("SetJavaScriptEnabled")
    private suspend fun fetchDirectFileLink(url: String) =
        withContext(Dispatchers.Main) {
        val fullUrl = "https:/sefon.pro$url"
        webView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            val js = "javascript:(function(){" +
                    "l=document.getElementsByClassName('b_btn download no-ajix')[0];" +
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
                    if (url.contains("sefon.pro/api/mp3_download/direct")) {
                        Log.d(TAG, url)
                        directFileLink = url
                    }
                    return super.shouldInterceptRequest(view, request)
                }

            }
            loadUrl(fullUrl)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private suspend fun fetchDirectSearchLink() =
        withContext(Dispatchers.Main) {
        val withoutRemix = song.title.substringBefore('(')
        val fullUrl = "https://sefon.pro/search/?q=${song.artist} $withoutRemix"
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
                    if (directUrl.contains("/search/?q=")) {
                        directSearchLink = directUrl
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


        val downloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadManager!!.enqueue(req) // enqueue puts the download request in the queue.
        return@withContext downloadedFile
    }

    private suspend fun fetchSearchResult(url: String)
            : List<String> = withContext(Dispatchers.IO) {
        val doc: Document = Jsoup.connect(url).get()
        val links: Elements = doc.select("a")
        val urlStr = links.map {  it.attr("href") }
        return@withContext urlStr
            .filter {
                it.contains("/mp3/")
            }
            .filter { it.isNotBlank() }
    }

    private suspend fun fetchSearch(directSearchLink: String): List<String> =
        filterSearchResult(fetchSearchResult(directSearchLink))

    private suspend fun filterSearchResult(url: List<String>): List<String> =
        withContext(Dispatchers.IO) {
            val onlyAlphabetAndDigit = Regex("[^a-z0-9]]")
            val lowerTitle = song.title
                .transliterate()
                .toLowerCase(Locale.ROOT)
            val lowerArtist = song.artist
                .transliterate()
                .toLowerCase(Locale.ROOT)
            val alphaTitle = onlyAlphabetAndDigit
                .replace(lowerTitle, "")
                .split(' ')
            val alphaArtist = onlyAlphabetAndDigit
                .replace(lowerArtist, "")
                .split(' ')
            return@withContext url
                .filter { url ->
                    var cont = false
                    alphaTitle.forEach {
                        if (url.contains(it)) cont = true
                    }
                    cont
                }
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
        return fetchSearch(directSearchLink)
    }

    suspend fun downloadFileByLink(link: String): File {
        //intercept link
        fetchDirectFileLink(link)
        //wait
        while (directFileLink.isBlank())
            delay(500)
        //download
        return fetchSong(directFileLink)
    }

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