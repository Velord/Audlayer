package velord.university.application.broadcast.restarter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import velord.university.application.AudlayerApp
import velord.university.application.service.hub.player.MiniPlayerServiceBroadcastReceiver


class RestarterMiniPlayerGeneralService : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        Log.i("Broadcast Listened", "Service tried to stop")
        Toast.makeText(context, "Service restarted: $intent", Toast.LENGTH_SHORT).show()

        AudlayerApp.startService(
            context, MiniPlayerServiceBroadcastReceiver()
        )
    }
}