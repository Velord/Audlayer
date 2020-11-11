package velord.university.ui.fragment.miniPlayer

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.coroutines.*
import velord.university.R
import velord.university.application.broadcast.behaviour.MiniPlayerShowAndHiderBroadcastReceiver
import velord.university.application.broadcast.behaviour.RadioUIReceiver
import velord.university.application.broadcast.hub.*
import velord.university.databinding.*
import velord.university.model.coroutine.getScope
import velord.university.model.exception.ViewDestroyed
import velord.university.ui.fragment.miniPlayer.logic.MiniPlayerLayoutState
import velord.university.ui.fragment.miniPlayer.logic.general.HeartLogic
import velord.university.ui.fragment.miniPlayer.logic.general.PlayPauseLogic
import velord.university.ui.util.DrawableIcon
import velord.university.ui.util.view.gone
import velord.university.ui.util.view.setAutoScrollable
import velord.university.ui.util.view.visible

class MiniPlayerRadioDefaultFragment :
    MiniPlayerDefaultFragment(),
    RadioUIReceiver,
    //control show and hide radio and default
    MiniPlayerShowAndHiderBroadcastReceiver {

    override val TAG: String = "MiniPlayerRadioFragment"

    private val receivers = getRadioUIReceiverList() +
            miniPlayerShowAndHiderReceiverList()

    private val scope = getScope()

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

    //view
    private var _binding: MiniPlayerFragmentBinding? = null
    override var _bindingRadio: MiniPlayerRadioBinding? = null
    override var _bindingDefault: MiniPlayerDefaultBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding ?:
    throw ViewDestroyed("Don't touch view when it is destroyed")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.mini_player_fragment,
        container, false).run {
        //bind
        _binding = MiniPlayerFragmentBinding.bind(this)
        _bindingRadio = binding.miniPlayerRadio
        _bindingDefault = binding.miniPlayerDefault
        super.initMiniPlayerGeneralView()
        initView()
        getInfoFromServiceWhenStart()

        binding.root
    }

    private fun initView() {
        bindingRadio.icon.setOnClickListener {
            AppBroadcastHub.run { requireContext().clickOnRadioIcon() }
        }
        bindingRadio.playOrPause.setOnClickListener {
            PlayPauseLogic.press(requireActivity(), viewModel.getState())
        }
        bindingRadio.liked.setOnClickListener {
            HeartLogic.press(requireActivity(), viewModel.getState())
        }
    }

    private fun getInfoFromServiceWhenStart() {
        val f: () -> Unit = {
            AppBroadcastHub.apply {
                showMiniPlayerRadio()
                requireContext().doAction(BroadcastActionType.GET_INFO_RADIO_SERVICE)
            }
        }
        val state = MiniPlayerLayoutState.RADIO
        viewModel.mayDoAction(state, f)
    }

    override val nameRadioUIF: (Intent?) -> Unit = {
        it?.apply {
            val extra = BroadcastExtra.radioNameUI
            val value = getStringExtra(extra)
            bindingRadio.name.text = value
        }
    }

    override val artistRadioUIF: (Intent?) -> Unit = {
        it?.apply {
            val extra = BroadcastExtra.radioArtistUI
            val value = getStringExtra(extra)
            bindingRadio.artist.text = value
        }
    }

    override val stopRadioUIF: (Intent?) -> Unit = {
        viewModel.mayDoAction(MiniPlayerLayoutState.RADIO) {
            stopButtonInvoke(bindingRadio.playOrPause)
        }
    }

    override val playRadioUIF: (Intent?) -> Unit = {
        viewModel.mayDoAction(MiniPlayerLayoutState.RADIO) {
            playButtonInvoke(bindingRadio.playOrPause)
        }
    }

    override val likeRadioUIF: (Intent?) -> Unit = {
        viewModel.mayDoAction(MiniPlayerLayoutState.RADIO) {
            likeButtonInvoke(bindingRadio.liked)
        }
    }

    override val unlikeRadioUIF: (Intent?) -> Unit = {
        viewModel.mayDoAction(MiniPlayerLayoutState.RADIO) {
            unlikeButtonInvoke(bindingRadio.liked)
        }
    }

    override val iconRadioUIF: (Intent?) -> Unit = {
        it?.apply {
            val extra = BroadcastExtra.iconRadioUI
            val value = getStringExtra(extra)
            DrawableIcon.loadRadioIconAsset(
                requireContext(), bindingRadio.icon, value)
        }
    }

    override val radioPlayerUnavailableUIF: (Intent?) -> Unit = {
        it?.apply {
            //wait and request info
            scope.launch {
                delay(3000)
                getInfoFromServiceWhenStart()
            }
        }
    }

    override val radioUrlIsWrongUIF: (Intent?) -> Unit = {}

    override val showF: (Intent?) -> Unit = {
        viewModel.setState(MiniPlayerLayoutState.DEFAULT)
        showMiniPlayerDefault()
    }

    override val hideF: (Intent?) -> Unit = {
        bindingDefault.miniPlayerGeneralContainer.gone()
    }

    override val showRadioF: (Intent?) -> Unit = {
        viewModel.setState(MiniPlayerLayoutState.RADIO)
        showMiniPlayerRadio()
    }

    override val hideRadioF: (Intent?) -> Unit = {
        bindingRadio.miniPlayerRadioContainer.gone()
    }

}