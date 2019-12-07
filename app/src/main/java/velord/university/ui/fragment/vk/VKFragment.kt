package velord.university.ui.fragment.vk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.lifecycle.ViewModelProviders
import velord.university.R
import velord.university.ui.fragment.MenuFragment

class VKFragment : MenuFragment() {

    override val TAG: String
        get() = "VKFragment"

    companion object {
        fun newInstance() = VKFragment()
    }

    override val pressedButton: ImageButton
        get() = vkImageBt

    override val newBackground: Int
        get() = R.drawable.vk_pressed

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(VkViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.vk_fragment, container, false).apply {
            super.initView(this)
        }
    }
}
