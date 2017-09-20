package name.cantanima.droidnim

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

class Computer_Opponent : Opponent() {

    private val random = Random()

    override fun make_a_move() : Boolean {

        var result = false

        val this_game : Nim_Game? = game
        if (this_game != null) {
            val n = this_game.value()
            if (n != 0) {
                for (j in 0.until(this_game.rows.size)) {
                    /*var xor_sum = 0
                    for (k in 0.until(this_game.rows.size))
                        if (k != j)
                            xor_sum = xor_sum.xor(this_game.rows[k].pebbles)*/
                    val xor_sum = this_game.rows.fold(0, {a, b -> a xor b.pebbles}) xor
                            this_game.rows[j].pebbles
                    if (xor_sum <= this_game.rows[j].pebbles) {
                        last_move = Move(j, this_game.rows[j].pebbles - xor_sum)
                        result = this_game.play(last_move)
                        break
                    }
                }
            } else {
                var which_row : Int
                do {
                    which_row = random.nextInt(this_game.rows.size)
                } while (this_game.rows[which_row].pebbles == 0)
                last_move = Move(which_row, random.nextInt(this_game.rows[which_row].pebbles) + 1)
                result = this_game.play(last_move)
            }
        }

        return result

    }

}