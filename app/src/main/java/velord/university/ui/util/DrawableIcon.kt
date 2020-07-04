package velord.university.ui.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import velord.university.R

object DrawableIcon {

    private val defaultSongIcon = arrayOf(
        R.drawable.song_guitar_room,
        R.drawable.song_woman,
        R.drawable.song_wall,
        R.drawable.song_piano_headphones,
        R.drawable.song_piano_guitar,
        R.drawable.song_piano,
        R.drawable.song_piano_2,
        R.drawable.song_microphone_drum,
        R.drawable.song_microphone,
        R.drawable.song_microphone_2,
        R.drawable.song_man_guitar,
        R.drawable.song_man,
        R.drawable.song_man_2,
        R.drawable.song_headphones,
        R.drawable.song_headphones_2,
        R.drawable.song_guitar_sofa,
        R.drawable.song_guitar,
        R.drawable.song_gramofon,
        R.drawable.song_gramofon_2,
        R.drawable.song_gramofon_3,
        R.drawable.song_drum,
        R.drawable.song_dj,
        R.drawable.song_dj_2,
        R.drawable.song_cassette,
        R.drawable.song_book_headphones,
        R.drawable.song_book,
        R.drawable.song_book_2
    )

    private val folderSongIcon = arrayOf(
        R.drawable.song_item_black,
        R.drawable.song_item_red,
        R.drawable.song_item_purple,
        R.drawable.song_item_light_green,
        R.drawable.song_item_green,
        R.drawable.song_item_gold,
        R.drawable.song_item_cyan,
        R.drawable.song_item_blue
    )

    private inline fun <T> loadIcon(context: Context,
                                    icon: T,
                                    view: ImageView,
                                    defaultIcon: Int) {

        Glide.with(context)
            .load(icon)
            .placeholder(defaultIcon)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(view)
    }


    val getResourceIdIcon: (Context, String)
    -> Int = { context, iconName ->

        context.resources.getIdentifier(
            iconName, "drawable", context.packageName
        )
    }

    private val getDrawableIcon: (Context, Int)
    -> Drawable = { context, iconName ->

        context.resources.getDrawable(iconName)
    }


    private val getRandomDefaultSongIcon: (Context)
    -> Drawable = { context ->

        getDrawableIcon(
            context,
            getRandomSongIconName()
        )
    }

    val getRandomSongIconName: () -> Int = {
        defaultSongIcon.random()
    }

    val getRandomFolderSongIconName: () -> Int = {
        folderSongIcon.random()
    }

    fun loadRadioIconDrawable(context: Context,
                              view: ImageView,
                              nameIcon: Int = R.drawable.radio_record,
                              defaultIcon: Int = R.drawable.radio_record) =
        loadIcon(
            context,
            nameIcon,
            view,
            defaultIcon
        )

    fun loadRadioIconAsset(context: Context,
                           view: ImageView,
                           nameIcon: String?,
                           defaultIcon: Int = R.drawable.radio_record) {

        val icon = nameIcon ?: "radio_record"
        val iconId = getResourceIdIcon(context, icon)

        loadIcon(
            context,
            iconId,
            view,
            defaultIcon
        )
    }

    fun loadSongIconDrawable(context: Context,
                             view: ImageView,
                             nameIcon: Int) =
        loadIcon(
            context,
            nameIcon,
            view,
            R.drawable.song_item_black
        )

    fun loadRandomSongIcon(context: Context,
                           view: ImageView) =
        loadIcon(
            context,
            getRandomDefaultSongIcon(context),
            view,
            R.drawable.song_item_black
        )
}