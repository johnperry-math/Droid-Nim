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
    abstract val row: Int
    abstract val which_one: Int
    abstract val frame: Array<Drawable?>
    abstract val repetitions: Int
    private var performed = 0

    var bounds = Rect(0, 0, 0, 0)

    private var step = 0

    override fun run() {
        if (step < frame.size) {
            droids[row][which_one] = frame[step]!!.constantState.newDrawable()
            droids[row][which_one].setColorFilter(view.color_happy_droid, PorterDuff.Mode.SRC_ATOP)
            droids[row][which_one].bounds = bounds
            view.invalidate()
            ++step
            if (step < frame.size)
                view.postOnAnimationDelayed(this, 25)
            else if (performed < repetitions) {
                step = 0
                ++performed
                view.postOnAnimationDelayed(this, 25)
            } else
                monitor.finished_droid(this)
        }
    }

    fun stop() {
        droids[row][which_one] = frame[0]!!.constantState.newDrawable()
        droids[row][which_one].setColorFilter(view.color_happy_droid, PorterDuff.Mode.SRC_ATOP)
        droids[row][which_one].bounds = bounds
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
        override val row: Int, override val which_one: Int
): Bored_Droids_Animation() {

    override val repetitions = 3
    override val frame: Array<Drawable?> = Array(13, { null })

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
    }

}

