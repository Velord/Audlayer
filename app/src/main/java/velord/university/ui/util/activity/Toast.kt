package velord.university.ui.util.activity

import android.app.Activity
import androidx.core.content.res.ResourcesCompat
import velord.university.R
import www.sanju.motiontoast.MotionToast


fun Activity.motionToast(message: String,
                         style: String,
                         gravity: Int = MotionToast.GRAVITY_BOTTOM,
                         duration: Int = MotionToast.SHORT_DURATION
) = MotionToast.createColorToast(this, message,
    style,
    gravity,
    duration,
    ResourcesCompat.getFont(this, R.font.helvetica_regular)
)

fun Activity.toastDelete(message: String,
                         duration: Int = MotionToast.SHORT_DURATION,
                         gravity: Int = MotionToast.GRAVITY_BOTTOM,
) = this.motionToast(message, MotionToast.TOAST_DELETE, gravity, duration)

fun Activity.toastInfo(message: String,
                       duration: Int = MotionToast.SHORT_DURATION,
                       gravity: Int = MotionToast.GRAVITY_BOTTOM,
) = this.motionToast(message, MotionToast.TOAST_INFO, gravity, duration)

fun Activity.toastWarning(message: String,
                          duration: Int = MotionToast.SHORT_DURATION,
                          gravity: Int = MotionToast.GRAVITY_BOTTOM,
) = this.motionToast(message, MotionToast.TOAST_WARNING, gravity, duration)

fun Activity.toastError(message: String,
                        duration: Int = MotionToast.SHORT_DURATION,
                        gravity: Int = MotionToast.GRAVITY_BOTTOM,
) = this.motionToast(message, MotionToast.TOAST_ERROR, gravity, duration)

fun Activity.toastSuccess(message: String,
                          duration: Int = MotionToast.SHORT_DURATION,
                          gravity: Int = MotionToast.GRAVITY_BOTTOM,
) = this.motionToast(message, MotionToast.TOAST_SUCCESS, gravity, duration)