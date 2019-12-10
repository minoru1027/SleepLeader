package f1.sleepLeader

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
//import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import org.jetbrains.anko.startActivity

class AlarmActivity : MediaPlayerActivity() {

    private lateinit var musicRealm : Realm
    private lateinit var musicAlarmRealm : Realm
    private var memoryRealm : Realm = Realm.getDefaultInstance()
    private val Id :Long = 1
    private val alarmId : Long = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playTime = 0
        bgplayTime = 0
        musicFlag = false
        alarmFlag = false
        musicRealm = Realm.getDefaultInstance()
        musicAlarmRealm = Realm.getDefaultInstance()

        val musicTable = musicRealm.where<MusicTable>().equalTo("musicId",Id).findFirst()
        val musicAlarmTable = musicAlarmRealm.where<MusicAlarmTable>().equalTo("musicAlarmId",alarmId).findFirst()
        var musicid = musicTable?.musicId
        var musicAlarmId = musicAlarmTable?.musicAlarmId
        val firebaseFlag : Array<String> = arrayOf("OFF","OFF","OFF","OFF","OFF")
        when(musicid){
             null->{
                 val musicName : Array<String> = arrayOf("焚火","水中","雨","大きな波","森林浴")
                 val musicPath : Array<String> = arrayOf("fire","bubble","rain","sleep_wave","sinrinyoku")
                 val playTime : Array<Long> = arrayOf(14000,10000,59000,121000,164000)
                 var i = 0
                 while(i != musicName.size){
                     if(i == musicName.size){
                         break
                     }else{
                         musicRealm.executeTransaction {
                            var maxId = musicRealm.where<MusicTable>().max("musicId")
                            var nextId = (maxId?.toLong() ?: 0L) + 1
                            var music = musicRealm.createObject<MusicTable>(nextId)
                            music.musicName = musicName[i]
                            music.musicPath = musicPath[i]
                             music.playTime = playTime[i]
                             music.firebaseFlag = firebaseFlag[i]
                             i++
                        }
                    }
                 }
            }
        }

        when(musicAlarmId){
            null->{
                val musicAlarmName : Array<String> = arrayOf("ニワトリ","朝食を作る","サイレン","小さい波","爆笑")
                val musicAlarmPath : Array<String> = arrayOf("niwatori","breakfast","sairen","wave","bakusyou")
                val playAlarmTime : Array<Long> = arrayOf(2000,32000,4000,10000,6000)
                var i = 0
                while(i != musicAlarmName.size){
                    if(i == musicAlarmName.size){
                        break
                    }else{
                        musicAlarmRealm.executeTransaction {
                            var maxId = musicAlarmRealm.where<MusicAlarmTable>().max("musicAlarmId")
                            var nextId = (maxId?.toLong() ?: 0L) + 1
                            var music = musicAlarmRealm.createObject<MusicAlarmTable>(nextId)
                            music.musicAlarmName = musicAlarmName[i]
                            music.musicAlarmPath = musicAlarmPath[i]
                            music.playAlarmTime = playAlarmTime[i]
                            music.firebaseAlarmFlag = firebaseFlag[i]
                            i++
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        clockButton.setOnClickListener{
            val intent = Intent(this,AlarmSetActivity::class.java)
            startActivity(intent)
        }

        musicButton.setOnClickListener{
            val intent = Intent(this,MusicActivity::class.java)
            startActivity(intent)
        }

        listButton.setOnClickListener{
            val intent = Intent(this,AlarmListActivity::class.java)
            startActivity(intent)
        }

        AlarmMinute.setOnClickListener{
            startActivity<AlarmStopActivity>("activityFlag" to "3")
        }

        AlarmMemory.setOnClickListener {
            val memoryList = memoryRealm.where<AlarmMemoryTable>().findAll()
            if(memoryList.size == 0){
                Toast.makeText(this, "前回のアラームの値が存在していません", Toast.LENGTH_LONG).show()
            }else {
                startActivity<AlarmStopActivity>("activityFlag" to "4")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        musicRealm.close()
        musicAlarmRealm.close()
    }
}
