package velord.university.repository.fetch

import android.util.Log
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

abstract class FetchJson {

    protected val TAG = "FetchWeb"

    protected inline fun <reified T> makeResponse(
        url: String,
        dataPart: Array<MultipartBody.Part>,
    ): T {
        //create request body
        val requestBody = createRequestBodyWithDataPart(*dataPart)
        //create request
        val request = createRequest(url, requestBody)
        //make response
        val bodyString = makeOkHttpResponse(request)
        Log.d(TAG, bodyString)
        //retrieve Json
        return bodyString.deserializeJson()
    }

    protected inline fun <reified T> String.deserializeJson(): T =
        Json.decodeFromString(this)

    protected fun createRequestBodyWithDataPart(
        vararg formDataPart: MultipartBody.Part
    ): MultipartBody {
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)

        formDataPart.forEach {
            requestBody.addPart(it)
        }

        return requestBody.build()
    }

    protected fun createRequest(
        url: String,
        requestBody: RequestBody
    ): Request = Request.Builder()
        .url(url)
        .post(requestBody)
        .build()

    protected fun makeOkHttpResponse(request: Request): String {
        val client = OkHttpClient.Builder()
            .callTimeout(10, TimeUnit.SECONDS)
            .build()

        return client.newCall(request).execute().use { response ->
            if (response.isSuccessful.not())
                throw IOException("Unexpected code $response")
            response.use { it.body!!.string() }
        }
    }

}