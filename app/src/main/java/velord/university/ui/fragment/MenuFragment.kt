package velord.university.ui.fragment

import android.content.Context
import android.view.View
import android.widget.ImageButton
import velord.university.R

abstract class MenuFragment : LoggerSelfLifecycleFragment() {

    override val TAG: String
        get() = "MenuFragment"

    interface Callback {

        fun openAlbumFragment()

        fun openFolderFragment()

        fun openSongFragment()

        fun openRadioFragment()

        fun openVKFragment()
    }

    protected var callbacks: Callback? =  null

    protected lateinit var folderBt: ImageButton
    protected lateinit var albumBt: ImageButton
    protected lateinit var songBt: ImageButton
    protected lateinit var radioBt: ImageButton
    protected lateinit var vkBt: ImageButton

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callback?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    protected fun initMenuButtons(view: View) {
        folderBt = view.findViewById(R.id.folder)
        albumBt = view.findViewById(R.id.album)
        songBt = view.findViewById(R.id.songs)
        radioBt = view.findViewById(R.id.radio)
        vkBt = view.findViewById(R.id.vk)

        folderBt.setOnClickListener { callbacks?.openFolderFragment() }
        albumBt.setOnClickListener { callbacks?.openAlbumFragment() }
        songBt.setOnClickListener { callbacks?.openSongFragment() }
        radioBt.setOnClickListener { callbacks?.openRadioFragment() }
        vkBt.setOnClickListener { callbacks?.openVKFragment() }
    }
}