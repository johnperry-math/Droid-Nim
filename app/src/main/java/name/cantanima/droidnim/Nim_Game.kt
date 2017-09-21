package name.cantanima.droidnim

/**
 * Classes pertaining to Nim game data.
 * @see Move
 * @see Nim_Row
 * @see Nim_Game
 */

/**
 * A Move consists of a row and a number of pebbles to remove from that row.
 */
data class Move(val row: Int, val number: Int)

/**
 * A Nim_Row consists of a number of pebbles. You may remove fewer pebbles than actually lie in it.
 */
class Nim_Row(var pebbles: Int) {

    /**
     * Returns true if and only if it removes the indicated number of pebbles from the row,
     * if and only if it can.
     */
    fun remove(num: Int): Boolean {
        val result = num <= pebbles
        if (result) pebbles -= num
        return result
    }

    /**
     * Indicates whether some other object is "equal to" this one. Implementations must fulfil the following
     * requirements:
     *
     * * Reflexive: for any non-null reference value x, x.equals(x) should return true.
     * * Symmetric: for any non-null reference values x and y, x.equals(y) should return true if and only if y.equals(x) returns true.
     * * Transitive:  for any non-null reference values x, y, and z, if x.equals(y) returns true and y.equals(z) returns true, then x.equals(z) should return true
     * * Consistent:  for any non-null reference values x and y, multiple invocations of x.equals(y) consistently return true or consistently return false, provided no information used in equals comparisons on the objects is modified.
     *
     * Note that the `==` operator in Kotlin code is translated into a call to [equals] when objects on both sides of the
     * operator are not null.
     */
    override fun equals(other: Any?): Boolean = (other is Nim_Row && pebbles == other.pebbles)

    /**
     * Returns a string representation of the object.
     */
    override fun toString() = "|".repeat(pebbles)

    /**
     * Returns a hash code value for the object.  The general contract of hashCode is:
     *
     * * Whenever it is invoked on the same object more than once, the hashCode method must consistently return the same integer, provided no information used in equals comparisons on the object is modified.
     * * If two objects are equal according to the equals() method, then calling the hashCode method on each of the two objects must produce the same integer result.
     */
    override fun hashCode(): Int = pebbles.hashCode()

}

/**
 * A Nim_Game consists of several rows of pebbles.
 * You may play from a Nim_Game using Move's.
 * The Move may not try to remove more pebbles than are actually in the row.
 * @see play
 * @param row_pebbles an array consisting of the number of pebbles in each row
 */
class Nim_Game(row_pebbles: IntArray, val misere : Boolean = false) {

    var rows: Array<Nim_Row>

    init {
        rows = Array(row_pebbles.size, { i -> Nim_Row(row_pebbles[i]) })
    }

    /**
     * @return the maximum number of pebbles in any row
     */
    fun max_size() = rows.fold(0, { a, b -> maxOf(a, b.pebbles) })

    /**
     * Returns true if and only if it removes the indicated number of pebbles from the indicated row
     * if and only if it can.
     */
    fun play(move : Move) : Boolean {
        rows[move.row].remove(move.number)
        return !rows.any({ it.pebbles > 0 })
    }

    /**
     * Returns the value of the game.
     */
    fun value() = rows.fold(0, { a, b -> a xor b.pebbles })

    /**
     * Returns a string representation of the object.
     */
    override fun toString() = rows.fold("", { a, b -> a + b.toString() + "\n" })

}