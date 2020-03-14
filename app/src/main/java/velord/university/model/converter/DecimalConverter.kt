package velord.university.model.converter

import java.math.RoundingMode
import java.text.DecimalFormat

fun roundOfDecimalToUp(number: Double): Double {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.CEILING
    return df.format(number).toDouble()
}

fun roundOfDecimalToDown(number: Double): Double? {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.FLOOR
    return df.format(number).toDouble()
}