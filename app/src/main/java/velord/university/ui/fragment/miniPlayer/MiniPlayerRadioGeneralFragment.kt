package velord.university.ui.fragment.miniPlayer

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import velord.university.R
import velord.university.application.broadcast.AppBroadcastHub
import velord.university.application.broadcast.PERM_PRIVATE_RADIO
import velord.university.application.broadcast.behaviour.RadioUIReceiver
import velord.university.application.broadcast.registerBroadcastReceiver
import velord.university.application.broadcast.unregisterBroadcastReceiver
import velord.university.ui.fragment.miniPlayer.logic.MiniPlayerLayoutState
import velord.university.ui.fragment.miniPlayer.logic.general.HeartLogic
import velord.university.ui.fragment.miniPlayer.logic.general.PlayPauseLogic

class MiniPlayerRadioGeneralFragment :
    MiniPlayerGeneralFragment(),
    RadioUIReceiver {

    override val TAG: String = "MiniPlayerRadioGeneralFragment"

    private val receivers = getRadioUIReceiverList()

    override fun onStart() {
        super.onStart()

        receivers.forEach {
            requireActivity()
                .registerBroadcastReceiver(
                    it.first, IntentFilter(it.second), PERM_PRIVATE_RADIO
                )
        }

        getInfoFromServiceWhenStart()
    }

    override fun onStop() {
        super.onStop()

        receivers.forEach {
            requireActivity()
                .unregisterBroadcastReceiver(it.first)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.mini_player_fragment, container, false).apply {
            super.initMiniPlayerGeneralView(this)
            initView()
        }
    }

    private fun initView() {
        miniPlayerRadioPlayOrPauseIB.setOnClickListener {
            PlayPauseLogic.press(requireActivity(), viewModel.getState())
        }
        miniPlayerRadioLikedIB.setOnClickListener {
            HeartLogic.press(requireActivity(), viewModel.getState())
        }
    }

    override val nameRadioUIF: (Intent?) -> Unit = {
        it?.apply {
            val extra = AppBroadcastHub.Extra.radioNameUI
            val value = getStringExtra(extra)
            miniPlayerRadioNameTV.text = value
        }
    }

    override val artistRadioUIF: (Intent?) -> Unit = {
        it?.apply {
            val extra = AppBroadcastHub.Extra.radioArtistUI
            val value = getStringExtra(extra)
            miniPlayerRadioArtistTV.text = value
        }
    }

    override val showRadioUIF: (Intent?) -> Unit = {
        it?.apply {
            viewModel.setState(MiniPlayerLayoutState.RADIO)
            showMiniPlayerRadio()
        }
    }

    override val stopRadioUIF: (Intent?) -> Unit = {
        viewModel.mayDoAction(MiniPlayerLayoutState.RADIO) {
            stopButtonInvoke(miniPlayerRadioPlayOrPauseIB)
        }
    }

    override val playRadioUIF: (Intent?) -> Unit = {
        viewModel.mayDoAction(MiniPlayerLayoutState.RADIO) {
            playButtonInvoke(miniPlayerRadioPlayOrPauseIB)
        }
    }

    override val likeRadioUIF: (Intent?) -> Unit = {
        viewModel.mayDoAction(MiniPlayerLayoutState.RADIO) {
            likeButtonInvoke(miniPlayerRadioLikedIB)
        }
    }

    override val unlikeRadioUIF: (Intent?) -> Unit = {
        viewModel.mayDoAction(MiniPlayerLayoutState.RADIO) {
            unlikeButtonInvoke(miniPlayerRadioLikedIB)
        }
    }

    override val iconRadioUIF: (Intent?) -> Unit = {
        it?.apply {
            val extra = AppBroadcastHub.Extra.iconRadioUI
            val value = getStringExtra(extra)
            loadRadioStationIcon(miniPlayerRadioIcon, value)
        }
    }

    private fun getInfoFromServiceWhenStart() {
        val f: () -> Unit = {
            AppBroadcastHub.apply {
                showMiniPlayerRadio()
                requireContext().getInfoRadioService()
            }
        }
        val state = MiniPlayerLayoutState.RADIO
        viewModel.mayDoAction(state, f)
    }

    private fun loadRadioStationIcon(view: ImageView, icon: String) {
        val imageName = resources.getIdentifier(
            icon, "drawable", requireContext().packageName)

        Glide.with(requireActivity())
            .load(imageName)
            .placeholder(R.drawable.radio_record)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(view)
    }
}