package f1.sleepLeader

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

    companion object {
        @JvmField
        var mediaPlayer : MediaPlayer = MediaPlayer()
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

    fun mpStart(context: Context){
        try {
            mediaPlayer.setVolume(alarmVolume,alarmVolume)
            mediaPlayer.setOnCompletionListener(this)
            mediaPlayer.start()
            alarmFlag = true
        }catch (e : IOException){
            Toast.makeText(context, "音楽処理時にエラー発生", Toast.LENGTH_LONG).show()
        }
    }
    fun mpStop(){
        try {
            mediaPlayer.stop()
            mediaPlayer.release()
        }catch (e : IllegalStateException){
            println("test")
        }
    }

    override fun onCompletion(mp: MediaPlayer?) {
        val time =mediaPlayer.duration
        playTime += time
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