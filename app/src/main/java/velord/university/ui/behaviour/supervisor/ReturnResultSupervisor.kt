package velord.university.ui.behaviour.supervisor

import android.app.Activity
import android.util.Log
import androidx.fragment.app.FragmentManager
import com.statuscasellc.statuscase.model.entity.openFragment.general.FragmentCaller
import velord.university.R
import velord.university.model.entity.music.song.DownloadSong
import velord.university.model.entity.openFragment.general.OpenFragmentEntity
import velord.university.model.entity.openFragment.returnResult.ReturnResultFromFragment
import velord.university.model.entity.vk.entity.VkCredential
import velord.university.ui.fragment.vk.VKFragment
import velord.university.ui.util.activity.toastError
import velord.university.ui.util.findBy

class ReturnResultSupervisor(
    private val activity: Activity,
    private val fm: FragmentManager
) {
    private val TAG = "ReturnResultSupervisor"

    fun vkLogin(open: OpenFragmentEntity) {
        val result = open as ReturnResultFromFragment<VkCredential>
        if (result.success) {
            when(result.source) {
                FragmentCaller.VK -> {
                    val fragment = fm.findBy<VKFragment>()
                    fragment.userInputLoginCredential(result)
                }
                else -> {
                    Log.d(TAG, result.source.toString())
                    activity.toastError(activity.getString(R.string.not_implemented_operation))
                }
            }
        }
    }

    fun downloadSong(open: OpenFragmentEntity) {
        val result = open as ReturnResultFromFragment<DownloadSong>
        if (result.success) {
            when(result.source) {
                FragmentCaller.VK -> {
                    val fragment = fm.findBy<VKFragment>()
                    fragment.songDownloaded(result.value!!)
                }
                else -> {
                    Log.d(TAG, result.source.toString())
                    activity.toastError(activity.getString(R.string.not_implemented_operation))
                }
            }
        }
    }

}