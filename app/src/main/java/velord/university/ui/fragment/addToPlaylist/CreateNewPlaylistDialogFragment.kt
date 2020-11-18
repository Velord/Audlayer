package velord.university.ui.fragment.addToPlaylist

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import velord.university.model.coroutine.getScope
import velord.university.model.coroutine.onMain
import velord.university.model.exception.ViewDestroyed
import velord.university.ui.util.activity.toastInfo
import kotlinx.coroutines.*
import velord.university.R
import velord.university.databinding.CreateNewPlaylistDialogBinding
import velord.university.interactor.SongPlaylistInteractor
import velord.university.repository.db.transaction.PlaylistTransaction
import velord.university.ui.fragment.selfLifecycle.LoggerSelfLifecycleDialogFragment


class CreateNewPlaylistDialogFragment :
    LoggerSelfLifecycleDialogFragment() {

    override val TAG: String = "CreateNewPlaylistDialogFragment"

    //Required interface for hosting activities
    interface Callbacks {  }

    private var callbacks: Callbacks? =  null

    private val scope = getScope()

    //view
    private var _binding: CreateNewPlaylistDialogBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding ?:
    throw ViewDestroyed("Don't touch view when it is destroyed")

    private val songsToPlaylist = SongPlaylistInteractor.songList

    private var playlistName = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null

        scope.cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(
        R.layout.create_new_playlist_dialog, container).run {
        //bind
        _binding = CreateNewPlaylistDialogBinding.bind(this)
        scope.launch {
            onMain { initView() }
        }
        binding.root
    }


    private fun initView() {
        initEditText()
        initCancel()
        initApply()
    }

    private fun initEditText() {
        binding.txtYourName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (s.isNotEmpty()) playlistName = s.toString()
            }
        })
    }

    private fun initCancel() {
        binding.cancelLayout.setCloseDialog()
        binding.cancelIcon.setCloseDialog()
        binding.cancelMsg.setCloseDialog()
    }

    private fun initApply() {
        binding.applyLayout.setNewPlaylist()
        binding.applyIcon.setNewPlaylist()
        binding.applyMsg.setNewPlaylist()
    }

    private fun createNewPlaylist() {
        if (playlistName.isNotEmpty()) {
            //todo()
            scope.launch {
                PlaylistTransaction.createNewPlaylist(playlistName, songsToPlaylist.map { it.id })
            }
            dismiss()
        } else requireActivity().toastInfo(
            requireContext().getString(R.string.please_type_playlist_name)
        )
    }

    private fun View.setNewPlaylist() =
        this.setOnClickListener { createNewPlaylist() }

    private fun View.setCloseDialog() =
        this.setOnClickListener { dismiss() }
}