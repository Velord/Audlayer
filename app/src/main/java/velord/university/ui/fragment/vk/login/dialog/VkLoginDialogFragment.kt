package velord.university.ui.fragment.vk.login.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.statuscasellc.statuscase.model.entity.openFragment.general.FragmentCaller
import velord.university.R
import velord.university.databinding.VkLoginDialogFragmentBinding
import velord.university.model.entity.openFragment.general.OpenFragmentEntity
import velord.university.model.entity.openFragment.returnResult.OpenFragmentForResult
import velord.university.model.entity.openFragment.returnResult.ReturnResultFromFragment
import velord.university.model.entity.vk.VkCredential
import velord.university.model.exception.ViewDestroyed
import velord.university.ui.util.activity.toastError

private const val WHOM_CALL_FRAGMENT = "whom_call_fragment"

class VkLoginDialogFragment : DialogFragment() {

    interface Callbacks {

        fun returnResultVkLogin(openFragmentEntity: OpenFragmentEntity)
    }
    private var callbacks: Callbacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        callbacks = requireActivity() as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()

        callbacks = null
    }

    companion object {
        fun newInstance(open: OpenFragmentEntity) =
            VkLoginDialogFragment().apply {
                val source = open as OpenFragmentForResult
                arguments = bundleOf(Pair(WHOM_CALL_FRAGMENT, source))
            }
    }

    private lateinit var source: FragmentCaller

    private var _binding: VkLoginDialogFragmentBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding ?:
    throw ViewDestroyed("Don't touch view when it is destroyed")

    override fun getTheme() = R.style.RoundedCornersDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(
            R.layout.vk_login_dialog_fragment,
            container,
            false
        ).run {
            _binding = VkLoginDialogFragmentBinding.bind(this)
            initView()
            binding.root
        }
    }

    private fun initView() {
        val arg = requireArguments().get(WHOM_CALL_FRAGMENT) as OpenFragmentForResult
        source = arg.source

        binding.apply.setOnClickListener {
            checkCredential()
        }
        binding.cancel.setOnClickListener {
            returnResult(false, null)
        }
    }

    private fun checkCredential() {
        val login = binding.authLoginInput.text.toString()
        if (login.isEmpty()) {
            requireActivity().toastError(getString(R.string.input_login))
            return
        }

        val password = binding.authPasswordInput.text.toString()
        if (password.isEmpty()) {
            requireActivity().toastError(getString(R.string.input_password))
            return
        }

        returnResult(true, VkCredential(login, password))
    }

    private fun returnResult(
        success: Boolean,
        value: VkCredential?,
    ) {
        val result = ReturnResultFromFragment(
            source, success, value
        )

        callbacks?.returnResultVkLogin(result)
        dialog?.dismiss()
    }

}