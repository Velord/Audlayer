package velord.university.ui.fragment.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.lifecycle.ViewModelProviders
import velord.university.R
import velord.university.ui.fragment.MenuFragment

class AlbumFragment : MenuFragment() {

    override val TAG: String
        get() = "AlbumFragment"

    override val pressedButton: ImageButton
        get() = albumImageBt

    override val newBackground: Int
        get() = R.drawable.album_pressed

    companion object {
        fun newInstance() = AlbumFragment()
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(AlbumViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.album_fragment, container, false).apply {
            super.initView(this)
        }
    }
}
