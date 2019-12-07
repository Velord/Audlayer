package velord.university.ui.fragment

import android.content.Context
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
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

    abstract val pressedButton: ImageButton
    abstract val newBackground: Int

    protected lateinit var folderImageBt: ImageButton
    protected lateinit var albumImageBt: ImageButton
    protected lateinit var songImageBt: ImageButton
    protected lateinit var radioImageBt: ImageButton
    protected lateinit var vkImageBt: ImageButton

    protected lateinit var folderTextBt: TextView
    protected lateinit var albumTextBt: TextView
    protected lateinit var songTextBt: TextView
    protected lateinit var radioTextBt: TextView
    protected lateinit var vkTextBt: TextView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callback?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    protected fun initView(view: View) {
        initMenuButtons(view)
        changeBackgroundPressedButton()
    }

    private fun initMenuButtons(view: View) {
        initMenuImageButtons(view)
        initMenuTextButtons(view)
    }

    private fun initMenuTextButtons(view: View) {
        folderTextBt = view.findViewById(R.id.folder_textView)
        albumTextBt = view.findViewById(R.id.album_textView)
        songTextBt = view.findViewById(R.id.song_textView)
        radioTextBt = view.findViewById(R.id.radio_textView)
        vkTextBt = view.findViewById(R.id.vk_textView)

        folderTextBt.setOnClickListener { callbacks?.openFolderFragment() }
        albumTextBt.setOnClickListener { callbacks?.openAlbumFragment() }
        songTextBt.setOnClickListener { callbacks?.openSongFragment() }
        radioTextBt.setOnClickListener { callbacks?.openRadioFragment() }
        vkTextBt.setOnClickListener { callbacks?.openVKFragment() }
    }

    private fun initMenuImageButtons(view: View) {
        folderImageBt = view.findViewById(R.id.folder)
        albumImageBt = view.findViewById(R.id.album)
        songImageBt = view.findViewById(R.id.song)
        radioImageBt = view.findViewById(R.id.radio)
        vkImageBt = view.findViewById(R.id.vk)

        folderImageBt.setOnClickListener { callbacks?.openFolderFragment() }
        albumImageBt.setOnClickListener { callbacks?.openAlbumFragment() }
        songImageBt.setOnClickListener { callbacks?.openSongFragment() }
        radioImageBt.setOnClickListener { callbacks?.openRadioFragment() }
        vkImageBt.setOnClickListener { callbacks?.openVKFragment() }
    }

    private fun changeBackgroundPressedButton() {
        pressedButton.setImageResource(newBackground)
    }
}