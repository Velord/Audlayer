package velord.university.ui.fragment.folder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.lifecycle.ViewModelProviders
import velord.university.R
import velord.university.ui.fragment.MenuFragment


class FolderFragment : MenuFragment() {

    override val TAG: String
        get() = "FolderFragment"

    companion object {
        fun newInstance() = FolderFragment()
    }

    override val pressedButton: ImageButton
        get() = folderImageBt

    override val newBackground: Int
        get() = R.drawable.folder_pressed

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(FolderViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.folder_fragment, container, false).apply {
            super.initView(this)
        }
    }

}
