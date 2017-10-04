package name.cantanima.droidnim

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.v4.content.res.ResourcesCompat

/**
 * File containing animations of droids rising and falling after destruction or reconstitution.
 */

class Falling_Droids_Animation(
        private val view: Nim_Game_View,
        private val droids: Array<Array<Drawable>>, private val eyes: Array<Array<Drawable>>,
        private val mouths: Array<Array<Drawable>>,
        private val row: Int, private val which_ones: IntRange,
        private var delay: Long = 0
) : Runnable {

    private var step = 0
    private val falling_droid : Array<Drawable?> = Array(11, { null })
    private val falling_eyes : Array<Drawable?> = Array(11, { null })
    private val falling_mouth : Array<Drawable?> = Array(11, { null })

    init {
        falling_droid[0] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_falling1, null
        )
        falling_droid[1] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_falling2, null
        )
        falling_droid[2] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_falling3, null
        )
        falling_droid[3] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_falling4, null
        )
        falling_droid[4] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_falling5, null
        )
        falling_droid[5] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_falling6, null
        )
        falling_droid[6] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_falling7, null
        )
        falling_droid[7] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_falling8, null
        )
        falling_droid[8] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_falling9, null
        )
        falling_droid[9] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_falling10, null
        )
        falling_droid[10] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_fallen, null
        )
        falling_eyes[0] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling1, null
        )
        falling_eyes[1] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling2, null
        )
        falling_eyes[2] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling3, null
        )
        falling_eyes[3] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling4, null
        )
        falling_eyes[4] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling5, null
        )
        falling_eyes[5] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling6, null
        )
        falling_eyes[6] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling7, null
        )
        falling_eyes[7] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling8, null
        )
        falling_eyes[8] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling9, null
        )
        falling_eyes[9] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling10, null
        )
        falling_eyes[10] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_fallen, null
        )
        falling_mouth[0] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_falling1, null
        )
        falling_mouth[1] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_falling2, null
        )
        falling_mouth[2] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_falling3, null
        )
        falling_mouth[3] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_falling4, null
        )
        falling_mouth[4] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_falling5, null
        )
        falling_mouth[5] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_falling6, null
        )
        falling_mouth[6] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_falling7, null
        )
        falling_mouth[7] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_falling8, null
        )
        falling_mouth[8] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_falling9, null
        )
        falling_mouth[9] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_falling10, null
        )
        falling_mouth[10] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_fallen, null
        )
    }

    override fun run() {
        if (delay != 0L) {
            val tmp = delay
            delay = 0
            view.postOnAnimationDelayed(this, tmp)
        } else {
            val which_droid_color = if (step == 10) view.color_deact_droid else view.color_worry_droid
            val which_eye_color = if (step == 10) view.color_deact_eyes else view.color_worry_eyes
            val which_mouth_color = if (step == 10) view.color_deact_mouth else view.color_worry_mouth
            for (number in which_ones) {
                val bounds = droids[row][number].bounds
                droids[row][number] = falling_droid[step]!!.constantState.newDrawable()
                droids[row][number].setColorFilter(which_droid_color, PorterDuff.Mode.SRC_ATOP)
                droids[row][number].bounds = bounds
                eyes[row][number] = falling_eyes[step]!!.constantState.newDrawable()
                eyes[row][number].setColorFilter(which_eye_color, PorterDuff.Mode.SRC_ATOP)
                eyes[row][number].bounds = bounds
                mouths[row][number] = falling_mouth[step]!!.constantState.newDrawable()
                mouths[row][number].setColorFilter(which_mouth_color, PorterDuff.Mode.SRC_ATOP)
                mouths[row][number].bounds = bounds
            }
            view.invalidate()
            step = step.inc()
            if (step < 11)
                view.postOnAnimationDelayed(this, 25)
        }
    }
}

class Rising_Droids_Animation(
        private val view: Nim_Game_View,
        private val droids: Array<Array<Drawable>>, private val eyes: Array<Array<Drawable>>,
        private val mouths: Array<Array<Drawable>>
) : Runnable {

    private var step : Int = 0
    private val rising_droid : Array<Drawable?> = Array(12, { null })
    private val rising_eyes : Array<Drawable?> = Array(12, { null })
    private val rising_mouths : Array<Drawable?> = Array(12, { null })

    init {
        rising_droid[0] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_standing, null
        )
        rising_droid[1] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_falling1, null
        )
        rising_droid[2] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_falling2, null
        )
        rising_droid[3] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_falling3, null
        )
        rising_droid[4] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_falling4, null
        )
        rising_droid[5] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_falling5, null
        )
        rising_droid[6] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_falling6, null
        )
        rising_droid[7] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_falling7, null
        )
        rising_droid[8] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_falling8, null
        )
        rising_droid[9] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_falling9, null
        )
        rising_droid[10] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_falling10, null
        )
        rising_droid[11] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_fallen, null
        )
        rising_eyes[0] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_standing, null
        )
        rising_eyes[1] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling1, null
        )
        rising_eyes[2] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling2, null
        )
        rising_eyes[3] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling3, null
        )
        rising_eyes[4] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling4, null
        )
        rising_eyes[5] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling5, null
        )
        rising_eyes[6] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling6, null
        )
        rising_eyes[7] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling7, null
        )
        rising_eyes[8] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling8, null
        )
        rising_eyes[9] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling9, null
        )
        rising_eyes[10] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_falling10, null
        )
        rising_eyes[11] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_eyes_fallen, null
        )
        rising_mouths[0] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_standing, null
        )
        rising_mouths[1] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_falling1, null
        )
        rising_mouths[2] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_falling2, null
        )
        rising_mouths[3] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_falling3, null
        )
        rising_mouths[4] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_falling4, null
        )
        rising_mouths[5] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_falling5, null
        )
        rising_mouths[6] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_falling6, null
        )
        rising_mouths[7] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_falling7, null
        )
        rising_mouths[8] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_falling8, null
        )
        rising_mouths[9] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_falling9, null
        )
        rising_mouths[10] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_falling10, null
        )
        rising_mouths[11] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_mouth_fallen, null
        )
    }

    override fun run() {
        val which_droid_color = if (step == 11) view.color_happy_droid else view.color_deact_droid
        val which_eye_color = if (step == 11) view.color_happy_eyes else view.color_deact_eyes
        val which_mouth_color = if (step == 11) view.color_happy_mouth else view.color_deact_mouth
        for (row in 0.until(droids.size))
            for (number in 0.until(droids[row].size)) {
                val bounds = droids[row][number].bounds
                droids[row][number] = rising_droid[11 - step]!!.constantState.newDrawable()
                droids[row][number].setColorFilter(which_droid_color, PorterDuff.Mode.SRC_ATOP)
                droids[row][number].bounds = bounds
                eyes[row][number] = rising_eyes[11 - step]!!.constantState.newDrawable()
                eyes[row][number].setColorFilter(which_eye_color, PorterDuff.Mode.SRC_ATOP)
                eyes[row][number].bounds = bounds
                mouths[row][number] = rising_mouths[11 - step]!!.constantState.newDrawable()
                mouths[row][number].setColorFilter(which_mouth_color, PorterDuff.Mode.SRC_ATOP)
                mouths[row][number].bounds = bounds
            }
        view.invalidate()
        step = step.inc()
        if (step < 12)
            view.postOnAnimationDelayed(this, 25)
    }
}