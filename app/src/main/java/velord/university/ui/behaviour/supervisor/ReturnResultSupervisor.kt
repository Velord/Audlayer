package velord.university.ui.behaviour.supervisor

import android.app.Activity
import androidx.fragment.app.FragmentManager

class ReturnResultSupervisor(
    private val activity: Activity,
    private val fm: FragmentManager
) {
    private val TAG = "ReturnResultSupervisor"

//    fun beforeImportantActionDialog(
//        open: OpenFragmentEntity) {
//        val result = open as ReturnResultFromFragment<Boolean>
//        when(result.source) {
//            FragmentCaller.SCHEMA_TRANSFER -> {
//                when(result.value) {
//                    true -> {
//                        val fragment = fm.findBy<SchemaTransferFragment>()
//                        fragment.userClickYesOnDialog(result.value)
//                    }
//                    false -> { }
//                }
//            }
//            FragmentCaller.CUT_INFO_VIEWER -> {
//                when(result.value) {
//                    true -> {
//                        val fragment = fm.findBy<CutInfoViewerFragment>()
//
//                        fragment.userClickYesOnDialog(result.value)
//                    }
//                    false -> { }
//                }
//            }
//            FragmentCaller.WARRANTY_VIEWER -> {
//                when(result.value) {
//                    true -> {
//                        val fragment = fm.findBy<WarrantyViewerFragment>()
//
//                        fragment.userClickYesOnDialog(result.value)
//                    }
//                    false -> { }
//                }
//            }
//            else -> {
//                Log.d(TAG, result.source.toString())
//                activity.toastError(activity.getString(R.string.not_implemented))
//            }
//        }
//    }

}