package velord.university.model.entity.fileType.json.fetch

import com.statuscasellc.statuscase.model.json.general.FromJson
import kotlinx.serialization.Serializable

@Serializable
data class FetchListEntity <T> (
    val entityList: Array<T>? = null,
    var error: Array<String>? = null
)  {

    //if need convert Json object to specific type
    inline fun <reified F> handleFromJson(): Array<F> =
        if (error != null && error!!.isNotEmpty())
            error(error!!.joinToString("\n"))
        else entityList!!.map {
            (it as FromJson<T, F>).fromJson(it)
        }.toTypedArray()

    //standard handle
    inline fun <reified F> handle(): Array<F> =
        if (error != null && error!!.isNotEmpty())
            error(error!!.joinToString("\n"))
        else entityList!!.map {
            (it as F)
        }.toTypedArray()
}