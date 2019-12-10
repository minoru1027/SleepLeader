package f1.sleepLeader

//import android.support.v7.app.AppCompatActivity
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import io.realm.kotlin.where
import java.util.Calendar
import kotlinx.android.synthetic.main.activity_music.*
import org.jetbrains.anko.startActivity
import org.w3c.dom.Text
import java.lang.RuntimeException

class MusicActivity : MediaPlayerActivity(){

    private lateinit var musicRealm: Realm
    private lateinit var musicAlarmRealm: Realm
    private var positioned = 0
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
            /*if(positioned != 0){
                selectedNamed.setTextColor(Color.rgb(211,211,211))
            }
            val color = view.findViewById<TextView>(android.R.id.text1)
            selectedNamed = color
            color.setTextColor(Color.rgb(152,217,142))*/
            val musicListPosition = parent.getItemAtPosition(position) as MusicTable
            alarmFlag = true
            if(mediaPlayer.isPlaying()|| mediaAlarmPlayer.isPlaying()&&name.equals(musicListPosition.musicName)){
                mpStop()
                playTime = 0
                name = ""
            }else if(mediaPlayer.isPlaying()|| mediaAlarmPlayer.isPlaying()){
                mpStop()
                playTime = 0
                if(musicListPosition.firebaseFlag.equals("ON")){
                    name = musicListPosition.musicName
                    bgplayTime += musicListPosition.playTime
                    mpStart(musicListPosition.musicPath)
                }else {
                    val res = this.resources
                    var soundId = res.getIdentifier(
                        musicListPosition.musicPath,
                        "raw",
                        this.packageName
                    )
                    name = musicListPosition.musicName
                    bgplayTime += musicListPosition.playTime
                    mediaAlarmPlayer = MediaPlayer.create(this, soundId)
                    mediaAlarmPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
                    mpStart(this)
                }
            }else if (musicListPosition.firebaseFlag.equals("ON")) {
                mpStart(musicListPosition.musicPath)
                bgplayTime += musicListPosition.playTime
                name = musicListPosition.musicName
            }else {
                    val res = this.resources
                    var soundId = res.getIdentifier(
                        musicListPosition.musicPath,
                        "raw",
                        this.packageName
                    )
                    name = musicListPosition.musicName
                    bgplayTime += musicListPosition.playTime
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
                playTime = 0
                name =""
            }else if(mediaPlayer.isPlaying()|| mediaAlarmPlayer.isPlaying()){
                mpStop()
                playTime = 0
                if(musicAlarmListPosition.firebaseAlarmFlag.equals("ON")){
                    mpStart(this,musicAlarmListPosition.musicAlarmPath)
                    bgplayTime += musicAlarmListPosition.playAlarmTime
                    name = musicAlarmListPosition.musicAlarmName

                }else {
                    val res = this.resources
                    var soundId = res.getIdentifier(
                        musicAlarmListPosition.musicAlarmPath,
                        "raw",
                        this.packageName
                    )
                    name = musicAlarmListPosition.musicAlarmName
                    bgplayTime += musicAlarmListPosition.playAlarmTime
                    mediaAlarmPlayer = MediaPlayer.create(this, soundId)
                    mediaAlarmPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
                    mpStart(this)
                }
            }else if(musicAlarmListPosition.firebaseAlarmFlag.equals("ON")){
                mpStart(musicAlarmListPosition.musicAlarmPath)
                bgplayTime += musicAlarmListPosition.playAlarmTime
                name = musicAlarmListPosition.musicAlarmName
            }else {
                val res = this.resources
                var soundId = res.getIdentifier(musicAlarmListPosition.musicAlarmPath,"raw",this.packageName)
                bgplayTime += musicAlarmListPosition.playAlarmTime
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
