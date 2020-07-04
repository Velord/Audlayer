package velord.university.ui.fragment.addToPlaylist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import kotlinx.coroutines.*
import velord.university.R
import velord.university.application.broadcast.AppBroadcastHub
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.entity.Playlist
import velord.university.model.entity.Song
import velord.university.repository.transaction.PlaylistTransaction
import velord.university.ui.backPressed.BackPressedHandlerSecond
import velord.university.ui.fragment.selfLifecycle.LoggerSelfLifecycleFragment
import velord.university.ui.util.setupAndShowPopupMenuOnClick
import java.io.File

class AddToPlaylist :
    LoggerSelfLifecycleFragment(),
    BackPressedHandlerSecond {
    //Required interface for hosting activities
    interface Callbacks {
        fun openCreateNewPlaylistDialogFragment()

        fun closeAddToPlaylistFragment()
    }
    private var callbacks: Callbacks? =  null

    override val TAG: String = "AddToPlaylistFragment"

    companion object {
        fun newInstance() = SelectSongFragment()
    }

    private val scope = CoroutineScope(Job() + Dispatchers.Default)

    private lateinit var layoutCS: ConstraintLayout
    private lateinit var rv: RecyclerView
    private lateinit var createNew: Button

    private val songsToPlaylist = SongPlaylistInteractor.songsPath

    override fun onBackPressed(): Boolean {
        Log.d(TAG, "onBackPressed")
        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.add_to_playlist_fragment,
            container, false).apply {
            initViews(this)
            setupAdapter()
        }
    }

    private fun initViews(view: View) {
        initCreateNew(view)
        initRV(view)
        initCS(view)
    }

    private fun initCS(view: View) {
        layoutCS = view.findViewById(R.id.add_to_playlist_CS)
        //if you now cause this lambda is blank -> you cool
        layoutCS.setOnClickListener {  }
    }

    private fun initCreateNew(view: View) {
        createNew = view.findViewById(R.id.add_to_playlist_create_new)
        createNew.setOnClickListener {
            callbacks?.openCreateNewPlaylistDialogFragment()
        }
    }

    private fun initRV(view: View) {
        rv = view.findViewById(R.id.general_RecyclerView)
        rv.layoutManager = LinearLayoutManager(activity)
    }

    private fun setupAdapter() {
        scope.launch {
            val playlist = Playlist.otherAndFavourite(
                PlaylistTransaction.getAllPlaylist()).toTypedArray()

            withContext(Dispatchers.Main) {
                rv.adapter = PlaylistAdapter(playlist)
            }
        }
    }

    private inner class PlaylistHolder(itemView: View):
        RecyclerView.ViewHolder(itemView) {

        private val pathTextView: TextView = itemView.findViewById(R.id.general_item_path)
        private val actionImageButton: ImageButton = itemView.findViewById(R.id.general_action_ImageButton)
        private val actionFrame: FrameLayout = itemView.findViewById(R.id.general_action_frame)

        private fun updatePlaylist(playlist: Playlist) {
                val filtered = songsToPlaylist.filter {
                    playlist.songs.contains(it).not()
                }
                //update db
                scope.launch {
                    PlaylistTransaction.update(playlist)
                }
                //show user info
                Toast.makeText(
                    requireContext(),
                    "Songs added: ${filtered.size}",
                    Toast.LENGTH_SHORT
                ).show()
                //back pressed
                callbacks?.closeAddToPlaylistFragment()
        }

        private val actionPopUpMenu: (Playlist) -> Unit = { playlist ->
            val initActionMenuStyle = { R.style.PopupMenuOverlapAnchorFolder }
            val initActionMenuLayout = { R.menu.add_to_playlist_pop_up }
            val initActionMenuItemClickListener: (MenuItem) -> Boolean = {menuItem ->
                when (menuItem.itemId) {
                    R.id.playlist_item_play -> {
                        //don't remember for SongPlaylist Interactor
                        SongPlaylistInteractor.songs =
                            playlist.songs
                                .map { Song(File(it)) }
                                .toTypedArray()

                        AppBroadcastHub.apply {
                            requireContext().playByPathService(playlist.songs[0])
                        }
                        true
                    }
                    R.id.playlist_item_add_to_home_screen -> {
                        TODO()
                        true
                    }
                    R.id.playlist_item_delete -> {
                        scope.launch {
                            PlaylistTransaction.delete(playlist.id)
                            withContext(Dispatchers.Main) {
                                setupAdapter()
                            }
                        }
                        true
                    }
                    else -> {
                        false
                    }
                }
            }

            actionImageButton.setupAndShowPopupMenuOnClick(
                requireContext(),
                initActionMenuStyle,
                initActionMenuLayout,
                initActionMenuItemClickListener
            )
            Unit
        }

        private fun setOnClickAndImageResource(playlist: Playlist) {
            itemView.setOnClickListener {
                updatePlaylist(playlist)
            }
            pathTextView.setOnClickListener {
                updatePlaylist(playlist)
            }
            actionImageButton.setOnClickListener {
                actionPopUpMenu(playlist)
            }
            actionFrame.setOnClickListener {
                actionPopUpMenu(playlist)
            }
        }

        fun bindItem(playlist: Playlist, position: Int) {
            setOnClickAndImageResource(playlist)

            pathTextView.text = getString(R.string.add_to_playlist_item,
                playlist.name, playlist.songs.size)
        }
    }

    private inner class PlaylistAdapter(val items: Array<out Playlist>):
        RecyclerView.Adapter<PlaylistHolder>(),  FastScrollRecyclerView.SectionedAdapter {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(
                R.layout.general_rv_item, parent, false
            )

            return PlaylistHolder(view)
        }

        override fun onBindViewHolder(holder: PlaylistHolder, position: Int) {
            items[position].apply {
                holder.bindItem(this, position)
            }
        }

        override fun getItemCount(): Int = items.size

        override fun getSectionName(position: Int): String =
            "${items[position].name[0]}"
    }
}