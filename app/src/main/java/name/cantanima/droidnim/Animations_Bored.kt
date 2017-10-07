package name.cantanima.droidnim

import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v4.content.res.ResourcesCompat
import android.util.Log

abstract class Bored_Droids_Animation : Runnable {

    abstract val monitor: Monitor_Bored_Droids
    abstract val view: Nim_Game_View
    abstract val droids: Array<Array<Drawable>>
    abstract val eyes: Array<Array<Drawable>>
    abstract val mouths: Array<Array<Drawable>>
    abstract val row: Int
    abstract val which_one: Int
    abstract val frame: Array<Drawable?>
    abstract val eye_frame: Array<Drawable?>
    abstract val mouth_frame: Array<Drawable?>
    abstract val repetitions: Int
    abstract var delay: Long
    private var performed = 0
    private var step = 0

    var bounds = Rect(0, 0, 0, 0)


    override fun run() {
        if (step < frame.size) {
            droids[row][which_one] = frame[step]!!.constantState.newDrawable()
            droids[row][which_one].setColorFilter(view.color_happy_droid, PorterDuff.Mode.SRC_ATOP)
            droids[row][which_one].bounds = bounds
            eyes[row][which_one] = eye_frame[step]!!.constantState.newDrawable()
            eyes[row][which_one].setColorFilter(view.color_happy_eyes, PorterDuff.Mode.SRC_ATOP)
            eyes[row][which_one].bounds = bounds
            mouths[row][which_one] = mouth_frame[step]!!.constantState.newDrawable()
            mouths[row][which_one].setColorFilter(view.color_happy_mouth, PorterDuff.Mode.SRC_ATOP)
            mouths[row][which_one].bounds = bounds
            view.invalidate()
            ++step
            when { // I find this less readable than if / else if / else but it shuts up lint
                step < frame.size ->
                    view.postOnAnimationDelayed(this, delay)
                performed < repetitions -> {
                    step = 0; ++performed; view.postOnAnimationDelayed(this, delay)
                }
                else -> {
                    monitor.finished_droid(this)
                    view.finished_boring_droid(row, which_one)
                }
            }
        }
    }

    fun stop() {
        droids[row][which_one] = frame[0]!!.constantState.newDrawable()
        droids[row][which_one].setColorFilter(view.color_happy_droid, PorterDuff.Mode.SRC_ATOP)
        droids[row][which_one].bounds = bounds
        eyes[row][which_one] = frame[0]!!.constantState.newDrawable()
        eyes[row][which_one].setColorFilter(view.color_happy_eyes, PorterDuff.Mode.SRC_ATOP)
        eyes[row][which_one].bounds = bounds
        step = frame.size
    }

}

/**
 * Animations of bored droids
 */
class Tapping_Droids_Animation constructor(
        override val monitor: Monitor_Bored_Droids,
        override val view: Nim_Game_View,
        override val droids: Array<Array<Drawable>>,
        override val eyes: Array<Array<Drawable>>,
        override val mouths: Array<Array<Drawable>>,
        override val row: Int, override val which_one: Int
): Bored_Droids_Animation() {

    override val repetitions = 2
    override val frame: Array<Drawable?> = Array(13, { null })
    override val eye_frame: Array<Drawable?> = Array(13, { null })
    override val mouth_frame: Array<Drawable?> = Array(13, { null })
    override var delay = 30L

    init {
        bounds = droids[row][which_one].bounds
        frame[0] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_standing, null
        )
        frame[1] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_tapping1, null
        )
        frame[2] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_tapping2, null
        )
        frame[3] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_tapping3, null
        )
        frame[4] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_tapping4, null
        )
        frame[5] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_tapping5, null
        )
        frame[6] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_tapping6, null
        )
        frame[7] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_tapping5, null
        )
        frame[8] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_tapping4, null
        )
        frame[9] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_tapping3, null
        )
        frame[10] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_tapping2, null
        )
        frame[11] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_tapping1, null
        )
        frame[12] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_standing, null
        )
        eye_frame[0] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_standing, null
        )
        eye_frame[1] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_standing, null
        )
        eye_frame[2] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_standing, null
        )
        eye_frame[3] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_standing, null
        )
        eye_frame[4] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_standing, null
        )
        eye_frame[5] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_standing, null
        )
        eye_frame[6] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_standing, null
        )
        eye_frame[7] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_standing, null
        )
        eye_frame[8] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_standing, null
        )
        eye_frame[9] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_standing, null
        )
        eye_frame[10] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_standing, null
        )
        eye_frame[11] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_standing, null
        )
        eye_frame[12] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_standing, null
        )
        mouth_frame[0] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_whistling1, null
        )
        mouth_frame[1] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_whistling2, null
        )
        mouth_frame[2] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_whistling3, null
        )
        mouth_frame[3] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_whistling4, null
        )
        mouth_frame[4] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_whistling5, null
        )
        mouth_frame[5] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_whistling6, null
        )
        mouth_frame[6] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_whistling7, null
        )
        mouth_frame[7] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_whistling8, null
        )
        mouth_frame[8] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_whistling8, null
        )
        mouth_frame[9] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_whistling6, null
        )
        mouth_frame[10] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_whistling4, null
        )
        mouth_frame[11] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_whistling2, null
        )
        mouth_frame[12] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_whistling1, null
        )
    }

}

/**
 * Animations of bored droids
 */
class Stretching_Droids_Animation constructor(
        override val monitor: Monitor_Bored_Droids,
        override val view: Nim_Game_View,
        override val droids: Array<Array<Drawable>>,
        override val eyes: Array<Array<Drawable>>,
        override val mouths: Array<Array<Drawable>>,
        override val row: Int, override val which_one: Int
): Bored_Droids_Animation() {

    override val repetitions = 0
    override val frame: Array<Drawable?> = Array(29, { null })
    override val eye_frame: Array<Drawable?> = Array(29, { null })
    override val mouth_frame: Array<Drawable?> = Array(29, { null })
    override var delay = 50L

    init {
        bounds = droids[row][which_one].bounds
        frame[0] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_standing, null
        )
        frame[1] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_stretching1, null
        )
        frame[2] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_stretching2, null
        )
        frame[3] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_stretching3, null
        )
        frame[4] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_stretching4, null
        )
        frame[5] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_stretching5, null
        )
        frame[6] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_stretching6, null
        )
        frame[7] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_stretching7, null
        )
        frame[8] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_stretching8, null
        )
        frame[9] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_stretching9, null
        )
        frame[10] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_stretching10, null
        )
        frame[11] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_stretching10, null
        )
        frame[12] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_stretching10, null
        )
        frame[13] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_stretching10, null
        )
        frame[14] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_stretching10, null
        )
        frame[15] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_stretching10, null
        )
        frame[16] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_stretching10, null
        )
        frame[17] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_stretching10, null
        )
        frame[18] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_stretching10, null
        )
        frame[19] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_stretching9, null
        )
        frame[20] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_stretching8, null
        )
        frame[21] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_stretching7, null
        )
        frame[22] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_stretching6, null
        )
        frame[23] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_stretching5, null
        )
        frame[24] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_stretching4, null
        )
        frame[25] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_stretching3, null
        )
        frame[26] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_stretching2, null
        )
        frame[27] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_stretching1, null
        )
        frame[28] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_standing, null
        )
        eye_frame[0] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling0_5, null
        )
        eye_frame[1] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling0_5, null
        )
        eye_frame[2] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling0_5, null
        )
        eye_frame[3] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling1, null
        )
        eye_frame[4] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling1, null
        )
        eye_frame[5] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling1_5, null
        )
        eye_frame[6] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling1_5, null
        )
        eye_frame[7] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling1_5, null
        )
        eye_frame[8] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling2, null
        )
        eye_frame[9] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling2, null
        )
        eye_frame[10] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling2, null
        )
        eye_frame[11] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling2_5, null
        )
        eye_frame[12] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling2_5, null
        )
        eye_frame[13] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling2_5, null
        )
        eye_frame[13] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling3, null
        )
        eye_frame[14] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling3, null
        )
        eye_frame[15] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling3, null
        )
        eye_frame[16] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling3, null
        )
        eye_frame[17] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling3, null
        )
        eye_frame[18] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling3, null
        )
        eye_frame[19] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling2_5, null
        )
        eye_frame[20] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling2_5, null
        )
        eye_frame[21] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling2_5, null
        )
        eye_frame[22] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling2, null
        )
        eye_frame[23] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling2, null
        )
        eye_frame[24] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling1_5, null
        )
        eye_frame[25] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling1, null
        )
        eye_frame[26] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling0_5, null
        )
        eye_frame[27] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_standing, null
        )
        eye_frame[28] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_standing, null
        )
        mouth_frame[0] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_stretching1, null
        )
        mouth_frame[1] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_stretching1, null
        )
        mouth_frame[2] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_stretching2, null
        )
        mouth_frame[3] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_stretching2, null
        )
        mouth_frame[4] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_stretching3, null
        )
        mouth_frame[5] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_stretching3, null
        )
        mouth_frame[6] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_stretching4, null
        )
        mouth_frame[7] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_stretching4, null
        )
        mouth_frame[8] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_stretching5, null
        )
        mouth_frame[9] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_stretching5, null
        )
        mouth_frame[10] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_stretching6, null
        )
        mouth_frame[11] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_stretching6, null
        )
        mouth_frame[12] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_stretching7, null
        )
        mouth_frame[13] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_stretching7, null
        )
        mouth_frame[13] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_stretching7, null
        )
        mouth_frame[14] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_stretching7, null
        )
        mouth_frame[15] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_stretching7, null
        )
        mouth_frame[16] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_stretching7, null
        )
        mouth_frame[17] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_stretching6, null
        )
        mouth_frame[18] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_stretching6, null
        )
        mouth_frame[19] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_stretching5, null
        )
        mouth_frame[20] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_stretching5, null
        )
        mouth_frame[21] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_stretching4, null
        )
        mouth_frame[22] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_stretching4, null
        )
        mouth_frame[23] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_stretching3, null
        )
        mouth_frame[24] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_stretching3, null
        )
        mouth_frame[25] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_stretching2, null
        )
        mouth_frame[26] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_stretching2, null
        )
        mouth_frame[27] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_stretching1, null
        )
        mouth_frame[28] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_stretching1, null
        )
    }

}

/**
 * Animations of bored droids
 */
class Dancing_Droids_Animation constructor(
        override val monitor: Monitor_Bored_Droids,
        override val view: Nim_Game_View,
        override val droids: Array<Array<Drawable>>,
        override val eyes: Array<Array<Drawable>>,
        override val mouths: Array<Array<Drawable>>,
        override val row: Int, override val which_one: Int
): Bored_Droids_Animation() {

    override val repetitions = 2
    override val frame: Array<Drawable?> = Array(20, { null })
    override val eye_frame: Array<Drawable?> = Array(20, { null })
    override val mouth_frame: Array<Drawable?> = Array(20, { null })
    override var delay = 30L

    init {
        bounds = droids[row][which_one].bounds
        frame[0] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_dancing1, null
        )
        frame[1] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_dancing2, null
        )
        frame[2] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_dancing3, null
        )
        frame[3] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_dancing4, null
        )
        frame[4] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_dancing5, null
        )
        frame[5] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_dancing4, null
        )
        frame[6] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_dancing3, null
        )
        frame[7] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_dancing2, null
        )
        frame[8] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_dancing1, null
        )
        frame[9] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_standing, null
        )
        frame[10] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_dancing10, null
        )
        frame[11] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_dancing9, null
        )
        frame[12] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_dancing8, null
        )
        frame[13] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_dancing7, null
        )
        frame[14] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_dancing6, null
        )
        frame[15] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_dancing7, null
        )
        frame[16] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_dancing8, null
        )
        frame[17] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_dancing9, null
        )
        frame[18] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_dancing10, null
        )
        frame[19] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_standing, null
        )
        eye_frame[0] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_eyes_dancing1, null
        )
        eye_frame[1] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_eyes_dancing2, null
        )
        eye_frame[2] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_eyes_dancing3, null
        )
        eye_frame[3] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_eyes_dancing4, null
        )
        eye_frame[4] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_eyes_dancing5, null
        )
        eye_frame[5] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_eyes_dancing4, null
        )
        eye_frame[6] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_eyes_dancing3, null
        )
        eye_frame[7] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_eyes_dancing2, null
        )
        eye_frame[8] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_eyes_dancing1, null
        )
        eye_frame[9] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_standing, null
        )
        eye_frame[10] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_eyes_dancing6, null
        )
        eye_frame[11] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_eyes_dancing7, null
        )
        eye_frame[12] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_eyes_dancing8, null
        )
        eye_frame[13] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_eyes_dancing9, null
        )
        eye_frame[14] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_eyes_dancing10, null
        )
        eye_frame[15] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_eyes_dancing9, null
        )
        eye_frame[16] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_eyes_dancing8, null
        )
        eye_frame[17] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_eyes_dancing7, null
        )
        eye_frame[18] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_eyes_dancing6, null
        )
        eye_frame[19] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_standing, null
        )
        mouth_frame[0] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_standing, null
        )
        mouth_frame[1] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_standing, null
        )
        mouth_frame[2] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_standing, null
        )
        mouth_frame[3] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_standing, null
        )
        mouth_frame[4] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_standing, null
        )
        mouth_frame[5] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_standing, null
        )
        mouth_frame[6] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_standing, null
        )
        mouth_frame[7] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_standing, null
        )
        mouth_frame[8] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_standing, null
        )
        mouth_frame[9] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_standing, null
        )
        mouth_frame[10] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_standing, null
        )
        mouth_frame[11] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_standing, null
        )
        mouth_frame[12] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_standing, null
        )
        mouth_frame[13] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_standing, null
        )
        mouth_frame[14] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_standing, null
        )
        mouth_frame[15] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_standing, null
        )
        mouth_frame[16] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_standing, null
        )
        mouth_frame[17] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_standing, null
        )
        mouth_frame[18] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_standing, null
        )
        mouth_frame[19] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_standing, null
        )
    }

}

