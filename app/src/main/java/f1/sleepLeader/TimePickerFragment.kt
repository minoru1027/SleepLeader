package f1.sleepLeader


import android.app.AlertDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.activity_alarm_set.*

import java.util.*

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {
    private lateinit var calendar:Calendar
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // Initialize a Calendar instance
        calendar = Calendar.getInstance()

        // Get the system current hour and minute
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return TimePickerDialog(
            activity, // Context
            android.R.style.
               Theme_Holo_Dialog_MinWidth,


            this, // TimePickerDialog.OnTimeSetListener
            hour, // Hour of day
            minute, // Minute
            false // Is 24 hour view

        )
    }


    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        // Do something with the returned time
        var tv : TextView = activity!!.findViewById<TextView>(R.id.timePicker)
        var setting : Button = activity!!.findViewById(R.id.AlarmSetting)
        var hour = hourOfDay
        val zone = getAMPM(hourOfDay)
        println(zone)
        if(zone.equals("PM")){
            hour += 12
        }
        setting.visibility = View.VISIBLE
        tv.text = "%1$02d:%2$02d".format(getHourAMPM(hour),minute)

    }
    override fun onCancel(dialog: DialogInterface?) {
        Toast.makeText(activity,"Picker Canceled.",Toast.LENGTH_SHORT).show()
        var setting : Button = activity!!.findViewById(R.id.AlarmSetting)
        setting.visibility = View.VISIBLE
        super.onCancel(dialog)
    }


    // Custom method to get AM PM value from provided hour
    private fun getAMPM(hour:Int):String{
        return if(hour>11)"PM" else "AM"
    }


    // Custom method to get hour for AM PM time format
    private fun getHourAMPM(hour:Int):Int{
        // Return the hour value for AM PM time format
        var modifiedHour = if (hour>11)hour-12 else hour
        return modifiedHour
    }
}