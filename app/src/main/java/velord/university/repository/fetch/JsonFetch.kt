package velord.university.repository.fetch

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

fun String.makeRequestViaOkHttp(): String {
    val client = OkHttpClient()
    val mimeType = "application/json; charset=utf-8".toMediaType()
    val requestBody = "{}".toRequestBody(mimeType)
    val request = Request.Builder()
        .url(this)
        .post(requestBody)
        .build()

    return client.newCall(request).execute().body!!.string()
}