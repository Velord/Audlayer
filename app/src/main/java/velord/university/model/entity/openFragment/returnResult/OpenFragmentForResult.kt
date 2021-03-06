package velord.university.model.entity.openFragment.returnResult

import android.os.Parcelable
import com.statuscasellc.statuscase.model.entity.openFragment.general.FragmentCaller
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import velord.university.model.entity.openFragment.general.OpenFragmentEntity

@Parcelize
data class OpenFragmentForResult (
    val source: FragmentCaller,
) : Parcelable, OpenFragmentEntity