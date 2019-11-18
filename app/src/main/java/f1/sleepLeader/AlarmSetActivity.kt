package f1.sleepLeader


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
//import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_alarm_set.*
import kotlinx.android.synthetic.main.activity_main.view.*
import org.jetbrains.anko.startActivity
import java.lang.IllegalArgumentException
import java.lang.NullPointerException
import java.lang.invoke.MutableCallSite
import java.lang.reflect.Array.get
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timer


class AlarmSetActivity : AppCompatActivity() {

    private lateinit var realm : Realm
    private var musicPath : String = ""
    private var requestCodeSet = 100

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
            try{
                var calendar: Calendar = Calendar.getInstance()
                var calendarNow : Calendar = Calendar.getInstance()

                realm.executeTransaction {
                    var maxId = realm.where<AlarmTable>().max("alarmId")
                    var nextId = (maxId?.toLong() ?: 0L) + 1
                    var alarm = realm.createObject<AlarmTable>(nextId)

                    //設定した時間
                    var time = timePicker.text.toString()



                    intent.putExtra("setTime", time)
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
                    val calendarSet = Calendar.getInstance()
                    var year = calendarSet.get(Calendar.YEAR)
                    var month = calendarSet.get(Calendar.MONTH)
                    var date = calendarSet.get(Calendar.DATE)
                    calendarSet.set(Calendar.YEAR,year)
                    calendarSet.set(Calendar.MONTH,month)
                    calendarSet.set(Calendar.DATE,date)

                    if(calendar.timeInMillis < calendarNow.timeInMillis){
                        calendar.add(Calendar.DAY_OF_MONTH+1,1)
                    }

                    var timeMill = (calendar.timeInMillis - calendarNow.timeInMillis).toInt()/1000

                    calendarSet.set(Calendar.SECOND,timeMill)

                    println(calendarSet.time)
                    //if (SetTime.length() != 0 && SetMinute.length() != 0){
                        val timer = realm.where<AlarmTable>().equalTo("timer",time).findFirst()

                        if(timer != null){
                            //var sec = setSecond()
                            //calendar.add(Calendar.SECOND, sec)

                            val alarmIntent = Intent(this, AlarmReceiver::class.java)
                            val pendingIntent = PendingIntent.getBroadcast(
                                this,
                                requestCodeSet,
                                alarmIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                            )
                            val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                            manager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

                            val intent = Intent(applicationContext, AlarmStopActivity::class.java)

                            var activity = requestCodeSet
                            intent.putExtra("activityFlag", "0")
                            intent.putExtra("setTime",alarm.timer)
                            intent.putExtra("musicFlag",alarm.musicFlag)
                            intent.putExtra("musicPath",alarm.musicPath)
                            intent.putExtra("snoozeFlag",alarm.snoozeFlag)

                            //incSetCode()
                            startActivity(intent)
                        }
                    //}else{
                        //エラー処理
                        /*if (SetTime.length() == 0 && SetMinute.length() == 0){
                            HourEmptyException()
                            MiniteEmptyException()

                        }else if(SetTime.length() == 0){
                            HourEmptyException()

                        }else if(SetMinute.length() == 0){
                            MiniteEmptyException()

                        }*/
                    }
                }catch (e:NullPointerException){
                    Toast.makeText(applicationContext, "時間が入力されていません", Toast.LENGTH_LONG).show()
                    startActivity<AlarmSetActivity>()
                }
            }
        }
    /*private fun setHour(): Int {

        val calender: Calendar = Calendar.getInstance()
        var hourNow = calender.get(Calendar.HOUR_OF_DAY)
        var hourSet = SetTime.text.toString().toInt()
        var diffHour = hourSet - hourNow

            if (diffHour < 0) {
                diffHour *= -1
                diffHour -= 24

            } else if (diffHour > 0) {
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

    }*/
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

    /*open fun stopAlarmSet(){
        val alarmIntent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            requestCodeSet,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.cancel(pendingIntent)

    }

    open fun incSetCode(){
        //アラームセットのリクエストコードの値を1個増やすよー
        requestCodeSet +=1
    }

    private fun HourEmptyException(){
        Toast.makeText(applicationContext, "時間が入力されていません", Toast.LENGTH_LONG).show()

    }
    private fun MiniteEmptyException(){
        Toast.makeText(applicationContext, "分が入力されていません", Toast.LENGTH_LONG).show()

    }*/
}