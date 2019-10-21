package f1.sleepLeader

import android.annotation.TargetApi
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.CalendarContract
import android.support.v7.app.AppCompatViewInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_alarm_list.*
import org.jetbrains.anko.startActivity
import java.lang.IllegalArgumentException
import java.text.FieldPosition
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AlarmListActivity : AppCompatActivity() {

    private lateinit var realm : Realm
    private var timerList : HashMap<Long,String> = hashMapOf()
    private lateinit var timer :String
    private  var time: ArrayList<Calendar> = arrayListOf()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_list)

        realm = Realm.getDefaultInstance()

        /*realm.executeTransaction{
            var maxId = realm.where<AlarmTable>().max("alarmId")
            var nextId = (maxId?.toLong() ?: 0L) +1
            var alarm = realm.createObject<AlarmTable>(nextId)
            alarm.timer="12:08"
            alarm.musicPath="www"
            alarm.musicFlag="ON"
            alarm.snoozeFlag="ON"
        }*/
        val alarmList = realm.where<AlarmTable>().findAll()
        listView.adapter = alarmListAdapter(alarmList)
    }

    override fun onResume() {
        super.onResume()
        listView.setOnItemClickListener { parent, view, position, id ->
            val timePosition = parent.getItemAtPosition(position) as AlarmTable
            val alarmId = timePosition.alarmId
            timer = timePosition.timer
            selectedTimer(alarmId,timer)

        }

        alarmStopButtom.setOnClickListener{

            val calendar = Calendar.getInstance()
            val calendarNow = Calendar.getInstance()
            val timeNow= getNow()
            calendarNow.time = timeNow
            for((key,value) in timerList){
                val timeSet = timerList.get(key)?.toDate()
                calendar.time = timeSet
                if(calendar.timeInMillis < calendarNow.timeInMillis){
                    calendar.add(Calendar.DAY_OF_MONTH+1,1)
                }
                var timeMill = calendar.timeInMillis - calendarNow.timeInMillis
                calendar.timeInMillis = timeMill
                println(calendar.timeInMillis)
                setAlarmManager(calendar)
                time.add(calendar)
            }
            startActivity<AlarmStopActivity>("timerList" to timerList)
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setAlarmManager(calendar: Calendar){
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this,AlarmBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)
        when{
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ->{
                val info = AlarmManager.AlarmClockInfo(
                    calendar.timeInMillis,null)
                alarmManager.setAlarmClock(info,pendingIntent)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ->{
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,pendingIntent)
            }
            else ->{
                alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.timeInMillis,null)
            }

        }
    }

    private fun selectedTimer(alarmId : Long,timer : String) {

        if(alarmId != null){
            if(timerList.get(alarmId)==timer){
                 timerList.remove(alarmId)
            }else{
                timerList.put(alarmId,timer)
            }
        }
    }
     private fun String.toDate(time : String = "HH:mm") : Date?{
         val sdTimer = try{
             SimpleDateFormat(time)
         }catch (e:IllegalArgumentException){
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
     private fun getNow() : Date?{
         val calendarNow = Calendar.getInstance()
         val hour  = calendarNow.get(Calendar.HOUR_OF_DAY).toString()
         var minute = calendarNow.get(Calendar.MINUTE).toString()
         val timeNow = hour+":"+minute
         val timerNow = timeNow.toDate()

         return timerNow
     }
     override fun onDestroy() {
         super.onDestroy()
         realm.close()
     }
}
