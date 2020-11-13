package velord.university.ui.fragment.addToPlaylist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import velord.university.model.coroutine.getScope
import velord.university.model.coroutine.onMain
import velord.university.model.exception.ViewDestroyed
import velord.university.ui.util.view.deactivate
import velord.university.ui.util.view.setupAndShowPopupMenuOnClick
import kotlinx.coroutines.*
import velord.university.R
import velord.university.application.broadcast.hub.AppBroadcastHub
import velord.university.databinding.AddToPlaylistFragmentBinding
import velord.university.databinding.GeneralRvBinding
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.entity.music.playlist.base.Playlist
import velord.university.model.entity.music.song.Song
import velord.university.repository.db.transaction.PlaylistTransaction
import velord.university.repository.db.transaction.hub.HubTransaction
import velord.university.ui.behaviour.backPressed.BackPressedHandlerSecond
import velord.university.ui.fragment.addToPlaylist.select.SelectSongFragment
import velord.university.ui.fragment.selfLifecycle.LoggerSelfLifecycleFragment
import java.io.File

class AddToPlaylistFragment :
    LoggerSelfLifecycleFragment(),
    BackPressedHandlerSecond {
    //Required interface for hosting activities
    interface Callbacks {

        fun openCreateNewPlaylistDialogFragment()

        fun toZeroLevel()
    }
    private var callbacks: Callbacks? =  null

    override val TAG: String = "AddToPlaylistFragment"

    companion object {
        fun newInstance() = SelectSongFragment()
    }

    private val scope = getScope()

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

        scope.cancel()
    }

    //view
    private var _binding: AddToPlaylistFragmentBinding? = null
    private var _bindingRv: GeneralRvBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding ?:
    throw ViewDestroyed("Don't touch view when it is destroyed")
    private val bindingRv get() = _bindingRv ?:
    throw ViewDestroyed("Don't touch view when it is destroyed")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(
        R.layout.add_to_playlist_fragment,
        container, false).run {
        //bind
        _binding = AddToPlaylistFragmentBinding.bind(this)
        _bindingRv = binding.generalRvInclude
        scope.launch {
            onMain {
                initViews()
                setupAdapter()
            }
        }

        binding.root
    }

    private fun initViews() {
        binding.addToPlaylistContainer.deactivate()
        binding.createNew.setOnClickListener {
            callbacks?.openCreateNewPlaylistDialogFragment()
        }
        bindingRv.fastScrollRv.layoutManager =
            LinearLayoutManager(requireActivity())
    }

    private fun setupAdapter() {
        scope.launch {
            val playlist = Playlist.otherAndFavourite(
                PlaylistTransaction.getAllPlaylist()).toTypedArray()

            onMain {
                bindingRv.fastScrollRv.adapter = PlaylistAdapter(playlist)
            }
        }
    }

    private inner class PlaylistHolder(itemView: View):
        RecyclerView.ViewHolder(itemView) {

        private val pathTextView: TextView =
            itemView.findViewById(R.id.general_item_path)
        private val actionImageButton: ImageButton =
            itemView.findViewById(R.id.general_action_ImageButton)
        private val actionFrame: FrameLayout =
            itemView.findViewById(R.id.general_action_frame)

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
                callbacks?.toZeroLevel()
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
                            HubTransaction.playlistTransaction("playlist_item_delete") {
                                deletePlaylistById(playlist.id)
                            }
                            onMain { setupAdapter() }
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