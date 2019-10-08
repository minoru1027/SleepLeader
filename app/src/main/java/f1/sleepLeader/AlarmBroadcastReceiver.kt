package f1.sleepLeader

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import org.jetbrains.anko.toast

class AlarmBroadcastReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
    context?.toast("アラーム設定したで")

    }
}