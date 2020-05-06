package velord.university.ui.fragment.miniPlayer

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import velord.university.application.broadcast.AppBroadcastHub
import velord.university.application.broadcast.PERM_PRIVATE_RADIO
import velord.university.application.broadcast.behaviour.RadioUIReceiver
import velord.university.application.broadcast.registerBroadcastReceiver
import velord.university.application.broadcast.unregisterBroadcastReceiver
import velord.university.ui.fragment.miniPlayer.logic.MiniPlayerLayoutState

class MiniPlayerRadioGeneralFragment :
    MiniPlayerGeneralFragment(),
    RadioUIReceiver {

    override val TAG: String = "MiniPlayerRadioGeneralFragment"

    private val receivers = getRadioReceiverList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()

        receivers.forEach {
            requireActivity()
                .registerBroadcastReceiver(
                    it.first, IntentFilter(it.second), PERM_PRIVATE_RADIO
                )
        }
    }

    override fun onStop() {
        super.onStop()

        receivers.forEach {
            requireActivity()
                .unregisterBroadcastReceiver(it.first)
        }
    }

    override val nameRadioF: (Intent?) -> Unit = {
        it?.apply {
            val extra = AppBroadcastHub.Extra.radioNameUI
            val value = getStringExtra(extra)
            miniPlayerSongNameTV.text = value
        }
    }

    override val artistRadioF: (Intent?) -> Unit = {
        it?.apply {
            val extra = AppBroadcastHub.Extra.radioArtistUI
            val value = getStringExtra(extra)
            miniPlayerSongArtistTV.text = value
        }
    }

    override val showRadioF: (Intent?) -> Unit = {
        it?.apply {
            viewModel.setState(MiniPlayerLayoutState.RADIO)
            showMiniPlayerRadio()
        }
    }

    override val stopRadioF: (Intent?) -> Unit = {
        stopButtonInvoke()
    }

    override val playRadioF: (Intent?) -> Unit = {
        playButtonInvoke()
    }

    override val likeRadioF: (Intent?) -> Unit = {
        likeButtonInvoke()
    }

    override val unlikeRadioF: (Intent?) -> Unit = {
        unlikeButtonInvoke()
    }
}