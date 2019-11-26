package f1.sleepLeader

//import android.support.v7.app.AppCompatActivity
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import io.realm.kotlin.where
import java.util.Calendar
import kotlinx.android.synthetic.main.activity_music.*
import java.lang.RuntimeException

class MusicActivity : MediaPlayerActivity(){

    private lateinit var player: MediaPlayer
    private lateinit var musicRealm: Realm
    private lateinit var musicAlarmRealm: Realm
    private var sId : Int = 0
    private var musicFlag = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)

        musicRealm = Realm.getDefaultInstance()
        musicAlarmRealm = Realm.getDefaultInstance()

        val musicList = musicRealm.where<MusicTable>().findAll()
        val musicAlarmList = musicAlarmRealm.where<MusicAlarmTable>().findAll()

        musicListView.adapter = musicAdapter(musicList)
        musicAlarmListView.adapter = musicAlarmAdapter(musicAlarmList)

    }

    override fun onResume() {
        super.onResume()
        musicListView.setOnItemClickListener{ parent, view, position, id ->
                val musicListPosition = parent.getItemAtPosition(position) as MusicTable
                val res = this.resources
                var soundId = res.getIdentifier(musicListPosition.musicPath,"raw",this.packageName)
                if(musicFlag == true){
                mpStop()
                musicFlag = false
            }
            if(soundId === sId){
                sId = 0
            }else {
                mediaPlayer = MediaPlayer.create(this, soundId)
                mpStart()
                musicFlag = true
                sId = soundId
            }
        }
        musicAlarmListView.setOnItemClickListener{ parent, view, position, id ->
            val musicAlarmListPosition = parent.getItemAtPosition(position) as MusicAlarmTable
            val res = this.resources
            var soundId = res.getIdentifier(musicAlarmListPosition.musicAlarmPath,"raw",this.packageName)
            if(musicFlag == true){
                mpStop()
                musicFlag = false
            }
            if(soundId === sId){
                sId = 0
            }else {
                mediaPlayer = MediaPlayer.create(this, soundId)
                mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
                mpStart(this)
                musicFlag = true
                sId = soundId
            }
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            mpStop()
            musicFlag = false
        }catch (e : IllegalStateException){
            println(e)
        }catch (e: RuntimeException){
            println(e)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        try {
            mpStop()
            musicRealm.close()
            musicAlarmRealm.close()
            musicFlag = false
        }catch(e : java.lang.IllegalStateException){
            println(e)
        }catch(e : RuntimeException){
            println(e)
        }
    }
}
