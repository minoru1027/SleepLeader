package f1.sleepLeader
import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import io.realm.OrderedRealmCollection
import io.realm.RealmBaseAdapter
import java.lang.IllegalArgumentException
import java.text.FieldPosition
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timerTask


class alarmListAdapter(context : Context,alarmListTimer : List<timerData>) : ArrayAdapter<timerData>(context,0,alarmListTimer){

    inner class ViewHolder(cell:View){
        val timer = cell.findViewById<TextView>(android.R.id.text1)
        var id:Long =0
        var musicPath : String = ""
        var musicFlag : String = ""
        var snoozeFlag  : String = ""
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view : View
        val viewHolder : ViewHolder

        when(convertView){
            null ->{
                val inflater = LayoutInflater.from(parent?.context)
                view = inflater.inflate(android.R.layout.simple_list_item_2,parent,false)
                viewHolder = ViewHolder(view)
                view.tag = viewHolder
            }else -> {
                view = convertView
                viewHolder = view.tag as ViewHolder
            }
        }

        val timerList = getItem(position) as timerData
        viewHolder.id = timerList.alarmId.toLong()
        viewHolder.timer.text = timerList.alarmTime

        return view
    }
}