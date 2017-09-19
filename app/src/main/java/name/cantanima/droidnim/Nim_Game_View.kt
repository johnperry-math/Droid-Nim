package name.cantanima.droidnim

import android.content.Context
import android.content.DialogInterface
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.graphics.Color.GREEN
import android.graphics.Color.RED
import android.graphics.PorterDuff.Mode.SRC_ATOP
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.widget.TextView
import android.view.View.OnTouchListener
import java.util.*


/**
 * Shows a Nim game using Droids.
 */
class Nim_Game_View : View, OnTouchListener, DialogInterface.OnClickListener {
    private var game = Nim_Game(intArrayOf(7, 5, 3))
    private var orig_num_droids = intArrayOf(7, 5, 3)

    private var pebble_paint = Paint()
    private var highlight_paint = Paint()
    private var max_pebbles = 7
    private var view_width = 0
    private var view_height = 0

    private var value_view : TextView? = null

    private var target_row = 0
    private var target_pebble = 0
    private var highlight = false

    private val random = Random()
    private var opponent : Opponent? = null

    private var game_over_dialog : AlertDialog? = null

    private val tag = "Nim Game View"

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {

        // Load attributes
        val a = context.obtainStyledAttributes(attrs, R.styleable.Nim_Game_View, defStyle, 0)

        a.recycle()

        pebble_paint.color = GREEN
        highlight_paint.color = RED
        setOnTouchListener(this)
        opponent = Computer_Opponent()
        if (!isInEditMode) new_game_dialog()

    }

    fun set_views(value_text_view : TextView) {
        value_view = value_text_view
    }

    private fun new_game_dialog() =
        New_Game_Dialog(context, this, game.rows.size, max_pebbles).show()

    private fun declare_winner(human_won: Boolean) {
        val message : String
        if (human_won) {
            val all_insults = context.resources.getStringArray(R.array.win_insults)
            message = context.getString(R.string.you_won) + " " +
                    all_insults[random.nextInt(all_insults.size)]
        } else {
            val all_insults = context.resources.getStringArray(R.array.lose_insults)
            message = context.getString(R.string.i_won) + " " +
                    all_insults[random.nextInt(all_insults.size)]
        }
        game_over_dialog = AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.game_over))
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.again_cur), this)
                .show()
    }

    fun start_game(rows : Int, max_pebbles_per_row : Int) {
        max_pebbles = max_pebbles_per_row
        val game_data = IntArray(rows)
        orig_num_droids = IntArray(rows)
        var max_made = 0
        for (i in 0.until(rows)) {
            orig_num_droids[i] = random.nextInt(max_pebbles_per_row) + 1
            game_data[i] = orig_num_droids[i]
            max_made = maxOf(max_made, game_data[i])
        }
        if (max_made < max_pebbles_per_row) {
            val which_row = random.nextInt(rows)
            orig_num_droids[which_row] = max_pebbles_per_row
            game_data[which_row] = max_pebbles_per_row
        }
        game = Nim_Game(game_data)
        opponent!!.prepare_for_new_game(game)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        val droid = ResourcesCompat.getDrawable(resources, R.drawable.ic_android_first_try, null)

        var pebble_width = contentWidth / (max_pebbles.toFloat() + 1.5f)
        var row_height = contentHeight / (game.rows.size.toFloat())
        if (pebble_width > row_height) pebble_width = row_height
        else row_height = pebble_width
        val start_y = view_height / 2 - row_height * game.rows.size / 2
        for ((i, row) in game.rows.withIndex()) {
            if (highlight && (i == target_row))
                droid!!.setColorFilter(RED, SRC_ATOP)
            else
                droid!!.setColorFilter(GREEN, SRC_ATOP)
            droid.setBounds(
                    paddingLeft, (start_y + row_height * i).toInt(),
                    paddingLeft + pebble_width.toInt(), (start_y + row_height * (i + 1)).toInt()
            )
            droid.draw(canvas)
            for (j in 1..(orig_num_droids[i] - row.pebbles)) {
                val start_x = (pebble_width * (j + 0.5f) + paddingLeft).toInt()
                droid.setColorFilter(RED, SRC_ATOP)
                droid.setBounds(
                        start_x,
                        (start_y + row_height * i).toInt(),
                        (start_x + pebble_width).toInt(),
                        (start_y + row_height * (i + 1)).toInt()
                )
                droid.draw(canvas)
            }
            for (j in 1..row.pebbles) {
                //var which_paint = pebble_paint
                var which_color = GREEN
                var offset = 0
                if (highlight && (i == target_row) && (j <= target_pebble)) {
                    //which_paint = highlight_paint
                    which_color = RED
                    offset = random.nextInt(4) - 2
                }
                val start_x = (paddingLeft + pebble_width * (j + 0.5) + pebble_width * (orig_num_droids[i] - row.pebbles)).toInt()
                droid.setBounds(
                        start_x + offset,
                        (start_y + row_height * i).toInt(),
                        (start_x + pebble_width).toInt(),
                        (start_y + row_height * (i + 1)).toInt()
                )
                droid.setColorFilter(which_color, SRC_ATOP)
                droid.draw(canvas)
            }

        }

        if (!isInEditMode) value_view!!.text = game.value().toString()

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        view_width = measuredWidth
        view_height = measuredHeight - paddingTop - paddingBottom
        view_width = minOf(view_width, view_height)
        view_height = view_width
        setMeasuredDimension(
                view_width,
                view_height + paddingTop + paddingBottom
        )
    }

    override fun getMinimumWidth(): Int = view_width

    override fun getMinimumHeight(): Int = view_height

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        val result = p0 == this && p1 != null
        if (result && p1 != null) {
            val x = p1.x
            val y = p1.y
            val contentWidth = width - paddingLeft - paddingRight
            val contentHeight = height - paddingTop - paddingBottom
            var pebble_width = contentWidth / (max_pebbles.toFloat() + 1.5f)
            var row_height = contentHeight / (game.rows.size.toFloat())
            if (pebble_width > row_height) pebble_width = row_height
            else row_height = pebble_width
            val start_x = 1.5f * pebble_width + paddingLeft
            val start_y = view_height / 2 - row_height * game.rows.size / 2
            val end_x = start_x + pebble_width * max_pebbles // view_width - paddingRight
            val end_y = start_y + row_height * game.rows.size
            if (x in start_x..end_x && y in start_y..end_y) {
                target_row = (y - start_y).toInt() / (row_height).toInt()
                target_row = minOf(target_row, game.rows.size - 1)
                target_pebble = (x - start_x).toInt() / pebble_width.toInt() + 1 - (orig_num_droids[target_row] - game.rows[target_row].pebbles)
                if (target_pebble < 0) target_pebble = 0
                val action = p1.actionMasked
                if (action == ACTION_DOWN || action == ACTION_MOVE) {
                    highlight = true
                } else if (action == ACTION_UP && highlight) {
                    var finished = game.play(Move(target_row, target_pebble))
                    highlight = false
                    var human_last = true
                    val my_opponent : Opponent? = opponent
                    if (!finished && my_opponent != null) {
                        human_last = false
                        finished = my_opponent.make_a_move()
                    }
                    if (finished)
                        declare_winner(human_last)
                }
                invalidate()
            } else {
                highlight = false
                invalidate()
            }
        }

        return result
    }

    override fun onClick(p0: DialogInterface?, p1: Int) {
        new_game_dialog()
    }

}
