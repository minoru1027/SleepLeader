package f1.sleepLeader


import android.annotation.TargetApi
import android.app.Activity
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.media.MediaPlayer
import android.os.Build
import android.os.CountDownTimer
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_alarm_stop.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startService
import java.lang.IllegalArgumentException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AlarmStopActivity : MediaPlayerActivity(),SensorEventListener,Application.ActivityLifecycleCallbacks{

    private lateinit var countDown : AlarmStopActivity.CountDown
    private lateinit var realm : Realm
    private lateinit var snoozeFlag : String
    private lateinit var musicFlag : String
    private var activityFlag = ""
    private var musicPath = ""
    private var firebaseFlag = ""
    private var snoozeSetFlag :Boolean  = false
    private var timerList : HashMap<Long,String> = hashMapOf()
    private var timeList  : ArrayList<String> = arrayListOf()
    private var calendarList : ArrayList<Long> = arrayListOf()
    private var timeCount : Int = 0
    private var calendar : Calendar = Calendar.getInstance()
    private var sortTime : String = ""
    private var sortCalendar : Long = 0
    private var calendarSet : Calendar = Calendar.getInstance()
    private var calendarNow : Calendar = Calendar.getInstance()
    var year = calendarSet.get(Calendar.YEAR)
    var month = calendarSet.get(Calendar.MONTH)
    var date = calendarSet.get(Calendar.DATE)
    private val dataFormat = SimpleDateFormat("mm:ss", Locale.JAPAN)

    //シェイク機能の初期設定
    private var beforeX: Float = 0f
    private var beforeY: Float = 0f
    private var beforeZ: Float = 0f
    private var beforeTime: Long = -1   // 前回の時間

    private val shakeSpeed = 80f  // 振ってると判断するスピード
    private var shakeCount = 0f   // 振ってると判断した回数

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_stop)

        realm = Realm.getDefaultInstance()

        startService(Intent(this,DestroyingService::class.java))

        nextTimer.visibility = View.GONE

        val timeNow = calendarNow.get(Calendar.HOUR_OF_DAY)

        if(timeNow >=4 && timeNow <= 8){
            background.setImageResource(R.mipmap.ohayou)
        }else if(timeNow >= 9 && timeNow <=16) {
            background.setImageResource(R.mipmap.ohirune)
        }else if(timeNow >=17 && timeNow <= 19){
            background.setImageResource(R.mipmap.yuuyake)
        }else if(timeNow >= 20 && timeNow <= 3){
            background.setImageResource(R.mipmap.yozora)
        }
        activityFlag = intent.getStringExtra("activityFlag")


        //AlarmSetActivityか、AlarmListActivityのどっちの遷移かを判定
        if(activityFlag.equals("0")){
            val setTime = intent.getStringExtra("setTime")
            snoozeFlag = intent.getStringExtra("snoozeFlag")
            musicFlag = intent.getStringExtra("musicFlag")
            if(musicFlag.equals("true")) {
                musicPath = intent.getStringExtra("musicPath")
                firebaseFlag = intent.getStringExtra("firebaseFlag")
            }

            val time = setTime?.toDate()

            setTimer(time)

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
                timeCount++
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

            setTimer(timer?.toDate())

            val timeSet = realm.where<AlarmTable>().equalTo("timer",timer).findFirst()

            snoozeFlag = timeSet?.snoozeFlag.toString()
            musicFlag = timeSet?.musicFlag.toString()
            if(musicFlag.equals("true")) {
                musicPath = timeSet?.musicPath.toString()
                firebaseFlag = timeSet?.firebaseFlag.toString()
            }
        }
        if(musicFlag.equals("false")){
            musicPath = MusicRandom()
        }
        if(firebaseFlag.equals("ON")) {
            mpStart(musicPath)
        }else {
            val res = this.resources
            var soundId = res.getIdentifier(musicPath, "raw", this.packageName)
            mediaPlayer = MediaPlayer.create(this, soundId)
            mpStart()
        }
    }
    override fun onResume() {
        super.onResume()

        val sensorManager = this.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accSensor = sensorManager.getDefaultSensor(
            Sensor.TYPE_ACCELEROMETER
        )
        sensorManager.registerListener(
            this, accSensor,
            SensorManager.SENSOR_DELAY_GAME
        )

        //setList.forEach {
        //アラームを止めた後の処理
        switch2.setOnCheckedChangeListener { _, isChecked: Boolean ->

            if (isChecked) {
                mpStop()
                bgStop()
                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(this,AlarmBroadcastReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(this, 9, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                alarmManager.cancel(pendingIntent)
                if (snoozeFlag.equals("true")) {
                    onSetSnooze()
                    snoozeFlag = "false"
                } else if (snoozeFlag.equals("false") && activityFlag.equals("0")) {
                    startActivity<AlarmActivity>()
                } else if (snoozeFlag.equals("false") && activityFlag.equals("1")) {
                    if (timeCount + 1 > timeList.size) {
                        startActivity<AlarmActivity>()
                    } else if (timeCount + 1 === timeList.size) {
                        nextTimer.visibility = View.GONE

                        AlarmTime.text = timeList[timeCount]

                        setTimer(timeList[timeCount]?.toDate())

                        val timeSet =
                            realm.where<AlarmTable>().equalTo("timer", timeList[timeCount])
                                .findFirst()

                        snoozeFlag = timeSet?.snoozeFlag.toString()

                        timeCount++


                    } else {

                        setTimer(timeList[timeCount]?.toDate())

                        AlarmTime.text = timeList[timeCount]
                        timeCount++
                        nextTimer.text = timeList[timeCount]

                        val timeSet =
                            realm.where<AlarmTable>().equalTo("timer", timeList[timeCount])
                                .findFirst()

                        snoozeFlag = timeSet?.snoozeFlag.toString()

                    }
                }
                Thread.sleep(100)

                switch2.setChecked(false)
            } else if (!isChecked) {
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        mpStop()
        try {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(this, AlarmBroadcastReceiver::class.java)
            val pendingIntent =
                PendingIntent.getBroadcast(this, 9, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.cancel(pendingIntent)
            countDown.cancel()
        }catch (e:UninitializedPropertyAccessException){
            println(e)
        }
    }

    private fun setTimer(time : Date?){
        calendarNow.time = getNow()
        calendar.time = time
        calendarSet.set(Calendar.YEAR,year)
        calendarSet.set(Calendar.MONTH,month)
        calendarSet.set(Calendar.DATE,date)

        if(calendar.timeInMillis < calendarNow.timeInMillis){
            calendar.add(Calendar.DAY_OF_MONTH+1,1)
        }

        var timeMill = (calendar.timeInMillis - calendarNow.timeInMillis).toInt()/1000

        calendarSet.set(Calendar.SECOND,timeMill)
        setAlarmManager(calendarSet)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setAlarmManager(calendar: Calendar){

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this,AlarmBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 9, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        when{
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ->{
                val info = AlarmManager.AlarmClockInfo(
                    calendar.timeInMillis,null)
                alarmManager.setAlarmClock(info, pendingIntent)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ->{
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis, pendingIntent)
            }
            else ->{
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis,null)
            }

        }
    }

    private fun onSetSnooze(){
        AlarmTime.setText(dataFormat.format(0))
        nextTimer.visibility = View.GONE
        snoozeSetFlag = true

        var calendar: Calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR,year)
        calendar.set(Calendar.MONTH,month)
        calendar.set(Calendar.DATE,date)
        calendar.add(Calendar.MINUTE,5)
        //初期値じゃい
        var cale :Calendar = Calendar.getInstance()
        cale.set(Calendar.YEAR,year)
        cale.set(Calendar.MONTH,month)
        cale.set(Calendar.DATE,date)

        var countNumber :Long = calendar.timeInMillis-cale.timeInMillis
        println(calendar.time)
        println(cale.time)
        //表示するやつの更新頻度じゃい
        val interval: Long = 10
        //カウントダウンじゃい
        countDown = CountDown(countNumber, interval)

        setAlarmManager(calendar)
        countDown.start()

    }
    internal inner class CountDown(millisInFuture: Long, countDownInterval: Long) :
        CountDownTimer(millisInFuture, countDownInterval) {

        override fun onFinish() {
            // 完了

            Toast.makeText(applicationContext, "(*´ω｀*)", Toast.LENGTH_LONG).show()
        }

        // インターバルで呼ばれる
        override fun onTick(millisUntilFinished: Long) {
            // 残り時間を分、秒、ミリ秒に分割
            //long mm = millisUntilFinished / 1000 / 60;
            //long ss = millisUntilFinished / 1000 % 60;
            //long ms = millisUntilFinished - ss * 1000 - mm * 1000 * 60;
            //timerText.setText(String.format("%1$02d:%2$02d.%3$03d", mm, ss, ms));

            AlarmTime.setText(dataFormat.format(millisUntilFinished))
        }
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
        firebaseFlag = musicId?.firebaseFlag.toString()

        return musicPath
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
    }


    override fun onDestroy() {
        super.onDestroy()
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this,AlarmBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 9, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.cancel(pendingIntent)
        try {
            mpStop()
        }catch (e:IllegalStateException){
            println(e)
        }
    }

    //以下Application.ActivityLifecycleCallbacksに必要な文
    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onActivityResumed(activity: Activity?) {
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
    }
    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {

    }
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
                    if (++shakeCount > 30) {
                        shakeCount = 0F

                        // シャッフル
                        if(snoozeSetFlag) {

                        }else{
                            mpStop()
                            musicPath = MusicRandom()

                            if(firebaseFlag.equals("ON")) {
                                mpStart(musicPath)
                            }else {
                                val res = this.resources
                                var soundId = res.getIdentifier(musicPath, "raw", this.packageName)
                                mediaPlayer = MediaPlayer.create(this, soundId)
                                mpStart()
                            }
                        }
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
}

