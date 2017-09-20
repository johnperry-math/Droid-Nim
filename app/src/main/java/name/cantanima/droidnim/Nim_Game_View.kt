package name.cantanima.droidnim

import android.content.Context
import android.content.DialogInterface
import android.graphics.Canvas
import android.graphics.Color.*
import android.graphics.Paint
import android.graphics.PorterDuff.Mode.SRC
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.graphics.PorterDuff.Mode.SRC_ATOP
import android.graphics.Rect
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
    private var max_pebbles = 7

    private val droid_standing = ResourcesCompat.getDrawable(
            resources, R.drawable.ic_android_standing, null
    )
    private val eyes_standing = ResourcesCompat.getDrawable(
            resources, R.drawable.ic_android_eyes_standing, null
    )
    private val droid_fallen = ResourcesCompat.getDrawable(
            resources, R.drawable.ic_android_fallen, null
    )
    private val eyes_fallen = ResourcesCompat.getDrawable(
            resources, R.drawable.ic_android_eyes_fallen, null
    )
    private var droids_to_draw : Array<Array<Drawable>> = Array(
            3, { Array(7 - 2*it, { droid_standing!!.constantState.newDrawable() }) }
    )
    private var eyes_to_draw :  Array<Array<Drawable>> = Array(
            3, { Array(7 - 2*it, { eyes_standing!!.constantState.newDrawable() }) }
    )

    private var pebble_paint = Paint()
    private var highlight_paint = Paint()

    private var value_view : TextView? = null

    private var target_row = 0
    private var target_pebble = 0
    private var highlight = false

    private val random = Random()
    private var opponent : Opponent? = null

    private var game_over_dialog : AlertDialog? = null

    @Suppress("unused") private val tag = "Nim Game View"

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
        droids_to_draw = Array(
            rows, { Array(game.rows[it].pebbles, { droid_standing!!.constantState.newDrawable() }) }
        )
        eyes_to_draw = Array(
                rows, { Array(game.rows[it].pebbles, { eyes_standing!!.constantState.newDrawable() }) }
        )
        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom
        var pebble_width = contentWidth / (max_pebbles.toFloat() + 1.5f)
        var row_height = contentHeight / (game.rows.size.toFloat())
        if (pebble_width > row_height) pebble_width = row_height
        else row_height = pebble_width
        val start_y = contentHeight / 2 - row_height * game.rows.size / 2
        for ((i, row) in game.rows.withIndex()) {
            for (j in 0.until(row.pebbles)) {
                val start_x = (pebble_width * (j + 1.5f) + paddingLeft).toInt()
                droids_to_draw[i][j].setColorFilter(GREEN, SRC_ATOP)
                droids_to_draw[i][j].setBounds(
                        start_x, (start_y + row_height * i).toInt(),
                        (start_x + pebble_width).toInt(), (start_y + row_height * (i + 1)).toInt()
                )
                eyes_to_draw[i][j].setColorFilter(WHITE, SRC_ATOP)
                eyes_to_draw[i][j].setBounds(
                        start_x, (start_y + row_height * i).toInt(),
                        (start_x + pebble_width).toInt(), (start_y + row_height * (i + 1)).toInt()
                )
            }
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        pebble_paint.style = Paint.Style.STROKE
        canvas.drawLine(
                paddingLeft.toFloat(), paddingTop.toFloat(), (paddingLeft + contentWidth).toFloat(),
                paddingTop.toFloat(), pebble_paint
        )
        canvas.drawLine(
                paddingLeft.toFloat(), paddingTop.toFloat(), paddingLeft.toFloat(),
                (paddingTop + contentHeight).toFloat(), pebble_paint
        )
        canvas.drawLine(
                (paddingLeft + contentWidth).toFloat(), paddingTop.toFloat(),
                (paddingLeft + contentWidth).toFloat(), (paddingTop + contentHeight).toFloat(),
                pebble_paint
        )
        canvas.drawLine(
                paddingLeft.toFloat(), (paddingTop + contentHeight).toFloat(),
                (paddingLeft + contentWidth).toFloat(), (paddingTop + contentHeight).toFloat(),
                pebble_paint
        )

        val droid = ResourcesCompat.getDrawable(resources, R.drawable.ic_android_standing, null)
        val droid_eyes = ResourcesCompat.getDrawable(resources, R.drawable.ic_android_eyes_standing, null)
        droid_eyes!!.setColorFilter(RED, SRC_ATOP)

        var pebble_width = contentWidth / (max_pebbles.toFloat() + 1.5f)
        var row_height = contentHeight / (game.rows.size.toFloat())
        if (pebble_width > row_height) pebble_width = row_height
        else row_height = pebble_width
        val start_y = contentHeight / 2 - row_height * game.rows.size / 2
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
            droid_eyes.setBounds(
                    paddingLeft, (start_y + row_height * i).toInt(),
                    paddingLeft + pebble_width.toInt(), (start_y + row_height * (i + 1)).toInt()
            )
            droid_eyes.draw(canvas)
            val pebble_targeted = target_pebble + orig_num_droids[i] - row.pebbles
            for (j in 0.until(orig_num_droids[i])) {
                val first_living_droid = orig_num_droids[i] - row.pebbles
                if (
                    highlight && i == target_row &&
                            (j in first_living_droid.until(pebble_targeted))
                ) {
                    droids_to_draw[i][j].setColorFilter(RED, SRC_ATOP)
                    eyes_to_draw[i][j].setColorFilter(WHITE, SRC_ATOP)
                } else {
                    droids_to_draw[i][j].setColorFilter(GREEN, SRC_ATOP)
                    eyes_to_draw[i][j].setColorFilter(BLACK, SRC_ATOP)
                }
            }
            for (draw_droid in droids_to_draw[i]) {
                draw_droid.draw(canvas)
            }
            for (draw_eyes in eyes_to_draw[i])
                draw_eyes.draw(canvas)
        }

        if (!isInEditMode) value_view!!.text = game.value().toString()

    }

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
            val start_y = contentHeight / 2 - row_height * game.rows.size / 2
            val end_x = start_x + pebble_width * max_pebbles
            val end_y = start_y + row_height * game.rows.size
            if (x in start_x..end_x && y in start_y..end_y) {
                target_row = (y - start_y).toInt() / (row_height).toInt()
                target_row = minOf(target_row, game.rows.size - 1)
                val pebble_targeted = (x - start_x).toInt() / pebble_width.toInt() + 1
                target_pebble =
                        pebble_targeted -
                        (orig_num_droids[target_row] - game.rows[target_row].pebbles)
                if (target_pebble < 0) target_pebble = 0
                val action = p1.actionMasked
                if (action == ACTION_DOWN || action == ACTION_MOVE) {
                    highlight = true
                } else if (action == ACTION_UP && highlight) {
                    val curr_pebbles = orig_num_droids[target_row] - game.rows[target_row].pebbles
                    var finished = game.play(Move(target_row, target_pebble))
                    for (i in (curr_pebbles).until(pebble_targeted)) {
                        droid_fallen!!.bounds = Rect(droids_to_draw[target_row][i].bounds)
                        droids_to_draw[target_row][i] = droid_fallen.constantState.newDrawable()
                        droids_to_draw[target_row][i].bounds = droid_fallen.bounds
                        eyes_fallen!!.bounds = Rect(eyes_to_draw[target_row][i].bounds)
                        eyes_to_draw[target_row][i] = eyes_fallen.constantState.newDrawable()
                        eyes_to_draw[target_row][i].bounds = eyes_fallen.bounds
                    }
                    highlight = false
                    var human_last = true
                    val my_opponent : Opponent? = opponent
                    if (!finished && my_opponent != null) {
                        human_last = false
                        finished = my_opponent.make_a_move()
                        val last_move = my_opponent.last_move
                        for (i in (orig_num_droids[last_move.row] - game.rows[last_move.row].pebbles - last_move.number).until(orig_num_droids[last_move.row] - game.rows[last_move.row].pebbles)) {
                            droid_fallen!!.bounds = Rect(droids_to_draw[last_move.row][i].bounds)
                            droids_to_draw[last_move.row][i] = droid_fallen.constantState.newDrawable()
                            droids_to_draw[last_move.row][i].bounds = droid_fallen.bounds
                            eyes_fallen!!.bounds = Rect(eyes_to_draw[last_move.row][i].bounds)
                            eyes_to_draw[last_move.row][i] = eyes_fallen.constantState.newDrawable()
                            eyes_to_draw[last_move.row][i].bounds = eyes_fallen.bounds
                        }
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
