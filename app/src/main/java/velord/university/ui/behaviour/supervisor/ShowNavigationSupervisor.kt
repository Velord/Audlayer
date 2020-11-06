package velord.university.ui.behaviour.supervisor

import android.app.Activity
import android.util.Log
import androidx.core.view.GravityCompat
import com.statuscasellc.statuscase.model.entity.openFragment.general.FragmentCaller
import com.statuscasellc.statuscase.model.entity.openFragment.general.ShowNavigation
import velord.university.R
import velord.university.databinding.MainActivityBinding
import velord.university.model.entity.openFragment.general.OpenFragmentEntity
import velord.university.ui.util.activity.toastError

class ShowNavigationSupervisor(
    private val activity: Activity,
    private val binding: MainActivityBinding
) {

    private val TAG = "ShowNavigationSupervisor"

    fun show(open: OpenFragmentEntity) {
        val to = open as ShowNavigation
        when(to.source) {
            FragmentCaller.FOLDER -> defaultShow()
            else -> {
                Log.d(TAG, to.source.toString())
                activity.toastError(activity.getString(R.string.not_implemented_from_this_place))
            }
        }
    }

    private fun defaultShow() {
        //todo()
        //binding.mainActivityDrawerContainer.openDrawer(GravityCompat.START)
    }
}