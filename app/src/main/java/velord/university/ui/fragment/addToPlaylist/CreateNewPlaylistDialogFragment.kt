package velord.university.ui.fragment.addToPlaylist

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import velord.university.R
import velord.university.application.AudlayerApp
import velord.university.interactor.SongPlaylistInteractor
import velord.university.model.entity.Playlist


class CreateNewPlaylistDialogFragment : DialogFragment(){
    //Required interface for hosting activities
    interface Callbacks {
        fun succes()
    }
    private var callbacks: Callbacks? =  null

    private val scope = CoroutineScope(Job() + Dispatchers.Default)

    private lateinit var editText: EditText
    private lateinit var cancel: TextView
    private lateinit var cancelImage: ImageButton
    private lateinit var apply: TextView
    private lateinit var applyImage: ImageButton

    private val songsToPlaylist = SongPlaylistInteractor.songsPath

    private var playlistName = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.create_new_playlist_dialog, container).apply {
            initViews(this)
        }
    }

    private fun initViews(view: View) {
        initEditText(view)
        initCancel(view)
        initApply(view)
    }

    private fun initEditText(view: View) {
        editText = view.findViewById(R.id.create_new_playlist_txt_your_name)
        editText.addTextChangedListener(object : TextWatcher {
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

    private fun initCancel(view: View) {
        cancel = view.findViewById(R.id.create_new_playlist_cancel_msg)
        cancelImage =  view.findViewById(R.id.create_new_playlist_cancel)
        cancel.setOnClickListener {
            close()
        }
        cancelImage.setOnClickListener {
            close()
        }
    }

    private fun close() {
        dismiss()
        callbacks?.succes()
    }

    private fun initApply(view: View) {
        apply = view.findViewById(R.id.create_new_playlist_apply_msg)
        applyImage = view.findViewById(R.id.create_new_playlist_apply)
        apply.setOnClickListener {
            createNewPlaylist()
        }
        applyImage.setOnClickListener {
            createNewPlaylist()
        }
    }

    private fun createNewPlaylist() {
        AudlayerApp.db?.let {
            if (playlistName.isNotEmpty()) {
                scope.launch {
                    val playlist = Playlist(playlistName,
                        songsToPlaylist.toList())
                    it.playlistDao().insertAll(playlist)
                }
                dismiss()
            } else Toast.makeText(
                requireContext(),
                "Please Type Playlist Name",
                Toast.LENGTH_SHORT).show()
        }
    }
}