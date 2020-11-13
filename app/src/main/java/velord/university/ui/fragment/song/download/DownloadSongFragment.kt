package velord.university.ui.fragment.song.download

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import velord.university.R
import velord.university.databinding.DownloadSongFragmentBinding
import velord.university.model.coroutine.getScope
import velord.university.model.coroutine.onMain
import velord.university.model.entity.music.song.download.DownloadSong
import velord.university.model.entity.openFragment.general.OpenFragmentEntity
import velord.university.model.entity.openFragment.returnResult.OpenFragmentForResultWithData
import velord.university.model.entity.openFragment.returnResult.ReturnResultFromFragment
import velord.university.model.exception.ViewDestroyed
import velord.university.ui.behaviour.backPressed.BackPressedHandlerFirst
import velord.university.ui.fragment.selfLifecycle.LoggerSelfLifecycleFragment
import velord.university.ui.util.activity.toastError
import velord.university.ui.util.activity.toastSuccess
import velord.university.ui.util.view.deactivate
import velord.university.ui.util.view.gone
import velord.university.ui.util.view.visible

private const val RETURN_RESULT = "returnResult"

class DownloadSongFragment :
    LoggerSelfLifecycleFragment(),
    BackPressedHandlerFirst {

    override val TAG: String = "DownloadSongFragment"

    //Required interface for hosting activities
    interface Callbacks {

        fun returnDownloadSong(open: OpenFragmentEntity)

        fun close()
    }
    private var callbacks: Callbacks? =  null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null

        scope.cancel()
    }

    override fun onBackPressed(): Boolean = true

    companion object {
        fun newInstance(open: OpenFragmentEntity) =
            DownloadSongFragment().apply {
                val result = open as OpenFragmentForResultWithData<Array<DownloadSong>>
                arguments = bundleOf(Pair(RETURN_RESULT, result))
            }
    }

    private val viewModel: DownloadSongViewModel  by viewModels()

    private val scope = getScope()
    //view
    private var _binding: DownloadSongFragmentBinding? = null
    // onDestroyView.
    private val binding get() = _binding ?:
    throw ViewDestroyed("Don't touch view when it is destroyed")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.download_song_fragment,
        container, false).run {
        //bind
        _binding = DownloadSongFragmentBinding.bind(this)
        initViewModel()
        initView()
        binding.root
    }

    private fun initViewModel() {
        val res = requireArguments().get(RETURN_RESULT)
                as OpenFragmentForResultWithData<Array<DownloadSong>>
        viewModel.initViewModel(res)
    }

    private fun initView() {
        binding.downloadSongContainer.deactivate()
        observe()

        binding.progress.visible()
        binding.webView.visible()

        download()
    }

    private fun observe() {
        viewModel.downloadedLive.observe(viewLifecycleOwner) { song ->
            returnResult(song)
        }
        viewModel.endLive.observe(viewLifecycleOwner) { end ->
            if (end) closeSelf()
        }
    }

    private fun closeSelf() {
        binding.webView.gone()
        binding.progress.gone()
        callbacks?.close()
    }

    private fun download() {
        scope.launch {
            try { viewModel.download(binding.webView) }
            catch (e: Exception) {
                onMain {
                    requireActivity().toastError(e.message.toString())
                    closeSelf()
                }
            }
        }
    }

    private fun returnResult(song: DownloadSong) {
        requireActivity().toastSuccess(
            requireContext().getString(R.string.song_success_downloaded)
        )

        val result = ReturnResultFromFragment(
            viewModel.forResult.source,
            true,
            song
        )
        callbacks?.returnDownloadSong(result)
    }
}