package velord.university.model.entity.fileType.json.fetch

import com.statuscasellc.statuscase.model.json.general.FromJson
import kotlinx.serialization.Serializable

@Serializable
data class FetchEntity <T> (
    val entity: T? = null,
    //can't build if made this property private
    //hate kapt
    val error: Array<String>? = null
) {

    //standard handle
    inline fun <reified F> handle(): F =
        if (error != null && error.isNotEmpty())
            error(error.joinToString("\n"))
        else entity as F

    //if need convert Json object to specific type
    inline fun <reified F> handleFromJson(): F =
        if (error != null && error.isNotEmpty())
            error(error.joinToString("\n"))
        else (entity as FromJson<T, F>).fromJson(entity)
}