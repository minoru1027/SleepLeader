package f1.sleepLeader

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AlphaAnimation
import android.widget.TextView

class FadeIn : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fade_in)

        val intent = Intent(this,AlarmActivity::class.java)
        val title: TextView = findViewById(R.id.title)
        val msg: TextView = findViewById(R.id.msg)
        val name: TextView = findViewById(R.id.name)

        //フォント設定
        val titlefont = Typeface.createFromAsset(assets,"Barkentina.otf")
        val msgfont = Typeface.createFromAsset(assets,"kokugl.ttf")
        val namefont = Typeface.createFromAsset(assets,"kokugl.ttf")
        title.setTypeface(titlefont)
        msg.setTypeface(msgfont)
        name.setTypeface(namefont)

        //タイトル
        var animation = AlphaAnimation(1f,0f)
            animation.duration = 5000
            animation.fillAfter = true
        title.startAnimation(animation)

        //メッセージ
        var animsg = AlphaAnimation(0f,1f)
            animsg.duration = 5000
            animsg.fillAfter = true
        msg.startAnimation(animsg)
        name.startAnimation(animsg)

        Handler().postDelayed({
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
        },7000)
    }
}
