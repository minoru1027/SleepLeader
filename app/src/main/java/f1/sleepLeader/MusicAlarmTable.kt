package f1.sleepLeader

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class MusicAlarmTable :RealmObject(){
    @PrimaryKey
    var musicAlarmId : Long = 0
    var musicAlarmName : String = ""
    var musicAlarmPath : String = ""
    var playAlarmTime : Long = 0
    var firebaseAlarmFlag : String = ""
}