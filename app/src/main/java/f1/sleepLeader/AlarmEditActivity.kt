package f1.sleepLeader

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_alarm_set.*
import org.jetbrains.anko.startActivity
import java.lang.IllegalArgumentException
import java.lang.NullPointerException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AlarmEditActivity : AppCompatActivity() {

    private lateinit var realm : Realm
    private var musicPath : String = ""
    private var firebaseFlag = ""
    private var alarmRealm : Realm = Realm.getDefaultInstance()
    private var selectId : Long = 1
    private var timeList : ArrayList<String> = arrayListOf()
    private var boolean = true
    private var setedTime = ""
    private var selectedTime =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_edit)

        realm = Realm.getDefaultInstance()

        val musicList = realm.where<MusicTable>().findAll()
        val alarmList = alarmRealm.where<AlarmTable>().findAll()
        for (id in 1..alarmList.size) {
            val alarmId = realm.where<AlarmTable>().equalTo("alarmId", id).findFirst()
            timeList.add(alarmId?.timer.toString())
        }

        musicSpinner.adapter = musicAdapter(musicList)
        musicSpinner.visibility = View.GONE

        selectId = intent.getLongExtra("alarmId",0)

        val select = realm.where<AlarmTable>().equalTo("alarmId",selectId).findFirst()
        selectedTime = select?.timer.toString()

        timePicker.setText(select?.timer)

        if(select?.snoozeFlag.equals("true")){
            snoozeFlag.setChecked(true)
        }

        if(select?.musicFlag.equals("true")){
            musicFlag.setChecked(true)
            musicSpinner.visibility = View.VISIBLE
            val selectMusic = alarmRealm.where<MusicTable>().equalTo("musicPath",select?.musicPath).findFirst()
            val selectMusicId = selectMusic?.musicId!!.toInt()
            musicSpinner.setSelection(selectMusicId-1)
        }
    }

    override fun onResume() {
        super.onResume()

        timePicker.setOnClickListener {
            val newFragment = TimePickerFragment()
            newFragment.show(supportFragmentManager, "Time Picker")
            AlarmSetting.visibility = View.GONE
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
                firebaseFlag = musicPosition.firebaseFlag

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        AlarmSetting.setOnClickListener {
            var time = timePicker.text.toString()
            try{
                if(time.equals(selectedTime)) {

                }else if(time.equals(setedTime)){
                    Toast.makeText(applicationContext, "同じ時間が既に登録されています", Toast.LENGTH_LONG).show()
                    boolean = false
                }else {
                    for (setTime in timeList) {
                        if (time.equals(setTime)) {
                            Toast.makeText(
                                applicationContext,
                                "同じ時間が既に登録されています",
                                Toast.LENGTH_LONG
                            ).show()
                            boolean = false
                            break
                   }
              }
        }
                if (boolean) {
                        realm.executeTransaction {
                            var alarm = realm.where<AlarmTable>().equalTo("alarmId",selectId).findFirst()
                            intent.putExtra("setTime", time)
                            alarm?.timer = time
                            setedTime = time

                            //スヌーズ設定
                            var snooze = snoozeFlag.isChecked.toString()
                            alarm?.snoozeFlag = snooze

                            //音楽の再生設定
                            var music = musicFlag.isChecked.toString()
                            println(music)
                            alarm?.musicFlag = music

                            //音楽のファイルパス
                            alarm?.musicPath = musicPath
                            alarm?.firebaseFlag = firebaseFlag

                            val timer =
                                realm.where<AlarmTable>().equalTo("timer", time).findFirst()
                            val intent =
                                Intent(applicationContext, AlarmListActivity::class.java)
                            Toast.makeText(applicationContext, "編集が終わったでー", Toast.LENGTH_LONG)
                                .show()
                            startActivity(intent)
                        }
                    }
                boolean = true
            }catch (e: NullPointerException){
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