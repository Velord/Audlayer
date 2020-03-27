package velord.university.application.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

fun Context.unregisterBroadcastReceiver(receiver: BroadcastReceiver) =
    unregisterReceiver(receiver)

fun Context.registerBroadcastReceiver(receiver: BroadcastReceiver,
                                      filter: IntentFilter,
                                      permission: String)  =
    registerReceiver(receiver, filter, permission, null)

fun Context.sendBroadcast(action: String, permission: String) =
    sendBroadcast(Intent(action), permission)

fun Context.sendBroadcast(action: String, permission: String,
                          valueName: String, value: String) {
    val intent = Intent(action)
    intent.putExtra(valueName, value)
    sendBroadcast(intent, permission)
}

fun Context.sendBroadcast(action: String, permission: String,
                          valueName: String, value: Boolean) {
    val intent = Intent(action)
    intent.putExtra(valueName, value)
    sendBroadcast(intent, permission)
}

fun Context.sendBroadcast(action: String, permission: String,
                          valueName: String, value: Int) {
    val intent = Intent(action)
    intent.putExtra(valueName, value)
    sendBroadcast(intent, permission)
}

