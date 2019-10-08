package f1.sleepLeader

import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlin.concurrent.timer

class AlarmStopActivity : AppCompatActivity() {
    private lateinit var soundPool: SoundPool
    private var soundResId = 0
    private var timerList : HashMap<Long,String> = hashMapOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_stop)

        soundPool = SoundPool(2,AudioManager.STREAM_ALARM,0)
        soundResId = soundPool.load(this,R.raw.machinegun2,1)
        soundPool.play(soundResId,1.0f,100f,0,0,1.0f)
        timerList = intent?.getSerializableExtra("timerList") as HashMap<Long, String>


    }
}