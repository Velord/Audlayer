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

class MiniPlayerRadioGeneralFragment :
    MiniPlayerGeneralFragment(),
    RadioUIReceiver {

    override val TAG: String = "MiniPlayerFragment"

    private val receivers = getRadioReceiverList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = super.onCreateView(inflater, container, savedInstanceState)


    override fun onStart() {
        super.onStart()

        receivers.forEach {
            requireActivity()
                .registerBroadcastReceiver(
                    it.first, IntentFilter(it.second), PERM_PRIVATE_RADIO
                )
        }
        //get info from service about song cause service was created earlier then this view
        AppBroadcastHub.apply {
            requireContext().getInfoService()
        }
    }

    override fun onStop() {
        super.onStop()

        receivers.forEach {
            requireActivity()
                .unregisterBroadcastReceiver(it.first)
        }
    }

    override val stopRadioF: (Intent?) -> Unit = {

    }

    override val playRadioF: (Intent?) -> Unit = {

    }

    override val likeRadioF: (Intent?) -> Unit = {

    }

    override val unlikeRadioF: (Intent?) -> Unit = {

    }
}