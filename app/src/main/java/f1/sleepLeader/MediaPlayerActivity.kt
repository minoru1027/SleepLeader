package f1.sleepLeader

import android.media.MediaPlayer
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity

open class MediaPlayerActivity: AppCompatActivity(){

    companion object {
        @JvmField
        var mediaPlayer : MediaPlayer = MediaPlayer()
    }
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }
    fun mpStart(){
        mediaPlayer.start()
    }

    fun mpStop(){
        mediaPlayer.stop()
    }
}