package f1.sleepLeader


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
//import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_alarm_set.*
import java.lang.IllegalArgumentException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class AlarmSetActivity : AppCompatActivity() {

    private lateinit var realm : Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_set)

        realm = Realm.getDefaultInstance()

        AlarmSetting.setOnClickListener {
            var calendar: Calendar = Calendar.getInstance()
            realm.executeTransaction {
                var maxId = realm.where<AlarmTable>().max("alarmId")
                var nextId = (maxId?.toLong() ?: 0L) + 1
                var alarm = realm.createObject<AlarmTable>(nextId)

                println(alarm.alarmId)

                //設定した時間
                var h = SetTime.text.toString()
                var m = SetMinute.text.toString()
                var hour = Integer.parseInt(h)
                var min = Integer.parseInt(m)
                var time = "%1$02d:%2$02d".format(hour,min)
                alarm.timer = time
                println(alarm.timer)

                //スヌーズ設定
                var snoozeFlag = "ON"
                alarm.snoozeFlag = snoozeFlag

                println(alarm.snoozeFlag)

                //音楽の再生設定
                var musicFlag = "ON"
                alarm.musicFlag = musicFlag

                println(alarm.musicFlag)

                //音楽のファイルパス
                var musicPath = "www"
                alarm.musicPath = musicPath

                println(alarm.musicPath)
            }
                var sec = setSecond()

                calendar.add(Calendar.SECOND, sec)

                val alarmIntent = Intent(this, AlarmReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(
                    this,
                    0,
                    alarmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                manager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

                val intent = Intent(applicationContext, AlarmStopActivity::class.java)
                startActivity(intent)
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
    private fun String.toDate(time : String = "HH:mm") : Date?{
        val sdTimer = try{
            SimpleDateFormat(time)
        }catch (e: IllegalArgumentException){
            null
        }

        val timer = sdTimer?.let{
            try{
                it.parse(this)
            }catch (e: ParseException){
                null
            }
        }
        return timer
    }
}