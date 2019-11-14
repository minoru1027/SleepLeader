package f1.sleepLeader

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.realm.OrderedRealmCollection
import io.realm.RealmBaseAdapter

class musicAdapter(data : OrderedRealmCollection<MusicTable>?) : RealmBaseAdapter<MusicTable>(data) {

    inner class ViewHolder(cell:View){
        var id:Long = 0
        var musicName = cell.findViewById<TextView>(android.R.id.text1)
        var musicPath = ""
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
            viewHolder.id = musicTable.musicId
            viewHolder.musicName.text = musicTable.musicName
            viewHolder.musicPath = musicTable.musicPath
        }

        return view
    }
}