package f1.sleepLeader

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class AlarmActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

            AlarmSet.setOnClickListener {
                val intent = Intent(applicationContext, AlarmSetActivity::class.java)
                startActivity(intent)
            }
    }
}
