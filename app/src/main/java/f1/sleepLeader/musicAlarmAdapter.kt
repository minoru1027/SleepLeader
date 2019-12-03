package f1.sleepLeader

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.realm.OrderedRealmCollection
import io.realm.RealmBaseAdapter

class musicAlarmAdapter(data : OrderedRealmCollection<MusicAlarmTable>?) : RealmBaseAdapter<MusicAlarmTable>(data){
    inner class ViewHolder(cell: View){
        var id:Long = 0
        var musicName = cell.findViewById<TextView>(android.R.id.text1)
        var musicPath = ""
        var playAlarmTime : Long = 0
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
            }else ->{
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        }

        adapterData?.run{
            val musicTable = get(position)
            viewHolder.id = musicTable.musicAlarmId
            viewHolder.musicName.text = musicTable.musicAlarmName
            viewHolder.musicPath = musicTable.musicAlarmPath
            viewHolder.playAlarmTime = musicTable.playAlarmTime
            viewHolder.musicName.setTextColor(Color.rgb(211,211,211))
            viewHolder.musicName.gravity= Gravity.CENTER
            viewHolder.musicName.textSize = 20F
        }

        return view
    }
}