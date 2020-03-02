package velord.university.model.converter

import androidx.room.TypeConverter

object StringListConverter {

    @TypeConverter
    @JvmStatic
    fun fromString(stringListString: String): List<String>  =
        stringListString.split("@#$%").map { it }

    @TypeConverter
    @JvmStatic
    fun toString(stringList: List<String>): String =
        stringList.joinToString(separator = "@#$%")
}