package velord.university.model.converter

import androidx.room.TypeConverter

object IntListConverter {

    @TypeConverter
    @JvmStatic
    fun fromString(str: String): List<Int> =
        if (str.isEmpty()) listOf()
        else str.split("@#$%").map { it.toInt() }

    @TypeConverter
    @JvmStatic
    fun toString(str: List<Int>): String =
        str.joinToString(separator = "@#$%")
}