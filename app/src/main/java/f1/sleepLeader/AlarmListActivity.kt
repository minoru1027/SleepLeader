package f1.sleepLeader

import android.annotation.TargetApi
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
//import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
//import android.support.v7.app.AppCompatViewInflater
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_alarm_list.*
import org.jetbrains.anko.startActivity
import java.lang.IllegalArgumentException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AlarmListActivity : AppCompatActivity() {

    private lateinit var realm : Realm
    private var timerList : HashMap<Long,String> = hashMapOf()
    private lateinit  var calendar : Calendar
    private lateinit var timer :String
    private lateinit var snoozeFlag:String
    private lateinit var musicFlag : String
    private lateinit var musicPath :String
    private var time: ArrayList<Calendar> = arrayListOf()
    private var idList : ArrayList<Int> = arrayListOf()
    private var timeList : ArrayList<String> = arrayListOf()
    private var calendarList : ArrayList<Long> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_list)

        realm = Realm.getDefaultInstance()

        val alarmList = realm.where<AlarmTable>().findAll()
      
        sortList(alarmList)

    }

    override fun onResume() {
        super.onResume()
        listView.setOnItemClickListener { parent, view, position, id ->
            val timePosition = parent.getItemAtPosition(position) as timerData
            val alarmId = timePosition.alarmId.toLong()
            timer = timePosition.alarmTime

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

                setAlarmManager(calendar)
                time.add(calendar)
            }
            startActivity<AlarmStopActivity>("timerList" to timerList,"activityFlag" to "1")
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setAlarmManager(calendar: Calendar){

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this,AlarmBroadcastReceiver::class.java)
                                                            //多分、これ↓
        val pendingIntent = PendingIntent.getBroadcast(this,9,intent,PendingIntent.FLAG_UPDATE_CURRENT)
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

    private fun sortList(alarmList : RealmResults<AlarmTable>){

        var sortId :Int
        var sortTime :String
        var sortCalendar : Long

        for(id in 1..alarmList.size){
            val alarmId = realm.where<AlarmTable>().equalTo("alarmId",id).findFirst()
            calendar = Calendar.getInstance()
            val t =alarmId?.timer?.toDate()
            calendar.time = t

            idList.add(id)
            timeList.add(alarmId?.timer.toString())
            calendarList.add(calendar.timeInMillis)
        }

        for(i in 1..idList.size-1 step 1){
            for(j in i..idList.size-1 step 1){
                if(calendarList[i-1] > calendarList[j]){
                    sortId = idList[i-1]
                    sortTime = timeList[i-1]
                    sortCalendar = calendarList[i-1]
                    idList[i-1] = idList[j]
                    timeList[i-1] = timeList[j]
                    calendarList[i-1] = calendarList[j]
                    idList[j] = sortId
                    timeList[j] = sortTime
                    calendarList[j] = sortCalendar
                }
            }
        }

        val alarmTimeList = List(alarmList.size){i ->timerData(idList[i],timeList[i])}
        listView.adapter = alarmListAdapter(this,alarmTimeList)
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

    private fun requestCode():Int{
        var requestCode = 0

        return requestCode++
    }

    open fun stopAlarmList(){
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(this,requestCode(),intent,PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager.cancel(pendingIntent)

    }
}
