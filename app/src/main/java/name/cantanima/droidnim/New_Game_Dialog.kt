package name.cantanima.droidnim

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.TextView

/**
 * Dialog for a new game of Droid Nim.
 */
class New_Game_Dialog(
        private val my_context: Context, private val game_view: Nim_Game_View,
        private var num_rows : Int, private var max_droids: Int
) :
        Dialog(my_context), View.OnClickListener, SeekBar.OnSeekBarChangeListener
{
    private var row_seekbar : SeekBar? = null
    private var max_droid_seekbar : SeekBar? = null
    private var row_text : TextView? = null
    private var max_droid_text : TextView? = null
    private var go_button : Button? = null
    private var bouton_checkbox : CheckBox? = null
    private var repeat_checkbox : CheckBox? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_game_dialog_layout)
        setTitle(R.string.new_game_dialog_title)
        row_seekbar = findViewById(R.id.sb_number_of_rows)
        max_droid_seekbar = findViewById(R.id.sb_max_droids)
        row_text = findViewById(R.id.tv_number_of_rows)
        max_droid_text = findViewById(R.id.tv_max_droids)
        go_button = findViewById(R.id.new_dialog_go_button)
        bouton_checkbox = findViewById(R.id.boutons_game_checkbox)
        repeat_checkbox = findViewById(R.id.repeat_game_checkbox)
        row_seekbar!!.progress = num_rows - 3
        row_seekbar!!.setOnSeekBarChangeListener(this)
        max_droid_seekbar!!.progress = max_droids - 5
        max_droid_seekbar!!.setOnSeekBarChangeListener(this)
        go_button!!.setOnClickListener(this)
        val row_string = num_rows.toString().toCharArray()
        row_text!!.setText(row_string, 0, row_string.size)
        val md_string = max_droids.toString().toCharArray()
        max_droid_text!!.setText(md_string, 0, md_string.size)
        bouton_checkbox!!.setOnClickListener(this)
        repeat_checkbox!!.setOnClickListener(this)
        setCancelable(false)
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
        when (p0) {
            go_button -> {
                dismiss()
                when {
                    bouton_checkbox!!.isChecked ->  game_view.start_bouton_game()
                    repeat_checkbox!!.isChecked -> game_view.start_repeat_last_game()
                    else ->
                        game_view.start_game(
                                row_seekbar!!.progress + 3, max_droid_seekbar!!.progress + 5
                        )
                }
            }
            bouton_checkbox -> {
                row_seekbar!!.isEnabled = !row_seekbar!!.isEnabled
                max_droid_seekbar!!.isEnabled = !max_droid_seekbar!!.isEnabled
                repeat_checkbox!!.isEnabled = !repeat_checkbox!!.isEnabled
            }
            repeat_checkbox -> {
                row_seekbar!!.isEnabled = !row_seekbar!!.isEnabled
                max_droid_seekbar!!.isEnabled = !max_droid_seekbar!!.isEnabled
                bouton_checkbox!!.isEnabled = !bouton_checkbox!!.isEnabled
            }
        }
    }

}