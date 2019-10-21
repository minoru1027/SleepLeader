package f1.sleepLeader


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_alarm_set.*


class AlarmSetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_set)


        AlarmSetting.setOnClickListener {

            var table = AlarmTable()

            //アラームID
            var alarmId: Long = 1
            table.alarmId = alarmId

            println(table.alarmId)

            //設定した時間
            var hour = SetTime.text.toString()
            var min = SetMinute.text.toString()
            table.timer = hour +":"+ min

            println(table.timer)

            //スヌーズ設定
            var snoozeFlag = "ON"
            table.snoozeFlag = snoozeFlag

            println(table.snoozeFlag)

            //音楽の再生設定
            var musicFlag = "ON"
            table.musicFlag = musicFlag

            println(table.musicFlag)

            //音楽のファイルパス
            var musicPath = "www"
            table.musicPath = musicPath

            println(table.musicPath)

            var sec = setSecond()
            var calendar: Calendar = Calendar.getInstance()
            calendar.add(Calendar.SECOND,sec)

            val alarmIntent = Intent(this, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            manager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

           // val intent = Intent(applicationContext, AlarmStopActivity::class.java)
            // startActivity(intent)

        }

    }

    private fun setHour(): Int {

        val calender: Calendar = Calendar.getInstance()
        var hourNow = calender.get(Calendar.HOUR_OF_DAY)

        var hourSet = SetTime.text.toString().toInt()
        var diffHour = hourSet - hourNow

        if (diffHour < 0) {
            diffHour *= -1

            diffHour -= 24

        }else if (diffHour > 0){
            diffHour *= 1
        }

        return diffHour
    }

    private fun setMinute(): Int {

        val calender: Calendar = Calendar.getInstance()
        var minNow = calender.get(Calendar.MINUTE)

        var minSet = SetMinute.text.toString().toInt()
        var diffMin = minSet - minNow

        if (diffMin < 0) {
            diffMin *= -1

            diffMin -= 60
        }

        return diffMin
    }

    private fun setSecond(): Int{
        val calendar: Calendar = Calendar.getInstance()
        var secNow = calendar.get(Calendar.SECOND)

        var transHour = setHour() * 60 * 60
        var transMinute = setMinute() * 60
        var secSet = transHour + transMinute

        var sec = secSet - secNow

        return sec

    }
}