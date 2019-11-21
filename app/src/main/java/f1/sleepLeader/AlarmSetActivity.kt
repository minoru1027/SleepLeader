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
import kotlin.collections.ArrayList
import kotlin.concurrent.timer


class AlarmSetActivity : AppCompatActivity() {

    private lateinit var realm : Realm
    private var musicPath : String = ""
    private var alarmRealm : Realm = Realm.getDefaultInstance()
    private var timeList : ArrayList<String> = ArrayList()
    private var boolean : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_set)

        realm = Realm.getDefaultInstance()


        val musicList = realm.where<MusicTable>().findAll()
        val alarmList = alarmRealm.where<AlarmTable>().findAll()

        musicSpinner.adapter = musicAdapter(musicList)
        musicSpinner.visibility = View.GONE
        println(alarmList.size)
        if(alarmList.size == 0){

        }else {
            for (id in 1..alarmList.size) {
                val alarmId = realm.where<AlarmTable>().equalTo("alarmId", id).findFirst()
                timeList.add(alarmId?.timer.toString())
            }
        }
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
            //設定した時間
            var time = timePicker.text.toString()
            try{
                if(timeList.size === 0) {
                    if (time.equals("起床時間を設定する")) {
                        Toast.makeText(applicationContext, "時間が入力されていません", Toast.LENGTH_LONG).show()
                        boolean = false
                    }
                }else {
                    for(setTime in timeList) {
                        if (time.equals("起床時間を設定する")) {
                            Toast.makeText(applicationContext, "時間が入力されていません", Toast.LENGTH_LONG).show()
                            boolean = false
                            break
                        }else if (time.equals(setTime)) {
                            Toast.makeText(applicationContext, "同じ時間が既に登録されています", Toast.LENGTH_LONG).show()
                            boolean = false
                            break
                        }
                    }
                }
                if(boolean){
                    realm.executeTransaction {
                        var maxId = realm.where<AlarmTable>().max("alarmId")
                        var nextId = (maxId?.toLong() ?: 0L) + 1
                        var alarm = realm.createObject<AlarmTable>(nextId)

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
                        val timer = realm.where<AlarmTable>().equalTo("timer", time).findFirst()
                        val intent = Intent(applicationContext, AlarmStopActivity::class.java)
                        intent.putExtra("activityFlag", "0")
                        intent.putExtra("setTime", alarm.timer)
                        intent.putExtra("musicFlag", alarm.musicFlag)
                        intent.putExtra("musicPath", alarm.musicPath)
                        intent.putExtra("snoozeFlag", alarm.snoozeFlag)
                        startActivity(intent)
                        }
                    }
                    boolean = true
                }catch (e:NullPointerException){
                    Toast.makeText(applicationContext, "時間が入力されていません", Toast.LENGTH_LONG).show()
                    startActivity<AlarmSetActivity>()
                }
            }
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