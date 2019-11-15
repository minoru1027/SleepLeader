package f1.sleepLeader


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
//import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_alarm_set.*
import kotlinx.android.synthetic.main.activity_main.view.*
import java.lang.IllegalArgumentException
import java.lang.invoke.MutableCallSite
import java.lang.reflect.Array.get
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timer


class AlarmSetActivity : AppCompatActivity() {

    private lateinit var realm : Realm
    private var musicPath : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_set)

        realm = Realm.getDefaultInstance()


        val musicList = realm.where<MusicTable>().findAll()
        musicSpinner.adapter = musicAdapter(musicList)
        musicSpinner.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()

        timePicker.setOnClickListener {
            val newFragment = TimePickerFragment()
            newFragment.show(supportFragmentManager, "Time Picker")

        }



        musicFlag.setOnClickListener{
            if(musicFlag.isChecked === true) {
                musicSpinner.visibility = View.VISIBLE
            }else{
                musicSpinner.visibility = View.GONE
            }
        }

        musicSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val musicPosition = musicSpinner.getItemAtPosition(position) as MusicTable
                musicPath = musicPosition.musicPath
                println(musicPath)

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        AlarmSetting.setOnClickListener {

            var calendar: Calendar = Calendar.getInstance()
            var calendarNow : Calendar = Calendar.getInstance()

            realm.executeTransaction {
                var maxId = realm.where<AlarmTable>().max("alarmId")
                var nextId = (maxId?.toLong() ?: 0L) + 1
                var alarm = realm.createObject<AlarmTable>(nextId)

                //設定した時間
                var time = timePicker.text.toString()




                alarm.timer = time

                //スヌーズ設定
                var snooze = snoozeFlag.isChecked.toString()

                alarm.snoozeFlag = snooze

                //音楽の再生設定
                var music = musicFlag.isChecked.toString()
                println(music)
                alarm.musicFlag = music

                //音楽のファイルパス
                alarm.musicPath = musicPath


                calendar.time = time?.toDate()
                calendarNow.time = getNow()

                if(calendar.timeInMillis < calendarNow.timeInMillis){
                    calendar.add(Calendar.DAY_OF_MONTH+1,1)
                }
                var timeMill = calendar.timeInMillis - calendarNow.timeInMillis
                calendar.timeInMillis = timeMill
                println(calendar.timeInMillis)

                val alarmIntent = Intent(this, AlarmReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(
                    this,
                    9,
                    alarmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                manager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

                val intent = Intent(applicationContext, AlarmStopActivity::class.java)

                var activity = "0"
                intent.putExtra("setTime", time)
                intent.putExtra("activityFlag", activity)
                intent.putExtra("snoozeFlag",alarm.snoozeFlag)
                intent.putExtra("musicFlag",alarm.musicFlag)
                intent.putExtra("musicPath",alarm.musicPath)
                startActivity(intent)
            }
        }
    }

    private fun getNow() : Date?{
        val calendarNow = Calendar.getInstance()
        val hour  = calendarNow.get(Calendar.HOUR_OF_DAY).toString()
        var minute = calendarNow.get(Calendar.MINUTE).toString()
        val timeNow = hour+":"+minute
        val timerNow = timeNow.toDate()

        return timerNow
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

    open fun stopAlarmSet(){
        val alarmIntent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            9,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.cancel(pendingIntent)
    }
}