package f1.sleepLeader

import android.content.Context
import android.content.res.Resources
import android.media.MediaPlayer
import io.realm.Realm
import io.realm.kotlin.where
import java.util.*

class MusicPlayActivity :MediaPlayerActivity(){

    private var alarmRealm : Realm = Realm.getDefaultInstance()
    private var firebaseFlag = ""

    public fun MusicStop(){
        musicFlag = true
        alarmFlag = true
        alarm()
    }
    public fun AlarmMusicSet(res: Resources, context: Context){

        val musicAlarmPath = MusicAlarmRandom()
        if(firebaseFlag.equals("ON")){
            mpStart(context,musicAlarmPath)
        }else {
            var soundId = res.getIdentifier(musicAlarmPath, "raw", context.packageName)
            mediaAlarmPlayer = MediaPlayer.create(context, soundId)
            mpStart(context)
        }
    }

    private fun MusicAlarmRandom() : String{
        var maxId = alarmRealm.where<MusicAlarmTable>().max("musicAlarmId")
        var randomId = (maxId?.toInt() ?: 0)
        println(randomId)
        val random = Random()
        var pathId = random.nextInt(randomId)
        while(pathId < 1){
            pathId = (maxId?.toInt() ?: 0)
        }
        val musicAlarmId = alarmRealm.where<MusicAlarmTable>().equalTo("musicAlarmId",pathId).findFirst()
        val musicAlarmPath = musicAlarmId?.musicAlarmPath.toString()
        firebaseFlag = musicAlarmId?.firebaseAlarmFlag.toString()

        return musicAlarmPath
    }

    override fun onStop() {
        super.onStop()
        try {
        }catch (e:IllegalStateException){
            println(e)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        try {
            alarmRealm.close()
        }catch (e:IllegalStateException){
            println(e)
        }
    }
}