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
import org.jetbrains.anko.startActivity
import java.lang.RuntimeException

class MusicActivity : MediaPlayerActivity(){

    private lateinit var player: MediaPlayer
    private lateinit var musicRealm: Realm
    private lateinit var musicAlarmRealm: Realm
    private var sId : Int = 0
    private var name = ""
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
        button.setOnClickListener{
            startActivity<MusicDownLoadActivity>()
        }
        musicListView.setOnItemClickListener{ parent, view, position, id ->
            val musicListPosition = parent.getItemAtPosition(position) as MusicTable
            alarmFlag = true
            if(mediaPlayer.isPlaying()|| mediaAlarmPlayer.isPlaying()&&name.equals(musicListPosition.musicName)){
                mpStop()
                name = ""
            }else if(mediaPlayer.isPlaying()|| mediaAlarmPlayer.isPlaying()){
                mpStop()
                if(musicListPosition.firebaseFlag.equals("ON")){
                    name = musicListPosition.musicName
                    mpStart(musicListPosition.musicPath)
                }else {
                    val res = this.resources
                    var soundId = res.getIdentifier(
                        musicListPosition.musicPath,
                        "raw",
                        this.packageName
                    )
                    name = musicListPosition.musicName
                    mediaAlarmPlayer = MediaPlayer.create(this, soundId)
                    mediaAlarmPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
                    mpStart(this)
                }
            }else if (musicListPosition.firebaseFlag.equals("ON")) {
                mpStart(musicListPosition.musicPath)
                name = musicListPosition.musicName
            }else {
                    val res = this.resources
                    var soundId = res.getIdentifier(
                        musicListPosition.musicPath,
                        "raw",
                        this.packageName
                    )
                    name = musicListPosition.musicName
                    mediaAlarmPlayer = MediaPlayer.create(this, soundId)
                    mediaAlarmPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
                    mpStart(this)
            }
        }


        musicAlarmListView.setOnItemClickListener{ parent, view, position, id ->
            val musicAlarmListPosition = parent.getItemAtPosition(position) as MusicAlarmTable
            alarmFlag = true
            if(mediaPlayer.isPlaying()|| mediaAlarmPlayer.isPlaying()&&name.equals(musicAlarmListPosition.musicAlarmName)){
                mpStop()
                name =""
            }else if(mediaPlayer.isPlaying()|| mediaAlarmPlayer.isPlaying()){
                mpStop()
                if(musicAlarmListPosition.firebaseAlarmFlag.equals("ON")){
                    mpStart(this,musicAlarmListPosition.musicAlarmPath)
                    name = musicAlarmListPosition.musicAlarmName

                }else {
                    val res = this.resources
                    var soundId = res.getIdentifier(
                        musicAlarmListPosition.musicAlarmPath,
                        "raw",
                        this.packageName
                    )
                    name = musicAlarmListPosition.musicAlarmName
                    mediaAlarmPlayer = MediaPlayer.create(this, soundId)
                    mediaAlarmPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
                    mpStart(this)
                }
            }else if(musicAlarmListPosition.firebaseAlarmFlag.equals("ON")){
                mpStart(musicAlarmListPosition.musicAlarmPath)
                name = musicAlarmListPosition.musicAlarmName
            }else {
                val res = this.resources
                var soundId = res.getIdentifier(musicAlarmListPosition.musicAlarmPath,"raw",this.packageName)
                name = musicAlarmListPosition.musicAlarmName
                mediaAlarmPlayer = MediaPlayer.create(this, soundId)
                mediaAlarmPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
                mpStart(this)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            mpStop()
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
