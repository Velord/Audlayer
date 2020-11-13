package velord.university.model.entity.music.song.download

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DownloadSong(
    val artist: String,
    val title: String,
    val path: String
) : Parcelable