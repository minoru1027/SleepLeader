package f1.sleepLeader


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.media.MediaPlayer
import android.view.View
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_alarm_stop.*
import java.lang.IllegalArgumentException
import java.lang.reflect.Array.get
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.max

class AlarmStopActivity : AppCompatActivity(){

    private lateinit var realm : Realm
    private lateinit var snoozeFlag : String
    private lateinit var musicFlag : String
    private var musicPath = ""
    private var timerList : HashMap<Long,String> = hashMapOf()
    private var mediaPlayer = MediaPlayer()
    private var timeList  : ArrayList<String> = arrayListOf()
    private var calendarList : ArrayList<Long> = arrayListOf()
    private var timeCount : Int = 0
    private var calendar : Calendar = Calendar.getInstance()
    private var sortTime : String = ""
    private var sortCalendar : Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_stop)
        realm = Realm.getDefaultInstance()

        nextTimer.visibility = View.GONE

        val activityFlag = intent.getStringExtra("activityFlag")

        if(activityFlag.equals("0")){
            val setTime = intent.getStringExtra("setTime")
            snoozeFlag = intent.getStringExtra("snoozeFlag")
            musicFlag = intent.getStringExtra("musicFlag")
            if(musicFlag.equals("true")) {
                musicPath = intent.getStringExtra("musicPath")
            }

            AlarmTime.text = setTime

        }else if(activityFlag.equals("1")){
            timerList = intent?.getSerializableExtra("timerList") as HashMap<Long, String>
            for((key,value) in timerList){
                var time = timerList.get(key).toString()
                calendar.time = time?.toDate()
                timeList.add(time)
                calendarList.add(calendar.timeInMillis)
            }

            if(timeCount+1 === timeList.size){
                AlarmTime.text = timeList[timeCount]
            }else{
                nextTimer.visibility = View.VISIBLE

                for(i in 1..timeList.size-1 step 1){
                    for(j in i..timeList.size-1 step 1){
                        if(calendarList[i-1] > calendarList[j]){
                            sortTime = timeList[i-1]
                            sortCalendar = calendarList[i-1]
                            timeList[i-1] = timeList[j]
                            calendarList[i-1] = calendarList[j]
                            timeList[j] = sortTime
                            calendarList[j] = sortCalendar
                        }
                    }
                }

                AlarmTime.text = timeList[timeCount]
                timeCount++
                nextTimer.text = timeList[timeCount]

            }
            val timer = AlarmTime.text.toString()

            val timeSet = realm.where<AlarmTable>().equalTo("timer",timer).findFirst()

            snoozeFlag = timeSet?.snoozeFlag.toString()
            musicFlag = timeSet?.musicFlag.toString()
            if(musicFlag.equals("true")) {
                musicPath = timeSet?.musicPath.toString()
            }
        }
        if(musicFlag.equals("false")){
            musicPath = MusicRandom()
        }
        val res = this.resources
        var soundId = res.getIdentifier(musicPath,"raw",this.packageName)
        mediaPlayer = MediaPlayer.create(this,soundId)
        mediaPlayer.start()
    }

    override fun onResume() {
        super.onResume()
        val switch = findViewById<Switch>(R.id.switch2)

        var setList = mutableListOf(timerList).toString()

        var getPara = intent.getStringExtra("setTime")


        /*var table = AlarmTable()
        var Snooze =table.snoozeFlag
        var setSnooze = "OFF"

        if(Snooze.equals("ON")){
            onSetSnooze()

            table.snoozeFlag = setSnooze
        }*/

        //setList.forEach {

            switch2.setOnCheckedChangeListener {_,isChecked: Boolean ->

                if (isChecked) {
                    onStop()
                    if(snoozeFlag.equals("true")){
                        onSetSnooze()
                    }
                }else if (!isChecked){
                    

                }
                    /*if (activityFlag.equals("9")) {
                        AlarmTime.setText(getPara)

                        val intent = Intent(applicationContext, AlarmActivity::class.java)
                        startActivity(intent)

                    } else if (activityFlag.equals("0") || activityFlag.equals("1") || activityFlag.equals("2") || activityFlag.equals("3") || activityFlag.equals("4")) {
                        AlarmTime.setText(setList)

                    } else {
                        val intent = Intent(applicationContext, AlarmActivity::class.java)
                        startActivity(intent)

                    }*/



           // }
        }
/*            Return.setOnClickListener{ <-戻るボタンを押したときの処理です
                if(activityFlag.equals("9")){
                    alarmSetCancel()

                    val intent = Intent(applicationContext, AlarmActivity::class.java)
                    startActivity(intent)

                }else{
                    alarmListCancel()

                    val intent = Intent(applicationContext, AlarmActivity::class.java)
                    startActivity(intent)

                }
            }

        Random.setOnClickListener{
            if(){

            }else{

            }
        }*/

    }

    private fun onSetSnooze(){
        var calendar: Calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE,5)

        val alarmIntent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        println("スヌーズ機能")
    }

    private fun alarmSetCancel(){
        val alarmSet = AlarmSetActivity()
        alarmSet.stopAlarmSet()

    }

    private fun alarmListCancel(){
        val alarmList = AlarmListActivity()
        alarmList.stopAlarmList()

    }

    private fun MusicRandom() : String{
        var maxId = realm.where<MusicTable>().max("musicId")
        var randomId = (maxId?.toInt() ?: 0)
        println(randomId)
        val random = Random()
        var pathId = random.nextInt(randomId)
        while(pathId < 1){
            pathId = (maxId?.toInt() ?: 0)
        }
        val musicId = realm.where<MusicTable>().equalTo("musicId",pathId).findFirst()
        val musicPath = musicId?.musicPath.toString()

        return musicPath
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

    override fun onStop() {
        super.onStop()
        try {
            mediaPlayer.stop()
            mediaPlayer.reset()
            mediaPlayer.release()
        }catch (e:IllegalStateException){
            println(e)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        try {
            mediaPlayer.stop()
            mediaPlayer.reset()
            mediaPlayer.release()
        }catch (e:IllegalStateException){
            println(e)
        }
    }

}

