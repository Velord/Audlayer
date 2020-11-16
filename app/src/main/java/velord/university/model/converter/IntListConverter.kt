package velord.university.model.converter

import androidx.room.TypeConverter

object IntListConverter {

    @TypeConverter
    @JvmStatic
    fun fromString(stringListString: String): List<Int>  =
        stringListString.split("@#$%").map { it.toInt() }

    @TypeConverter
    @JvmStatic
    fun toString(stringList: List<Int>): String =
        stringList.joinToString(separator = "@#$%")
}