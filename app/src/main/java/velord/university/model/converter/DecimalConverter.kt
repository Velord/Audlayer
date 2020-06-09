package velord.university.model.converter

import java.math.RoundingMode
import java.text.DecimalFormat

//i don't know but in android 8
//.format(number) return string with comma instead of dot
fun roundOfDecimalToUp(number: Double): Double {
    val df = DecimalFormat("0.00")
    df.roundingMode = RoundingMode.FLOOR
    val format: String = df
        .format(number)
        .replace(',', '.')
    return format.toDouble()
}

fun roundOfDecimalToDown(number: Double): Double? {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.FLOOR
    val format: String = df
        .format(number)
        .replace(',', '.')
    return format.toDouble()
}