package f1.sleepLeader

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import org.jetbrains.anko.toast


class AlarmBroadcastReceiver:BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent){
        Toast.makeText(context, "アラームが鳴ったで", Toast.LENGTH_LONG).show()

        val musicPlay = MusicPlayActivity()

        val res = context?.resources
        musicPlay.MusicStop()
        musicPlay.AlarmMusicSet(res, context)

        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
}