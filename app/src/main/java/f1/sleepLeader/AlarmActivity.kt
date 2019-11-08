package f1.sleepLeader

import android.content.Intent
//import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where

class AlarmActivity : AppCompatActivity() {

    private lateinit var musicRealm : Realm
    private lateinit var musicAlarmRealm : Realm
    private val Id :Long = 1
    private val alarmId : Long = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        musicRealm = Realm.getDefaultInstance()
        musicAlarmRealm = Realm.getDefaultInstance()

        val musicTable = musicRealm.where<MusicTable>().equalTo("musicId",Id).findFirst()
        val musicAlarmTable = musicAlarmRealm.where<MusicAlarmTable>().equalTo("musicAlarmId",alarmId).findFirst()
        var musicid = musicTable?.musicId
        var musicAlarmId = musicAlarmTable?.musicAlarmId

        when(musicid){
             null->{
                 val musicName : Array<String> = arrayOf("日の陰り","Moon","静止した宇宙","夜空に舞う鳥","夕べの星")
                 val musicPath : Array<String> = arrayOf("hinokageri","moon","seishishitauchu","yozoranimautori","yuubenohoshi")
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
                            i++
                        }
                    }
                 }
            }
        }

        when(musicAlarmId){
            null->{
                val musicAlarmName : Array<String> = arrayOf("チョコレート大作戦","ファミポップⅣ","軽序曲","長靴でお出かけ","吹奏楽部の夏")
                val musicAlarmPath : Array<String> = arrayOf("chocolatedaisakusen","famipop4","keijokyoku","nagagutsudeodekake","suisougakubunonatsu")
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
    }

    override fun onDestroy() {
        super.onDestroy()
        musicRealm.close()
        musicAlarmRealm.close()
    }
}
