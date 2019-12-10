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
import java.util.*
import kotlin.IllegalStateException

open class MediaPlayerActivity: AppCompatActivity(),MediaPlayer.OnCompletionListener{

    private var volume : Float = 0.7f
    private var alarmVolume : Float = 0.4f
    private var calendar : Calendar = Calendar.getInstance()
    private var limitTime : Long = 1800000
    private var calendarTime : Calendar = Calendar.getInstance()
    private var firebaseFlag = "OFF"
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference
    companion object {
        @JvmField
        var playTime : Long = 0
        var bgplayTime : Long = 0
        var setedAlarmPath : String = ""
        var mediaPlayer : MediaPlayer = MediaPlayer()
        var mediaAlarmPlayer : MediaPlayer = MediaPlayer()
        var musicFlag = false
        var alarmFlag = false
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

    }
    fun mpStart(){
        try {
            if(musicFlag == false) {
                mediaPlayer.setVolume(volume, volume)
                mediaPlayer.setOnCompletionListener(this)
                println("test")
                mediaPlayer.start()
            }
        }catch (e : IOException){
            Toast.makeText(this, "音楽処理時にエラー発生", Toast.LENGTH_LONG).show()
        }
    }
    fun mpStart(context: Context){
        try {
            mediaAlarmPlayer.setVolume(alarmVolume,alarmVolume)
            mediaAlarmPlayer.setOnCompletionListener(this)
            mediaAlarmPlayer.start()

        }catch (e : IOException){
            Toast.makeText(context, "音楽処理時にエラー発生", Toast.LENGTH_LONG).show()
        }
    }
    fun mpStart(musicPath : String){
            val path = "Music/" + musicPath
            val dir = storageRef.child(path.trim())
            try {
                dir.downloadUrl.addOnSuccessListener { uri ->
                    Log.i("Test", "3")
                    val url = uri.toString()
                    println(uri)
                    mediaPlayer.setDataSource(url)
                    mediaPlayer.setOnCompletionListener(this)
                    mediaPlayer.setOnPreparedListener(this::onPrepared)
                    mediaPlayer.prepare()
                    alarm()

                }.addOnFailureListener {}
            }catch (e:IllegalStateException){

            }catch (e:IllegalStateException){

            }
    }
    fun alarm(){
        if(musicFlag){
            mediaPlayer.reset()
            mediaPlayer.stop()
        }
        if(mediaAlarmPlayer.isPlaying()){

        }else{
            mediaAlarmPlayer.start()
        }
    }
    fun dlStop(){
        try {
            mediaAlarmPlayer.reset()
            mediaAlarmPlayer.stop()
        }catch (e : IllegalStateException){
            println("test")
        }
    }
    fun mpStart(context: Context,musicPath: String){
        val path = "Music/"+musicPath
        val dir = storageRef.child(path.trim())
        try {
            dir.downloadUrl.addOnSuccessListener { uri ->
                Log.i("Test", "3")
                val url = uri.toString()
                println(url)
                mediaAlarmPlayer.setDataSource(url)
                mediaAlarmPlayer.setOnCompletionListener(this)
                mediaAlarmPlayer.setOnPreparedListener(this::onPrepared2)
                mediaAlarmPlayer.prepare()
            }.addOnFailureListener {}
        }catch (e:IllegalStateException){

        }
    }
    fun mpStop(){
        try {
            if(mediaPlayer.isPlaying()) {
                mediaPlayer.reset()
                mediaPlayer.stop()
            }
            if(mediaAlarmPlayer.isPlaying()){
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
        println(playTime)
        if(playTime >= limitTime){
            println("bbb")
            playTime =0
            bgplayTime = 0
            mpStop()
        }else {
            println("ループだよ")
            if(alarmFlag){
                playTime += bgplayTime
                println("ddd")
                alarmVolume += 0.1f
                mpStart(this)
            }else if(alarmFlag == false || musicFlag == false) {
                playTime += bgplayTime
                println("ccc")
                mpStart()
            }
        }

    }
}