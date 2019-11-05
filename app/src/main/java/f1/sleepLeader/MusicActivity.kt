package f1.sleepLeader

//import android.support.v7.app.AppCompatActivity
import android.content.res.Resources
import android.media.AsyncPlayer
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_alarm_list.*

class MusicActivity : AppCompatActivity() {

    private lateinit var player: MediaPlayer
    private lateinit var realm : Realm
    private var sId : Int = 0
    private var musicFlag = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)

        realm = Realm.getDefaultInstance()

        val musicList = realm.where<MusicTable>().findAll()
        listView.adapter = musicAdapter(musicList)


    }

    override fun onResume() {
        super.onResume()
        listView.setOnItemClickListener{ parent, view, position, id ->
            val musicListPosition = parent.getItemAtPosition(position) as MusicTable
            val res = this.resources
            var soundId = res.getIdentifier(musicListPosition.musicPath,"raw",this.packageName)
            if(musicFlag == true){
                player.stop()
                player.reset()
                player.release()
                musicFlag = false
            }
            if(soundId === sId){
                sId = 0
            }else {
                player = MediaPlayer.create(this, soundId)
                player.start()
                musicFlag = true
                sId = soundId
            }
        }
    }

    override fun onStop() {
        super.onStop()
        player.stop()
        player.reset()
        player.release()
        musicFlag = false
    }
    override fun onDestroy() {
        super.onDestroy()
        player.stop()
        player.reset()
        player.release()
        musicFlag = false
    }
}
