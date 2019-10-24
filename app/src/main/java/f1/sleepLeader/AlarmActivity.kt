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

    private lateinit var realm : Realm
    private val Id :Long = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        realm = Realm.getDefaultInstance()

        val musicTable = realm.where<MusicTable>().equalTo("musicId",Id).findFirst()
        var musicid = musicTable?.musicId
        when(musicid){
             null->{
                 val testName : Array<String> = arrayOf("abc","de","f")
                 val testPass : Array<String> = arrayOf("raw/abc","raw/de","raw/f")
                 var i = 0
                 realm.executeTransaction {
                     var maxId = realm.where<MusicTable>().max("musicId")
                     var nextId = (maxId?.toLong() ?: 0L) +1
                     var music = realm.createObject<MusicTable>(nextId)
                         while (i != 2) {
                             music.musicName=testName[i]
                             music.musicPath=testPass[i]
                             i++
                        }
                 }
            }else ->{
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
        realm.close()
    }
}
