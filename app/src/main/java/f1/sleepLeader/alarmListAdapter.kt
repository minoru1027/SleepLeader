package f1.sleepLeader
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.realm.OrderedRealmCollection
import io.realm.RealmBaseAdapter
import java.text.FieldPosition


class alarmListAdapter(data: OrderedRealmCollection<AlarmTable>?) : RealmBaseAdapter<AlarmTable>(data) {

    inner class ViewHolder(cell:View){
        val timer = cell.findViewById<TextView>(android.R.id.text1)
        var id:Long =0
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

        adapterData?.run{
            val alarmTimer = get(position)
            viewHolder.timer.text =  alarmTimer.timer
            viewHolder.id = alarmTimer.alarmId
        }

        return view
    }
}