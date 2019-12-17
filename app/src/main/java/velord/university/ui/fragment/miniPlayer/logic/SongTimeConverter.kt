package velord.university.ui.fragment.miniPlayer.logic

import android.widget.TextView

object SongTimeConverter {

    private fun textToSeconds(view: TextView): Int {
        val text = view.text
        var min = 0
        var sec = 0
        if (text[0] == '0') {
            min = text[1].toString().toInt()

            if (text[4] == '0') sec = text[5].toString().toInt()
            sec = (text[4].toString() + text[5]).toInt()
        } else {
            min = (text[0].toString() + text[1]).toInt()

            if (text[4] == '0') sec = text[5].toString().toInt()
            sec = (text[4].toString() + text[5]).toInt()
        }

        sec += min * 60

        return sec
    }

    fun secondsToTimeText(sec: Int): String {
        val secToView = (sec % 60)
        val minToView = (sec / 60)
        return when(secToView) {
            in 0..10 -> {
                if (minToView in 0..9) "0$minToView: 0$secToView"
                else "$minToView: 0$secToView"
            }
            else -> {
                if (minToView in 0..9) "0$minToView: $secToView"
                else "$minToView: $secToView"
            }
        }
    }

    fun percentToSongTimeText(value: Int, view: TextView): String {
        val secondsInSong = textToSeconds(view)
        val onePercent = (secondsInSong.toFloat() / 100.0)
        val sec = (value.toFloat() * onePercent).toInt()
        return secondsToTimeText(sec)
    }
}