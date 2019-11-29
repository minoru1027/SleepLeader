package f1.sleepLeader

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException
import java.lang.IllegalStateException
import java.util.*

open class MediaPlayerActivity: AppCompatActivity(),MediaPlayer.OnCompletionListener{

    private var volume : Float = 0.7f
    private var alarmVolume : Float = 0.4f
    private var calendar : Calendar = Calendar.getInstance()
    private var playTime : Int = 0
    private var calendarTime : Calendar = Calendar.getInstance()
    private var alarmFlag :Boolean = false
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference
    companion object {
        @JvmField
        var mediaPlayer : MediaPlayer = MediaPlayer()
        var mediaAlarmPlayer : MediaPlayer = MediaPlayer()
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        calendarTime.timeInMillis = 1800000
    }
    fun mpStart(){
        try {
            mediaPlayer.setVolume(volume,volume)
            mediaPlayer.setOnCompletionListener(this)
            mediaPlayer.start()
        }catch (e : IOException){
            Toast.makeText(this, "音楽処理時にエラー発生", Toast.LENGTH_LONG).show()
        }
    }

    fun mpStart(musicPath : String){
            val path = "Music/" + musicPath
            val dir = storageRef.child(path.trim())

            dir.downloadUrl.addOnSuccessListener { uri ->
                Log.i("Test", "3")
                val url = uri.toString()
                println(uri)
                mediaPlayer.setDataSource(url)
                mediaPlayer.setOnCompletionListener(this)
                mediaPlayer.setOnPreparedListener(this::onPrepared)
                mediaPlayer.prepare()
                Thread.sleep(500)

                if (mediaAlarmPlayer.isPlaying()) {
                    mediaPlayer.reset()
                }

            }.addOnFailureListener {}
    }
    fun mpStart(context: Context){
        try {
            mediaAlarmPlayer.setVolume(alarmVolume,alarmVolume)
            mediaAlarmPlayer.setOnCompletionListener(this)
            mediaAlarmPlayer.start()
            alarmFlag = true
        }catch (e : IOException){
            Toast.makeText(context, "音楽処理時にエラー発生", Toast.LENGTH_LONG).show()
        }
    }
    fun mpStart(context: Context,musicPath: String){
        val path = "Music/"+musicPath
        val dir = storageRef.child(path.trim())
        dir.downloadUrl.addOnSuccessListener {
                uri ->
            Log.i("Test", "3")
            val url = uri.toString()
            println(url)
            mediaAlarmPlayer.setDataSource(url)
            mediaAlarmPlayer.setOnPreparedListener(this::onPrepared2)
            mediaAlarmPlayer.prepare()

        }.addOnFailureListener{}
    }
    fun mpStop(){
        try {
            if(mediaPlayer.isPlaying()) {
                mediaPlayer.reset()
                mediaPlayer.stop()
            }
            if(mediaAlarmPlayer.isPlaying()) {
                mediaAlarmPlayer.reset()
                mediaAlarmPlayer.stop()
            }
        }catch (e : IllegalStateException){
            println("test")
        }
    }
    fun onPrepared(mp: MediaPlayer){
        mediaPlayer = mp
        mediaPlayer.start()
    }
    fun onPrepared2(mp: MediaPlayer){
        mediaAlarmPlayer = mp
        mediaAlarmPlayer.start()
    }
    override fun onCompletion(mp: MediaPlayer) {
        val time =mp.duration
        playTime += time.toInt()
        calendar.timeInMillis = playTime.toLong()
        if(calendar.timeInMillis >= calendarTime.timeInMillis){
            mpStop()
        }else {
            println("ループだよ")
            println(calendar.timeInMillis)
            if (alarmFlag) {
                alarmVolume+=0.1f
                mpStart(this)
            }else{
                mpStart()
            }
        }

    }
}