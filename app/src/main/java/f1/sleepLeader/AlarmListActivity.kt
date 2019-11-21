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
    private lateinit var calendarSet: Calendar
    private lateinit var timer :String
    private var requestCodeList = 200
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
        println(alarmList)
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

        listView.setOnItemLongClickListener{ parent, view, position, id ->
            val timePosition = parent.getItemAtPosition(position) as timerData
            val alarmId = timePosition.alarmId.toLong()
            startActivity<AlarmEditActivity>("alarmId" to alarmId)
            true
        }

        alarmStopButtom.setOnClickListener{
            startActivity<AlarmStopActivity>("timerList" to timerList,"activityFlag" to "1")
        }
    }

    private fun sortList(alarmList : RealmResults<AlarmTable>){

        var sortId :Int
        var sortTime :String
        var sortCalendar : Long

        for(id in 1..alarmList.size){
            val alarmId = realm.where<AlarmTable>().equalTo("alarmId",id).findFirst()
                calendar = Calendar.getInstance()
                val t = alarmId?.timer?.toDate()
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

     override fun onDestroy() {
         super.onDestroy()
         realm.close()
     }

}
