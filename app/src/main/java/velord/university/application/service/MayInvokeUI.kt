package velord.university.application.service

import android.content.Context
import velord.university.repository.hub.MiniPlayerRepository
import velord.university.ui.fragment.miniPlayer.logic.MiniPlayerLayoutState

fun Context.mayInvokeRadio(f: () -> Unit) =
    MiniPlayerRepository.mayDoAction(
        this, MiniPlayerLayoutState.RADIO, f)

//when mini player is radio no need send info
fun Context.mayInvokeGeneral(f: () -> Unit) =
    MiniPlayerRepository.mayDoAction(
        this, MiniPlayerLayoutState.GENERAL, f)