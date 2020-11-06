package velord.university.model.converter

object VolumeConverter {

    fun tenthScale(volume: Int): Int =
        when(volume) {
            0 -> 10
            1 -> 20
            2 -> 30
            3 -> 40
            4 -> 50
            5 -> 60
            6 -> 70
            7 -> 80
            8 -> 90
            9 -> 100
            else -> 100
        }

    fun fifteenthScale(volume: Int): Int =
        when(volume) {
            0 -> 0
            1 -> 7
            2 -> 13
            3 -> 20
            4 -> 27
            5 -> 33
            6 -> 40
            7 -> 47
            8 -> 53
            9 -> 60
            10 -> 67
            11 -> 73
            12 -> 80
            14 -> 87
            13 -> 93
            15 -> 100
            else -> 100
        }
}