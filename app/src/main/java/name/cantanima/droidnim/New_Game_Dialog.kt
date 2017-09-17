package name.cantanima.droidnim

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView

/**
 * Created by cantanima on 9/16/17.
 */
class New_Game_Dialog(
        val my_context: Context, val game_view: Nim_Game_View,
        var num_rows : Int, var max_droids: Int
) :
        Dialog(my_context), View.OnClickListener, SeekBar.OnSeekBarChangeListener
{
    var row_seekbar : SeekBar? = null
    var max_droid_seekbar : SeekBar? = null
    var row_text : TextView? = null
    var max_droid_text : TextView? = null
    var go_button : Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_game_dialog_layout)
        setTitle(R.string.new_game_dialog_title)
        row_seekbar = findViewById(R.id.sb_number_of_rows)
        max_droid_seekbar = findViewById(R.id.sb_max_droids)
        row_text = findViewById(R.id.tv_number_of_rows)
        max_droid_text = findViewById(R.id.tv_max_droids)
        go_button = findViewById<Button>(R.id.new_dialog_go_button)
        row_seekbar!!.setProgress(num_rows - 3)
        row_seekbar!!.setOnSeekBarChangeListener(this)
        max_droid_seekbar!!.setProgress(max_droids - 5)
        max_droid_seekbar!!.setOnSeekBarChangeListener(this)
        go_button!!.setOnClickListener(this)
        val row_string = num_rows.toString().toCharArray()
        row_text!!.setText(row_string, 0, row_string.size)
        val md_string = max_droids.toString().toCharArray()
        max_droid_text!!.setText(md_string, 0, md_string.size)
    }

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        if (p0 != null) {
            if (p0 == max_droid_seekbar) {
                val text = (p0.progress + 5).toString()
                max_droid_text!!.setText(text.toCharArray(), 0, text.length)
            } else {
                val text = (p0.progress + 3).toString()
                row_text!!.setText(text.toCharArray(), 0, text.length)
            }
        }
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
    }

    override fun onClick(p0: View?) {
        val rsb = row_seekbar
        val mdsb = max_droid_seekbar
        if (rsb != null && mdsb != null) {
            game_view.start_game(rsb.progress + 3, mdsb.progress + 5)
            dismiss()
        }
    }

}