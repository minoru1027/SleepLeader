package f1.sleepLeader

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class AlarmMemoryTable : RealmObject(){
    @PrimaryKey
    var AlarmMemoryId : Long = 0
    var timer : String = ""
    var snoozeFlag : String = ""
    var musicFlag : String = ""
    var musicPath : String = ""
    var firebaseFlag : String = ""
}