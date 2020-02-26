package velord.university.application.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionChecker {
    fun checkThenRequestReadWriteExternalStoragePermission(context: Context, activity: Activity): Boolean {
        val permissionCheckRead =
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_EXTERNAL_STORAGE)
        val permissionCheckWrite =
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return if (permissionCheckRead != PackageManager.PERMISSION_GRANTED ||
            permissionCheckWrite != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1)
            false
        } else true
    }
}