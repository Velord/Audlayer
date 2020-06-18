package velord.university.ui.util

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import velord.university.R

object DrawableIcon {

    private val defaultSongIcon = arrayOf(
        "song_guitar_room",
        "song_woman",
        "song_wall",
        "song_piano_headphones",
        "song_piano_guitar",
        "song_piano",
        "song_piano_2",
        "song_microphone_drum",
        "song_microphone",
        "song_microphone_2",
        "song_man_guitar",
        "song_man",
        "song_man_2",
        "song_headphones",
        "song_headphones_2",
        "song_guitar_sofa",
        "song_guitar",
        "song_gramofon",
        "song_gramofon_2",
        "song_gramofon_3",
        "song_drum",
        "song_dj",
        "song_dj_2",
        "song_cassette",
        "song_book_headphones",
        "song_book",
        "song_book_2"
    )

    private val folderSongIcon = arrayOf(
        "song_item_black",
        "song_item_red",
        "song_item_purple",
        "song_item_light_green",
        "song_item_gold",
        "song_item_cyan",
        "song_item_blue"
    )

    private inline fun loadIcon(context: Context,
                                getIcon: (Context) -> Int,
                                view: ImageView,
                                defaultIcon: Int) {

        val image = getIcon(context)

        Glide.with(context)
            .load(image)
            .placeholder(defaultIcon)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(view)
    }


    private val getDrawableIcon: (Context, String)
    -> Int = { context, iconName ->

        context.resources.getIdentifier(
            iconName, "drawable", context.packageName
        )
    }

    private val getIconByName: (Context, String)
    -> ((Context) -> Int) = { context, iconName ->

        {
            getDrawableIcon(
                context,
                iconName
            )
        }
    }

    private val getRandomDefaultSongIcon: (Context)
    -> Int = { context ->

        getDrawableIcon(
            context,
            getRandomSongIconName()
        )
    }


    fun loadRadioIconByName(context: Context,
                            view: ImageView,
                            nameIcon: String) =
        loadIcon(
            context,
            getIconByName(
                context,
                nameIcon
            ),
            view,
            R.drawable.radio_record
        )


    val getRandomSongIconName: () -> String = {
        defaultSongIcon.random()
    }

    val getRandomFolderSongIconName: () -> String = {
        folderSongIcon.random()
    }

    fun loadSongIconByName(context: Context,
                           view: ImageView,
                           nameIcon: String) =
        loadIcon(
            context,
            getIconByName(
                context,
                nameIcon
            ),
            view,
            R.drawable.song_item_black
        )

    fun loadRandomSongIcon(context: Context,
                           view: ImageView) =
        loadIcon(
            context,
            getRandomDefaultSongIcon,
            view,
            R.drawable.song_item_black
        )
}