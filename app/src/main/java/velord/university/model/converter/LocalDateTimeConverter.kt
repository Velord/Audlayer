package velord.university.model.converter

import androidx.room.TypeConverter
import java.time.LocalDateTime

object LocalDateTimeConverter {

    @TypeConverter
    @JvmStatic
    fun toDate(dateString: String?): LocalDateTime? =
        if (dateString == null) null
        else LocalDateTime.parse(dateString)

    @TypeConverter
    @JvmStatic
    fun toDateString(date: LocalDateTime?): String? =
        if (date == null) null
        else date.toString()
}