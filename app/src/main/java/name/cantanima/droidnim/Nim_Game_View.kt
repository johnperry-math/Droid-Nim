package name.cantanima.droidnim

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.graphics.Color.GREEN
import android.graphics.Color.RED
import android.graphics.PorterDuff.Mode.SRC_ATOP
import android.support.v4.content.res.ResourcesCompat
import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.widget.TextView
import android.view.View.OnTouchListener
import java.util.*


/**
 * Shows a Nim game using Droids.
 */
class Nim_Game_View : View, OnTouchListener {

    private var game = Nim_Game(intArrayOf(7, 5, 3))
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

    fun start_game(rows : Int, max_pebbles_per_row : Int) {
        val game_data = IntArray(rows)
        var max_made = 0
        for (i in 0.until(rows)) {
            game_data[i] = random.nextInt(max_pebbles_per_row) + 1
            max_made = maxOf(max_made, game_data[i])
        }
        if (max_made < max_pebbles_per_row)
            game_data[random.nextInt(rows)] = max_pebbles_per_row
        game = Nim_Game(game_data)
        max_pebbles = game.max_size()
        opponent!!.prepare_for_new_game(game)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        val droid = ResourcesCompat.getDrawable(resources, R.drawable.ic_android_black_24dp, null)

        val pebble_width = contentWidth / (max_pebbles.toFloat() + 2)
        val row_height = contentHeight / (2 * game.rows.size.toFloat() - 1)
        val diameter = minOf(pebble_width, row_height)
        val start_y = view_height / 2 - game.rows.size * diameter + diameter
        for ((i, row) in game.rows.withIndex()) {
            if (highlight && (i == target_row))
                droid!!.setColorFilter(RED, SRC_ATOP)
            else
                droid!!.setColorFilter(GREEN, SRC_ATOP)
            droid.setBounds(
                    paddingLeft, (start_y + diameter * i * 2 - diameter / 2).toInt(),
                    (paddingLeft + diameter).toInt(), (start_y + diameter * i * 2 + diameter / 2).toInt()
            )
            droid.draw(canvas)
            for (j in 1.rangeTo(row.pebbles)) {
                //var which_paint = pebble_paint
                var which_color = GREEN
                var offset = 0
                if (highlight && (i == target_row) && (j <= target_pebble)) {
                    //which_paint = highlight_paint
                    which_color = RED
                    offset = random.nextInt(4) - 2
                }
                val start_x = (diameter * j + diameter + paddingLeft).toInt()
                droid.setBounds(
                        start_x + offset,
                        (start_y + diameter * i * 2 - diameter / 2).toInt(),
                        (start_x + diameter).toInt(),
                        (start_y + diameter * i * 2 + diameter / 2).toInt()
                )
                droid.setColorFilter(which_color, SRC_ATOP)
                droid.draw(canvas)
            }

        }

        value_view!!.text = game.value().toString()

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        view_width = measuredWidth - paddingLeft - paddingRight
        view_height = measuredHeight - paddingTop - paddingBottom
        view_width = minOf(view_width, view_height)
        view_height = view_width
        setMeasuredDimension(
                view_width + paddingLeft + paddingRight,
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
            val pebble_width = contentWidth / (max_pebbles.toFloat() + 2)
            val row_height = contentHeight / (2 * game.rows.size.toFloat() - 1)
            val diameter = minOf(pebble_width, row_height)
            val start_x = paddingLeft + 2 * diameter
            val start_y = view_height / 2 - game.rows.size * diameter
            val end_x = view_width - paddingRight
            val end_y = start_y + diameter * 2 * game.rows.size - 1
            if (x >= start_x && x <= end_x && y >= start_y && y <= end_y) {
                target_row = (y - start_y).toInt() / (2 * diameter).toInt()
                target_pebble = (x - start_x).toInt() / diameter.toInt() + 1
                val action = p1.actionMasked
                if (action == ACTION_DOWN || action == ACTION_MOVE) {
                    highlight = true
                } else if (action == ACTION_UP && highlight) {
                    var finished = game.play(Move(target_row, target_pebble))
                    highlight = false
                    val my_opponent : Opponent? = opponent
                    if (!finished && my_opponent != null)
                        finished = my_opponent.make_a_move()
                    if (finished)
                        new_game_dialog()
                }
                invalidate()
            } else {
                highlight = false
                invalidate()
            }
        }

        return result
    }

}
