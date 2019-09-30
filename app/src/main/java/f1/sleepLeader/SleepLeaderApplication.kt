package f1.sleepLeader

import android.app.Application
import io.realm.Realm

class SleepLeaderApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}