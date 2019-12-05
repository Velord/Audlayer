package velord.university.ui.fragment.vk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import velord.university.R
import velord.university.ui.fragment.MenuFragment

class VKFragment : MenuFragment() {

    override val TAG: String
        get() = "VKFragment"

    companion object {
        fun newInstance() = VKFragment()
    }

    private lateinit var viewModel: VkViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.vk_fragment, container, false).apply {
            initMenuButtons(this)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(VkViewModel::class.java)
        // TODO: Use the ViewModel
    }
}
