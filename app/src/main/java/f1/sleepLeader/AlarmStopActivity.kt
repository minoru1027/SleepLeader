package f1.sleepLeader


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_alarm_stop.*
import java.lang.reflect.Array.get
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.max

class AlarmStopActivity : AppCompatActivity(){

    private lateinit var realm : Realm
    private var timerList : HashMap<Long,String> = hashMapOf()
    private var mediaPlayer = MediaPlayer()
  
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_stop)

        realm = Realm.getDefaultInstance()

        val musicFlag = intent.getStringExtra("musicFlag")
        val musicPath = intent.getStringExtra("musicPath")
        val res = this.resources
        val path = MusicRandom()
        var soundId = res.getIdentifier(path,"raw",this.packageName)
        mediaPlayer = MediaPlayer.create(this,soundId)
        mediaPlayer.start()
    }

    override fun onResume() {
        super.onResume()

        //timerList = intent?.getSerializableExtra("timerList") as HashMap<Long, String>
        var setList = mutableListOf(timerList).toString()
        val activityFlag = intent.getStringExtra("activityFlag")
        var getPara = intent.getStringExtra("setTime")


        var table = AlarmTable()
        var Snooze =table.snoozeFlag
        var setSnooze = "OFF"

        if(Snooze.equals("ON")){
            onSetSnooze()

            table.snoozeFlag = setSnooze
        }

        setList.forEach {

//            AlarmStop.setOnCheckedChangeListener {_,isChecked: Boolean ->
//                if (isChecked) {
//                    audioStop()
//
//                    if (activityFlag.equals("9")) {
//                        AlarmTime.setText(getPara)
//
//                        val intent = Intent(applicationContext, AlarmActivity::class.java)
//                        startActivity(intent)
//
//                    } else if (activityFlag.equals("0") || activityFlag.equals("1") || activityFlag.equals("2") || activityFlag.equals("3") || activityFlag.equals("4")) {
//                        AlarmTime.setText(setList)
//
//                    } else {
//                        val intent = Intent(applicationContext, AlarmActivity::class.java)
//                        startActivity(intent)
//
//                    }
//
//                }else if (!isChecked){
//
//                }
//            }
        }
//            Return.setOnClickListener{ <-戻るボタンを押したときの処理です
//                if(activityFlag.equals("9")){
//                    alarmSetCancel()
//
//                    val intent = Intent(applicationContext, AlarmActivity::class.java)
//                    startActivity(intent)
//
//                }else{
//                    alarmListCancel()
//
//                    val intent = Intent(applicationContext, AlarmActivity::class.java)
//                    startActivity(intent)
//
//                }
//            }

//        Random.setOnClickListener{
//            if(){
//
//            }else{
//
//            }
//        }
    }

    private fun onSetSnooze(){
        var calendar: Calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE,5)

        val alarmIntent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)



    }
    //音楽を止める処理
    private fun audioStop() {
        // 再生終了
        mediaPlayer.stop()
        // リセット
        mediaPlayer.reset()
        // リソースの解放
        mediaPlayer.release()

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
        var randomId = (maxId?.toInt() ?: 0)+1
        val random = Random()
        var pathId = random.nextInt(randomId)
        while(pathId <= 0){
            pathId = (maxId?.toInt() ?: 0)+1
        }
        val musicId = realm.where<MusicTable>().equalTo("musicId",pathId).findFirst()
        val musicPath = musicId?.musicPath.toString()

        return musicPath
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

