package velord.university.repository.fetch

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink
import org.apache.commons.text.similarity.LevenshteinDistance
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import velord.university.model.converter.transliterate
import velord.university.model.entity.vk.VkSong
import java.io.File
import java.util.*


object SefonFetch {

    val TAG = "SefonFetch"

    val scope = CoroutineScope(Job() + Dispatchers.IO)

    @SuppressLint("SetJavaScriptEnabled")
    suspend inline fun getDirectFileLink(
        context: Context,
        webView: WebView,
        url: String,
        song: VkSong,
        crossinline successF: (File) -> Unit
    ) = withContext(Dispatchers.Main) {
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
                            val file = downloadSong(context, url, song.artist, song.title)
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
    suspend inline fun getDirectSearchLink(
        context: Context,
        webView: WebView,
        song: VkSong,
        crossinline successF: (File) -> Unit) = withContext(Dispatchers.Main) {
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
                            val links = withContext(Dispatchers.Main) {
                                fetchSearchResult(directUrl)
                            }
                            if (links.isNotEmpty())
                                getDirectFileLink(context, webView, links[0], song, successF)
                        }
                    }
                    return super.shouldInterceptRequest(view, request)
                }

            }
            loadUrl(fullUrl)
        }
    }

    suspend fun downloadSong(context: Context, url: String,
                                     artist: String, title: String): File = withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .build()
        val response = client.newCall(request).execute()

        val downloadedFile = File(context.cacheDir, "$artist - $title")
        val sink = downloadedFile.sink().buffer()
        sink.writeAll(response.body!!.source())
        sink.close()

        return@withContext downloadedFile
    }

    suspend inline fun download(context: Context,
                                webView: WebView,
                                song: VkSong,
                                crossinline successF: (File) -> Unit) {
        getDirectSearchLink(context, webView, song, successF)
    }

    suspend fun filterSearchResult(url: List<String>,
                                   artist: String,
                                   title: String): List<String> = withContext(Dispatchers.IO) {
        val withoutRemix = title.substringBefore('(')
        val titleTrans = withoutRemix.transliterate()
        val artistTrans = artist.transliterate()
        return@withContext url
            .filter {
                val lower = artistTrans.toLowerCase(Locale.ROOT)
                val artistHref = it.substringAfter('-').substringBefore('-')
                val sdfds = artistHref
                val sdfdsd = artistHref
                val levenshtein = LevenshteinDistance().apply(lower, artistHref)
                levenshtein in 0..2
            }
            .filter {
                val lower = titleTrans.toLowerCase(Locale.ROOT)
                val titleHref = it.substringAfterLast('-')
                val levenshtein = LevenshteinDistance().apply(lower, titleHref)
                levenshtein in 0..2
            }
            .filter { it.isNotBlank() }
    }

    suspend fun fetchSearchResult(url: String)
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
}