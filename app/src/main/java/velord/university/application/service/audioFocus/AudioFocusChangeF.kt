package velord.university.application.service.audioFocus

data class AudioFocusChangeF(
    val focusLossF: () -> Unit,
    val focusLossTransientF: () -> Unit,
    val focusLossTransientCanDuckF: () -> Unit,
    val focusGainF: () -> Unit
)