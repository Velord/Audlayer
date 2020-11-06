package velord.university.model.entity.openFragment.returnResult

import com.statuscasellc.statuscase.model.entity.openFragment.general.FragmentCaller
import velord.university.model.entity.openFragment.general.OpenFragmentEntity

data class ReturnResultFromFragment <T> (
    val source: FragmentCaller,
    val success: Boolean,
    val value: T?
) : OpenFragmentEntity