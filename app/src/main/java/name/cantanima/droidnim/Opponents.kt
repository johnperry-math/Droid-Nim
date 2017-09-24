package name.cantanima.droidnim

import android.bluetooth.BluetoothSocket
import android.os.AsyncTask
import android.util.Log
import java.util.*

/**
 * File containing data for opponents.
 * @see Opponent
 * @see Computer_Opponent
 */

val tag = "Opponents"

abstract class Opponent {

    var last_move = Move(-1,-1)

    fun prepare_for_new_game(new_game : Nim_Game) {
        game = new_game
    }

    abstract fun make_a_move() : Boolean

    protected var game : Nim_Game? = null

}

class Computer_Opponent(private val sometimes_stupid : Boolean = false) : Opponent() {

    private val random = Random()

    override fun make_a_move() : Boolean {

        var result = false
        var played = false

        val this_game : Nim_Game? = game
        if (this_game != null) {
            if (sometimes_stupid && random.nextBoolean()) {
                played = true
                last_move = select_random_move()
            }
            if (!played and this_game.misere) {
                val num_non_singletons = this_game.rows.sumBy { it -> if (it.pebbles > 1) 1 else 0 }
                val num_singletons = this_game.rows.sumBy { it -> if (it.pebbles == 1) 1 else 0 }
                if (num_non_singletons == 1) {
                    val j = this_game.rows.indexOfFirst { it -> it.pebbles > 1 }
                    val leave = if (num_singletons % 2 == 0) 1 else 0
                    last_move = Move(j, this_game.rows[j].pebbles - leave)
                    played = true
                } else if (num_non_singletons == 0) {
                    val j = this_game.rows.indexOfFirst { it -> it.pebbles != 0 }
                    last_move = Move(j, this_game.rows[j].pebbles)
                    played = true
                }
            }
            if (!played) {
                val n = this_game.value()
                if (n != 0) {
                    for (j in 0.until(this_game.rows.size)) {
                        val xor_sum = this_game.rows.fold(0, { a, b -> a xor b.pebbles }) xor
                                this_game.rows[j].pebbles
                        if (xor_sum <= this_game.rows[j].pebbles) {
                            last_move = Move(j, this_game.rows[j].pebbles - xor_sum)
                            break
                        }
                    }
                } else {
                    last_move = select_random_move()
                }
            }
            result = this_game.play(last_move)
        }

        return result

    }

    private fun select_random_move() : Move {
        var which_row: Int
        val this_game = game!!
        do {
            which_row = random.nextInt(this_game.rows.size)
        } while (this_game.rows[which_row].pebbles == 0)
        return Move(which_row, random.nextInt(this_game.rows[which_row].pebbles) + 1)
    }

}

class Human_Opponent(private val game_view : Nim_Game_View, private val bt_socket : BluetoothSocket)
    : Opponent(), BTR_Listener
{

    private val bt_raw_data = arrayOfNulls<Byte>(4)

    fun notify_of_move(M: Move) {
        bt_raw_data[0] = 2.toByte()
        bt_raw_data[1] = M.row.toByte()
        bt_raw_data[2] = M.number.toByte()
        bt_raw_data[3] = 0.toByte()
        val bt_writer = BT_Writing_Thread(game_view.context, bt_socket)
        bt_writer.execute(bt_raw_data)
    }

    override fun make_a_move(): Boolean {
        val bt_reader = BT_Reading_Thread(
                game_view.context, bt_socket, this, true
        )
        bt_reader.execute()
        return false
    }

    override fun received_data(size: Int, data: ByteArray?) {
        val p = Move(data!![1].toInt(), data[2].toInt())
        game_view.get_human_move(p)
    }

}