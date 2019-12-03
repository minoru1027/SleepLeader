package f1.sleepLeader

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class MusicTable :RealmObject(){
    @PrimaryKey
    var musicId : Long = 0
    var musicName : String = ""
    var musicPath : String = ""
    var playTime : Long = 0
    var firebaseFlag : String = ""
}