package f1.sleepLeader


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.media.MediaPlayer
import android.view.View
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_alarm_stop.*
import org.jetbrains.anko.act
import org.jetbrains.anko.startActivity
import java.lang.IllegalArgumentException
import java.lang.reflect.Array.get
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.max

class AlarmStopActivity : AppCompatActivity(),SensorEventListener{

    private lateinit var realm : Realm
    private lateinit var snoozeFlag : String
    private lateinit var musicFlag : String
    private var activityFlag = ""
    private var musicPath = ""
    private var timerList : HashMap<Long,String> = hashMapOf()
    private var mediaPlayer = MediaPlayer()
    private var timeList  : ArrayList<String> = arrayListOf()
    private var calendarList : ArrayList<Long> = arrayListOf()
    private var timeCount : Int = 0
    private var calendar : Calendar = Calendar.getInstance()
    private var sortTime : String = ""
    private var sortCalendar : Long = 0


    //シェイク機能の初期設定
    private var beforeX: Float = 0f
    private var beforeY: Float = 0f
    private var beforeZ: Float = 0f
    private var beforeTime: Long = -1   // 前回の時間

    private val shakeSpeed = 80f  // 振ってると判断するスピード
    private var shakeCount = 0f   // 振ってると判断した回数

    //シェイク機能のメソッド
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    //シェイク機能のメソッド
    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        when (event.sensor.type) {

            // 加速度センサーのイベントをハンドリング
            Sensor.TYPE_ACCELEROMETER -> {

                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val nowTime = System.currentTimeMillis()
                var speed = 0f

                // 最初のイベント→値を保持するのみ
                if (beforeTime == -1L) {
                    beforeX = x
                    beforeY = y
                    beforeZ = z

                    beforeTime = nowTime
                }

                // 0.5秒間隔でチェック
                val diffTime = nowTime - beforeTime
                if (diffTime < 500)


                // 前回の値との差から、スピードを算出
                // すみません、どうしてこれでOKなのか、不勉強でまだ理解出来ていません。。。とサイト主が申しておりました
                    speed = Math.abs(x + y + z - beforeX - beforeY - beforeZ) / diffTime * 10000

                // スピードがしきい値以上の場合、振ってるとみなす
                if (speed > shakeSpeed) {

                    // 振ってると判断したら、シャッフルする
                    if (++shakeCount > 25) {
                        shakeCount = 0F

                        // シャッフル
                        onStop()
                        musicPath = MusicRandom()
                        val res = this.resources
                        var soundId = res.getIdentifier(musicPath,"raw",this.packageName)
                        mediaPlayer = MediaPlayer.create(this,soundId)
                        mediaPlayer.start()

                    }
                } else {
                    // 途中でフリが収まった場合は、カウントを初期化
                    shakeCount = 0F
                }

                // 前回の値を覚える
                beforeX = x
                beforeY = y
                beforeZ = z

                beforeTime = nowTime
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_stop)
        realm = Realm.getDefaultInstance()

        nextTimer.visibility = View.GONE

        activityFlag = intent.getStringExtra("activityFlag")

        //AlarmSetActivityか、AlarmListActivityのどっちの遷移かを判定
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

                //TimeListを昇順に書き換え
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

        val sensorManager = this.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accSensor = sensorManager.getDefaultSensor(
            Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(
            this, accSensor,
            SensorManager.SENSOR_DELAY_GAME)


        //setList.forEach {
            //アラームを止めた後の処理
            switch2.setOnCheckedChangeListener {_,isChecked: Boolean ->

                if (isChecked) {
                    onStop()
                    if(snoozeFlag.equals("true")){
                        onSetSnooze()
                        snoozeFlag = "false"
                        switch2.setChecked(false)
                    }else if(snoozeFlag.equals("false") && activityFlag.equals("0")){
                        startActivity<AlarmActivity>()
                    }else if(snoozeFlag.equals("false") && activityFlag.equals("1")){
                        if(timeCount+1 > timeList.size){
                            startActivity<AlarmActivity>()
                        }else if(timeCount+1 === timeList.size){
                            nextTimer.visibility = View.GONE

                            AlarmTime.text = timeList[timeCount]

                            val timeSet = realm.where<AlarmTable>().equalTo("timer",timeList[timeCount]).findFirst()

                            snoozeFlag = timeSet?.snoozeFlag.toString()

                            timeCount++

                            switch2.setChecked(false)
                        }else{


                            AlarmTime.text = timeList[timeCount]
                            timeCount++
                            nextTimer.text = timeList[timeCount]

                            val timeSet = realm.where<AlarmTable>().equalTo("timer",timeList[timeCount]).findFirst()

                            snoozeFlag = timeSet?.snoozeFlag.toString()

                            switch2.setChecked(false)
                        }
                    }

                }else if (!isChecked){
                   /* if(snoozeFlag.equals("false") && activityFlag.equals("0")){
                        startActivity<AlarmActivity>()
                    }else if(snoozeFlag.equals("false") && activityFlag.equals("1")){
                        if(timeCount+1 > timeList.size){
                            startActivity<AlarmActivity>()
                        }else if(timeCount+1 === timeList.size){
                            nextTimer.visibility = View.GONE

                            AlarmTime.text = timeList[timeCount]

                            val timeSet = realm.where<AlarmTable>().equalTo("timer",timeList[timeCount]).findFirst()

                            snoozeFlag = timeSet?.snoozeFlag.toString()

                            timeCount++
                        }else{

                            AlarmTime.text = timeList[timeCount]
                            timeCount++
                            nextTimer.text = timeList[timeCount]

                            val timeSet = realm.where<AlarmTable>().equalTo("timer",timeList[timeCount]).findFirst()

                            snoozeFlag = timeSet?.snoozeFlag.toString()
                        }
                    }*/
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
                }
            }
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

    //シェイク機能のメソッド
    override fun onPause() {
        super.onPause()
        //センサーの終了
        val sensorManager = this.getSystemService(Context.SENSOR_SERVICE)
                as SensorManager
        sensorManager.unregisterListener(this)
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

