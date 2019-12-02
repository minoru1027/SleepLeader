package f1.sleepLeader

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class MusicListAdapter (context : Context,MusicNameList: List<FireBaseData>) : ArrayAdapter<FireBaseData>(context,0,MusicNameList){

    inner class ViewHolder(cell: View) {
        val musicName = cell.findViewById<TextView>(android.R.id.text1)
        var musicPath: String = ""
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        when (convertView) {
            null -> {
                val inflater = LayoutInflater.from(parent?.context)
                view = inflater.inflate(android.R.layout.simple_list_item_2, parent, false)
                viewHolder = ViewHolder(view)
                view.tag = viewHolder
            }
            else -> {
                view = convertView
                viewHolder = view.tag as ViewHolder
            }
        }

        val musicList = getItem(position) as FireBaseData
        viewHolder.musicName.text = musicList.musicName
        viewHolder.musicPath = musicList.musicPath
        //Listの文字色
        viewHolder.musicName.setTextColor(Color.rgb(211,211,211))
        viewHolder.musicName.gravity= Gravity.CENTER
        viewHolder.musicName.textSize = 20F
        return view
    }
}