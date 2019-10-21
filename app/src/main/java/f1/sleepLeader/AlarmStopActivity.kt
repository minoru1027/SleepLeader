package f1.sleepLeader

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import java.util.*

class AlarmStopActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_stop)
    }

    override fun onResume() {
        super.onResume()

        var table = AlarmTable()
        var Snooze =table.snoozeFlag
        var setSnooze = "OFF"

        if(Snooze.equals("ON")){
            onSetSnooze()

            table.snoozeFlag = setSnooze
        }
    }

    private fun onSetSnooze(){
        var calendar: Calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE,5)

        val alarmIntent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)



    }
}
