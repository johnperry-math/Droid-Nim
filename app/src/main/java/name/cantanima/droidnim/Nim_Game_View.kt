package name.cantanima.droidnim

import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Color.*
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.graphics.PorterDuff.Mode.SRC_ATOP
import android.graphics.drawable.Drawable
import android.preference.PreferenceManager
import android.support.annotation.ColorInt
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.widget.TextView
import android.view.View.OnTouchListener
import android.widget.Toast
import java.util.*

@ColorInt
val ORANGE = Color.argb(0xff, 0xff, 0x80, 0x00)
val DARK_GREEN = Color.argb(0xff, 0x00, 0x88, 0x00)

enum class Player_Kind { COMPUTER, HUMAN }

/**
 * Shows a Nim game using Droids.
 */
class Nim_Game_View
    : View, OnTouchListener, DialogInterface.OnClickListener, BTR_Listener,
        SharedPreferences.OnSharedPreferenceChangeListener
{
    private var sometimes_stupid = false
    private var misere = false

    private var game = Nim_Game(intArrayOf(7, 5, 3), misere)
    var orig_num_droids = intArrayOf(7, 5, 3)
    private var max_pebbles = 7

    private val droid_standing = ResourcesCompat.getDrawable(
            resources, R.drawable.ic_android_standing, null
    )
    private val eyes_standing = ResourcesCompat.getDrawable(
            resources, R.drawable.ic_android_eyes_standing, null
    )
    private val mouth_standing = ResourcesCompat.getDrawable(
            resources, R.drawable.ic_mouth_standing, null
    )
    private var sentinels = Array(3, { droid_standing!!.mutate().constantState.newDrawable() })
    private var droids_to_draw = Array(
            3, { Array(7 - 2*it, { droid_standing!!.constantState.newDrawable() }) }
    )
    private var eyes_to_draw = Array(
            3, { Array(7 - 2*it, { eyes_standing!!.constantState.newDrawable() }) }
    )
    private var mouths_to_draw = Array(
            3, { Array(7 - 2*it, { mouth_standing!!.constantState.newDrawable() }) }
    )

    private val monitor = Monitor_Bored_Droids()
    private var borer : Bore_A_Droid? = null

    private var pebble_paint = Paint()
    private var highlight_paint = Paint()
    var color_happy_droid = GREEN
    var color_happy_eyes = ORANGE
    var color_happy_mouth = ORANGE
    var color_worry_droid = RED
    var color_worry_eyes = WHITE
    var color_worry_mouth = YELLOW
    var color_deact_droid = DARK_GREEN
    var color_deact_eyes = BLACK
    var color_deact_mouth = BLACK

    private var value_view : TextView? = null

    private var target_row = 0
    private var target_pebble = 0
    private var highlight = false

    private val random = Random()
    private var opponent : Opponent? = null
    private var kind_of_opponent = Player_Kind.COMPUTER

    private var game_over_dialog : AlertDialog? = null

    private val bt_ideal_raw = arrayOfNulls<Byte>(25)

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
        for (sentinel in sentinels)
            sentinel.setColorFilter(color_happy_droid, SRC_ATOP)
        
        setOnTouchListener(this)
        opponent = Computer_Opponent(sometimes_stupid)
        if (!isInEditMode) {
            
            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            if (
                !pref.contains(context.getString(R.string.version_pref)) or (
                            pref.getString(context.getString(R.string.version_pref), "none") != 
                            context.getString(R.string.app_version)
                        )
            ) {
                val editor = pref.edit()
                editor.putString(
                        context.getString(R.string.version_pref), context.getString(R.string.app_version)
                )
                editor.apply()
                (context as MainActivity).show_welcome()
            } else {
                new_game_dialog()
            }
            sometimes_stupid = pref.getBoolean(context.getString(R.string.stupid_pref_key), false)
            opponent = Computer_Opponent(sometimes_stupid)
            misere = pref.getBoolean(context.getString(R.string.misere_pref_key), false)
            color_happy_droid = pref.getInt(context.getString(R.string.color_droid_happy_key), color_happy_droid)
            color_happy_eyes = pref.getInt(context.getString(R.string.color_eyes_happy_key), color_happy_eyes)
            color_worry_droid = pref.getInt(context.getString(R.string.color_droid_worry_key), color_worry_droid)
            color_worry_eyes = pref.getInt(context.getString(R.string.color_eyes_worry_key), color_worry_eyes)
            color_deact_droid = pref.getInt(context.getString(R.string.color_droid_deact_key), color_deact_droid)
            color_deact_eyes = pref.getInt(context.getString(R.string.color_eyes_deact_key), color_deact_eyes)
            pref.registerOnSharedPreferenceChangeListener(this)
        }

    }

    fun set_views(value_text_view : TextView) {
        value_view = value_text_view
    }

    private fun new_game_dialog() =
        New_Game_Dialog(context, this, game.rows.size, max_pebbles).show()

    private fun declare_winner(human_last: Boolean) {
        if (borer != null) borer!!.stop()
        val all_insults =
                if (human_last xor game.misere) context.resources.getStringArray(R.array.win_insults)
                else context.resources.getStringArray(R.array.lose_insults)
        val message =
                if (kind_of_opponent == Player_Kind.HUMAN) {
                    if (human_last xor game.misere) context.getString(R.string.two_player_won)
                    else context.getString(R.string.two_player_lost)
                }
                else if (sometimes_stupid) {
                    if (human_last xor game.misere) context.getString(R.string.you_won) + " " +
                            all_insults[0]
                    else context.getString(R.string.i_won) + " " + all_insults[0]
                } else {
                    if (human_last xor game.misere) context.getString(R.string.you_won) + " " +
                            all_insults[random.nextInt(all_insults.size - 1) + 1]
                    else context.getString(R.string.i_won) + " " +
                            all_insults[random.nextInt(all_insults.size - 1) + 1]
                }
        if (kind_of_opponent == Player_Kind.HUMAN) {
            kind_of_opponent = Player_Kind.COMPUTER
            opponent = Computer_Opponent(sometimes_stupid)
        }
        game_over_dialog = AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.game_over))
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.again_cur), this)
                .setCancelable(false)
                .show()
    }

    fun emergency_start_game() {
        kind_of_opponent = Player_Kind.COMPUTER
        start_game(game.rows.size, max_pebbles)
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
        game = Nim_Game(game_data, misere)
        opponent!!.prepare_for_new_game(game)
        draw_initial_game()
    }

    fun start_bouton_game() {
        max_pebbles = 7
        orig_num_droids = intArrayOf(7, 5, 3)
        game = Nim_Game(orig_num_droids, misere)
        opponent!!.prepare_for_new_game(game)
        draw_initial_game()
    }

    fun start_repeat_last_game() {
        game = Nim_Game(orig_num_droids, misere)
        opponent!!.prepare_for_new_game(game)
        draw_initial_game()
    }

    private fun draw_initial_game() {
        val rows = game.rows.size
        sentinels = Array(rows, { droid_standing!!.mutate().constantState.newDrawable() })
        for (sentinel in sentinels) sentinel.setColorFilter(color_happy_droid, SRC_ATOP)
        droids_to_draw = Array(
                rows, { Array(game.rows[it].pebbles, { droid_standing!!.constantState.newDrawable() }) }
        )
        eyes_to_draw = Array(
                rows, { Array(game.rows[it].pebbles, { eyes_standing!!.constantState.newDrawable() }) }
        )
        mouths_to_draw = Array(
                rows, { Array(game.rows[it].pebbles, { mouth_standing!!.constantState.newDrawable() }) }
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
                droids_to_draw[i][j].setColorFilter(color_happy_droid, SRC_ATOP)
                droids_to_draw[i][j].setBounds(
                        start_x, (start_y + row_height * i).toInt(),
                        (start_x + pebble_width).toInt(), (start_y + row_height * (i + 1)).toInt()
                )
                eyes_to_draw[i][j].setColorFilter(color_happy_eyes, SRC_ATOP)
                eyes_to_draw[i][j].setBounds(
                        start_x, (start_y + row_height * i).toInt(),
                        (start_x + pebble_width).toInt(), (start_y + row_height * (i + 1)).toInt()
                )
                mouths_to_draw[i][j].setColorFilter(color_happy_mouth, SRC_ATOP)
                mouths_to_draw[i][j].setBounds(
                        start_x, (start_y + row_height * i).toInt(),
                        (start_x + pebble_width).toInt(), (start_y + row_height * (i + 1)).toInt()
                )
            }
        }
        Rising_Droids_Animation(this, droids_to_draw, eyes_to_draw, mouths_to_draw).run()
        borer = Bore_A_Droid(this, game)
        postOnAnimationDelayed(borer, 3000)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        val droid_eyes = ResourcesCompat.getDrawable(resources, R.drawable.ic_android_eyes_standing, null)
        val droid_mouth = ResourcesCompat.getDrawable(resources, R.drawable.ic_mouth_standing, null)

        var pebble_width = contentWidth / (max_pebbles.toFloat() + 1.5f)
        var row_height = contentHeight / (game.rows.size.toFloat())
        if (pebble_width > row_height) pebble_width = row_height
        else row_height = pebble_width
        val start_y = contentHeight / 2 - row_height * game.rows.size / 2
        for ((i, row) in game.rows.withIndex()) {
            sentinels[i].setBounds(
                    paddingLeft, (start_y + row_height * i).toInt(),
                    paddingLeft + pebble_width.toInt(), (start_y + row_height * (i + 1)).toInt()
            )
            droid_eyes!!.setBounds(
                    paddingLeft, (start_y + row_height * i).toInt(),
                    paddingLeft + pebble_width.toInt(), (start_y + row_height * (i + 1)).toInt()
            )
            droid_eyes.setColorFilter(color_happy_eyes, SRC_ATOP)
            droid_mouth!!.setBounds(
                    paddingLeft, (start_y + row_height * i).toInt(),
                    paddingLeft + pebble_width.toInt(), (start_y + row_height * (i + 1)).toInt()
            )
            droid_mouth.setColorFilter(color_happy_mouth, SRC_ATOP)
            sentinels[i].draw(canvas)
            droid_eyes.draw(canvas)
            droid_mouth.draw(canvas)
            val pebble_targeted = minOf(orig_num_droids[i], target_pebble + orig_num_droids[i] - row.pebbles)
            val first_living_droid = orig_num_droids[i] - row.pebbles
            if (highlight && i == target_row) {
                for (j in first_living_droid.until(pebble_targeted)) {
                    droids_to_draw[i][j].mutate().setColorFilter(color_worry_droid, SRC_ATOP)
                    eyes_to_draw[i][j].mutate().setColorFilter(color_worry_eyes, SRC_ATOP)
                    mouths_to_draw[i][j].mutate().setColorFilter(color_worry_mouth, SRC_ATOP)
                }
            }
            for (draw_droid in droids_to_draw[i])
                draw_droid.draw(canvas)
            for (draw_eyes in eyes_to_draw[i])
                draw_eyes.draw(canvas)
            for (draw_mouth in mouths_to_draw[i])
                draw_mouth.draw(canvas)
            if (highlight && i == target_row) {
                for (j in first_living_droid.until(pebble_targeted)) {
                    droids_to_draw[i][j].mutate().setColorFilter(color_happy_droid, SRC_ATOP)
                    eyes_to_draw[i][j].mutate().setColorFilter(color_happy_eyes, SRC_ATOP)
                    mouths_to_draw[i][j].mutate().setColorFilter(color_happy_mouth, SRC_ATOP)
                }
            }
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
                val first_droid_in_row = orig_num_droids[target_row] -
                        game.rows[target_row].pebbles + 1
                val pebble_targeted = (x - start_x).toInt() / pebble_width.toInt() + 1
                target_pebble =
                        pebble_targeted -
                        (orig_num_droids[target_row] - game.rows[target_row].pebbles)
                if (target_pebble < 0) target_pebble = 0
                val action = p1.actionMasked
                if (action == ACTION_DOWN || action == ACTION_MOVE) {
                    if (pebble_targeted in first_droid_in_row..orig_num_droids[target_row])
                        highlight = true
                } else if (action == ACTION_UP && highlight) {
                    highlight = false
                    if ( // no cheating!
                        game.rows[target_row].pebbles != 0 &&
                                pebble_targeted in first_droid_in_row..orig_num_droids[target_row]
                    ) {
                        if (kind_of_opponent == Player_Kind.HUMAN)
                            (opponent as Human_Opponent).notify_of_move(Move(target_row, target_pebble))
                        val curr_pebbles = orig_num_droids[target_row] - game.rows[target_row].pebbles
                        var finished = game.play(Move(target_row, target_pebble))
                        val humans_choices = curr_pebbles.until(pebble_targeted)
                        monitor.stop_droids(target_row, humans_choices)
                        borer!!.stop_droids(target_row, humans_choices)
                        Sentinels_Rising_Arm(this, sentinels, target_row, true).run()
                        Sentinels_Rising_Arm(this, sentinels, target_row, false, 600).run()
                        Falling_Droids_Animation(
                                this, droids_to_draw, eyes_to_draw, mouths_to_draw,
                                target_row, humans_choices, 350
                        ).run()
                        var human_last = true
                        val my_opponent: Opponent? = opponent
                        if (!finished && my_opponent != null) {
                            human_last = false
                            finished = my_opponent.make_a_move()
                            if (kind_of_opponent == Player_Kind.COMPUTER) {
                                val last_move = my_opponent.last_move
                                val opponents_choices =
                                        (
                                                orig_num_droids[last_move.row] -
                                                        game.rows[last_move.row].pebbles - last_move.number
                                                ).until(
                                                orig_num_droids[last_move.row] -
                                                        game.rows[last_move.row].pebbles
                                        )
                                monitor.stop_droids(last_move.row, opponents_choices)
                                borer!!.stop_droids(last_move.row, opponents_choices)
                                Sentinels_Rising_Arm(this, sentinels, last_move.row, true, 1000).run()
                                Sentinels_Rising_Arm(this, sentinels, last_move.row, false, 1600).run()
                                Falling_Droids_Animation(
                                        this, droids_to_draw, eyes_to_draw, mouths_to_draw,
                                        last_move.row, opponents_choices, 1350
                                ).run()
                            }
                        }
                        if (finished) {
                            if (kind_of_opponent == Player_Kind.HUMAN)
                                (context as MainActivity).two_player_game_ended()
                            declare_winner(human_last)
                        }
                    }
                }
                invalidate()
            } else {
                highlight = false
                invalidate()
            }
        }

        return result
    }

    fun get_human_move(last_move : Move) {
        target_row = last_move.row
        target_pebble = last_move.number
        val curr_pebbles = orig_num_droids[target_row] - game.rows[target_row].pebbles
        val pebble_targeted = target_pebble + curr_pebbles
        val humans_choices = curr_pebbles.until(pebble_targeted)
        monitor.stop_droids(target_row, humans_choices)
        borer!!.stop_droids(target_row, humans_choices)
        Sentinels_Rising_Arm(this, sentinels, last_move.row, true).run()
        Sentinels_Rising_Arm(this, sentinels, last_move.row, false, 600).run()
        Falling_Droids_Animation(
                this, droids_to_draw, eyes_to_draw, mouths_to_draw,
                target_row, humans_choices, 350
        ).run()
        val finished = game.play(last_move)
        if (finished) {
            (context as MainActivity).two_player_game_ended()
            declare_winner(false)
        }
    }

    override fun onClick(p0: DialogInterface?, p1: Int) {
        new_game_dialog()
    }

    fun setup_human_game(socket: BluetoothSocket, i_am_hosting: Boolean) {

        kind_of_opponent = Player_Kind.HUMAN
        val other = Human_Opponent(this, socket)
        opponent = other
        if (i_am_hosting) {
            invalidate()
            bt_ideal_raw[0] = game.rows.size.toByte()
            bt_ideal_raw[1] = if (misere) 1.toByte() else 0.toByte()
            var i = 2
            for (row in game.rows) {
                bt_ideal_raw[i] = row.pebbles.toByte()
                i += 1
            }
            val writing_thread = BT_Writing_Thread(context, socket)
            writing_thread.execute(bt_ideal_raw)
            other.make_a_move()
        } else {
            val reading_thread = BT_Reading_Thread(context, socket, this, false)
            reading_thread.execute()
        }

    }

    override fun received_data(size: Int, data: ByteArray) {
        misere = data[1] == 1.toByte()
        val edit = PreferenceManager.getDefaultSharedPreferences(context).edit()
        edit.putBoolean(context.getString(R.string.misere_pref_key), misere)
        edit.apply()
        val toast = Toast.makeText(
                context,
                if (misere) R.string.bt_toast_misere_play else R.string.bt_toast_normal_play,
                Toast.LENGTH_LONG
        )
        toast.setGravity(Gravity.TOP, 0, 0)
        toast.show()
        val positions = Array(data[0].toInt(), { it -> data[it + 2].toInt() })
        orig_num_droids = positions.toIntArray()
        game = Nim_Game(orig_num_droids, misere)
        max_pebbles = game.max_size()
        draw_initial_game()

    }

    override fun onSharedPreferenceChanged(in_pref: SharedPreferences?, key: String?) {

        var can_continue = true
        var num_rows = game.rows.size
        var num_pebs = max_pebbles
        val pref = in_pref!!
        if (key != null) {
            when (key) {
                context.getString(R.string.pref_rows) -> {
                    num_rows = pref.getInt(context.getString(R.string.pref_rows), 3)
                    can_continue = false
                }
                context.getString(R.string.pref_max_droids) -> {
                    num_pebs = pref.getInt(context.getString(R.string.pref_max_droids), 7)
                    can_continue = false
                }
                context.getString(R.string.stupid_pref_key) -> {
                    sometimes_stupid = pref.getBoolean(context.getString(R.string.stupid_pref_key), false)
                    can_continue = false
                }
                context.getString(R.string.misere_pref_key) -> {
                    misere = pref.getBoolean(context.getString(R.string.misere_pref_key), false)
                    can_continue = false
                }
                context.getString(R.string.color_droid_happy_key) ->
                    color_happy_droid = pref.getInt(context.getString(R.string.color_droid_happy_key), color_happy_droid)
                context.getString(R.string.color_droid_worry_key) ->
                    color_worry_droid = pref.getInt(context.getString(R.string.color_droid_worry_key), color_worry_droid)
                context.getString(R.string.color_droid_deact_key) ->
                    color_deact_droid = pref.getInt(context.getString(R.string.color_droid_deact_key), color_deact_droid)
                context.getString(R.string.color_eyes_happy_key) ->
                    color_happy_eyes = pref.getInt(context.getString(R.string.color_eyes_happy_key), color_happy_eyes)
                context.getString(R.string.color_eyes_worry_key) ->
                    color_worry_eyes = pref.getInt(context.getString(R.string.color_eyes_worry_key), color_worry_eyes)
                context.getString(R.string.color_eyes_deact_key) ->
                    color_deact_eyes = pref.getInt(context.getString(R.string.color_eyes_deact_key), color_deact_eyes)
            }
        }
        if (can_continue)
            invalidate()
        else {
            max_pebbles = num_pebs
            if (kind_of_opponent == Player_Kind.COMPUTER)
                opponent = Computer_Opponent(sometimes_stupid)
            start_game(num_rows, num_pebs)
        }
    }

    fun bore_droid(row: Int, droid: Int) =
            monitor.add_droid(this, droids_to_draw, eyes_to_draw, mouths_to_draw, row, droid)

    fun finished_boring_droid(row: Int, droid: Int) = borer!!.finished_droid(row, droid)

}


class Monitor_Bored_Droids {

    private val bored_droids = LinkedList<Bored_Droids_Animation>()
    private val random = Random()

    fun add_droid(
            view: Nim_Game_View,
            droids: Array<Array<Drawable>>, eyes: Array<Array<Drawable>>,
            mouths: Array<Array<Drawable>>,
            row: Int, num: Int
    ) {
        val anim = when (random.nextInt(3)) {
            0 -> Stretching_Droids_Animation(this, view, droids, eyes, mouths, row, num)
            1 -> Dancing_Droids_Animation(this, view, droids, eyes, mouths, row, num)
            else -> Tapping_Droids_Animation(this, view, droids, eyes, mouths, row, num)
        }

        bored_droids.add(anim)
        anim.run()
    }

    fun stop_droids(row: Int, which_ones: IntRange) {
        val droids_to_remove = LinkedList<Bored_Droids_Animation>()
        for (anim in bored_droids) {
            if (anim.row == row && anim.which_one in which_ones) {
                anim.stop()
                droids_to_remove.add(anim)
            }
        }
        bored_droids.removeAll(droids_to_remove)
    }

    fun finished_droid(anim: Bored_Droids_Animation) = bored_droids.remove(anim)

}

class Bore_A_Droid(
        private val view: Nim_Game_View, private val game: Nim_Game
) : Runnable {

    private val random = Random()
    private var valid = true
    private val currently_bored = TreeSet<Int>()

    override fun run() {
        if (valid and (game.number_of_pebbles() != currently_bored.size)) {
            var row: Int
            var active_droids: Int
            var searching = true
            while (searching) {
                do {
                    row = random.nextInt(game.rows.size)
                    active_droids = view.orig_num_droids[row] - game.rows[row].pebbles
                } while (active_droids == view.orig_num_droids[row])
                val droid_to_bore = active_droids + random.nextInt(game.rows[row].pebbles)
                if (!currently_bored.contains(row * 10 + droid_to_bore)) {
                    currently_bored.add(row * 10 + droid_to_bore)
                    view.bore_droid(row, droid_to_bore)
                    searching = false
                }
            }
        }
        val delay = 2000 + (if (random.nextBoolean()) -1 else 1) * random.nextInt(1000).toLong()
        view.postOnAnimationDelayed(this,
                delay
        )
    }

    fun finished_droid(row: Int, droid: Int) {
        currently_bored.remove(row * 10 + droid)
    }

    fun stop_droids(row: Int, which_ones: IntRange) {
        currently_bored.removeAll { it -> (row == it / 10) and (which_ones.contains(it - row * 10)) }
        /*for (droid in which_ones) {
            if (currently_bored.contains(row * 10 + droid))
                currently_bored.remove(row * 10 + droid)
        }*/
    }
    fun stop() { valid = false }

}