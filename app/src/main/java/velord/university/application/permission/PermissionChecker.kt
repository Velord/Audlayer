package velord.university.application.permission

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionChecker {

    const val REQUEST_ENABLE_READ_WRITE_STORAGE = 123
    fun Context.checkReadWriteExternalStoragePermission(activity: Activity): Boolean {
        val permissionCheckRead =
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
            )
        val permissionCheckWrite =
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        return if (permissionCheckRead != PackageManager.PERMISSION_GRANTED ||
            permissionCheckWrite != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_ENABLE_READ_WRITE_STORAGE
            )
            false
        } else true
    }

    const val REQUEST_ENABLE_BT = 456
    fun Context.enableBluetooth(activity: Activity): Boolean {
        val bluetoothManager = this
            .getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter

        val enabled = bluetoothAdapter.isEnabled
        return if (bluetoothAdapter != null && enabled) true
        else {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            ActivityCompat.startActivityForResult(activity, enableBtIntent, REQUEST_ENABLE_BT, null)
            false
        }
    }

    fun Context.checkBluetoothPermission(activity: Activity): Boolean {
        val permissionBluetooth =
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH
            )

        return if (permissionBluetooth != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.BLUETOOTH
                ),
                1
            )
            false
        }
        else true
    }

    const val REQUEST_ENABLE_LOCATION = 333
    fun Context.checkAllLocationPermission(activity: Activity): Boolean {
        //init help lists
        val permissionList = mutableListOf<Pair<Boolean, String>>()
        val permissionListRequest = mutableListOf<String>()
        //fine
        val permissionFineLocation =
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            )
        val fineGranted =  permissionFineLocation == PackageManager.PERMISSION_GRANTED
        permissionList += fineGranted to Manifest.permission.ACCESS_FINE_LOCATION
        //coarse
        val permissionCoarseLocation =
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            )
        val coarseGranted = permissionCoarseLocation == PackageManager.PERMISSION_GRANTED
        permissionList += coarseGranted to Manifest.permission.ACCESS_COARSE_LOCATION
        //background
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            val permissionBackground =
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            val backgroundGranted = permissionBackground == PackageManager.PERMISSION_GRANTED
            permissionList += backgroundGranted to Manifest.permission.ACCESS_BACKGROUND_LOCATION
        }
        //check what need request
        permissionList.forEach {
            if (it.first.not()) permissionListRequest += it.second
        }
        return if (permissionListRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                activity,
                permissionListRequest.toTypedArray(),
                REQUEST_ENABLE_LOCATION
            )
            false
        }
        else true
    }

    private const val CAMERA_PERMISSION_REQUEST_CODE = 200
    fun Context.checkCameraPermission(activity: Activity): Boolean {
        val selfPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        )
        val granted = PackageManager.PERMISSION_GRANTED
//        val shouldGranted = ActivityCompat
//            .shouldShowRequestPermissionRationale(
//                (requireContext() as Activity?)!!,
//                Manifest.permission.CAMERA
//            )
        val request = {
            ActivityCompat.requestPermissions(
                (activity as Activity?)!!,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }

        //    && shouldGranted
        return if (selfPermission != granted) {
            request()
            false
        }
        else true
    }
}