package velord.university.ui.fragment.addToPlaylist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import kotlinx.coroutines.*
import velord.university.R
import velord.university.application.AudlayerApp
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.entity.Playlist
import velord.university.ui.BackPressedHandlerSecond
import velord.university.ui.fragment.LoggerSelfLifecycleFragment

class AddToPlaylist : LoggerSelfLifecycleFragment(),  BackPressedHandlerSecond {
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
        return inflater.inflate(R.layout.add_to_playlist_fragment, container, false).apply {
            initViews(this)
            setupAdapter()
        }
    }

    private fun initViews(view: View) {
        initCreateNew(view)
        initRV(view)
    }

    private fun initCreateNew(view: View) {
        createNew = view.findViewById(R.id.add_to_playlist_create_new)
        createNew.setOnClickListener {
            callbacks?.let {
                it.openCreateNewPlaylistDialogFragment()
            }
        }
    }

    private fun initRV(view: View) {
        rv = view.findViewById(R.id.current_folder_RecyclerView)
        rv.layoutManager = LinearLayoutManager(activity)
    }

    private fun setupAdapter() {
        scope.launch {
            val playlists =
                AudlayerApp.db?.let {
                    it.playlistDao().getAll()
                }?.toTypedArray() ?: arrayOf()

            withContext(Dispatchers.Main) {
                rv.adapter = PlaylistAdapter(playlists)
            }
        }
    }

    private inner class PlaylistHolder(itemView: View):
        RecyclerView.ViewHolder(itemView) {

        private val pathTextView: TextView = itemView.findViewById(R.id.add_to_playlist_item_name)

        private fun updatePlaylist(playlist: Playlist) {
            AudlayerApp.db?.let {
                //update db
                scope.launch {
                    playlist.songs += songsToPlaylist
                    it.playlistDao().update(playlist)
                }
                //show user info
                Toast.makeText(
                    requireContext(),
                    "Songs added: ${songsToPlaylist.size}",
                    Toast.LENGTH_SHORT
                ).show()
                //back pressed
                callbacks?.let {
                    it.closeAddToPlaylistFragment()
                }
            }
        }

        private fun setOnClickAndImageResource(playlist: Playlist) {
            itemView.setOnClickListener {
                updatePlaylist(playlist)
            }
            pathTextView.setOnClickListener {
                updatePlaylist(playlist)
            }
        }

        fun bindItem(playlist: Playlist, position: Int) {
            setOnClickAndImageResource(playlist)
            pathTextView.text = playlist.name
        }
    }

    private inner class PlaylistAdapter(val items: Array<out Playlist>):
        RecyclerView.Adapter<PlaylistHolder>(),  FastScrollRecyclerView.SectionedAdapter {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(
                R.layout.add_to_playlist_item, parent, false
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