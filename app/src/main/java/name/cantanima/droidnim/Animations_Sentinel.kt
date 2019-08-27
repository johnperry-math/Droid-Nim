package name.cantanima.droidnim

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.v4.content.res.ResourcesCompat

/**
 * File containing animations of sentinels raising and lowering arms.
 */
class Sentinels_Rising_Arm(
        private val view : Nim_Game_View,
        private val sentinels : Array<Drawable>,
        private val which_one : Int,
        forward : Boolean,
        private var delay: Long = 0
) : Runnable {

    private var delta = 1
    private val range = 0..10
    private var step = 0
    private val frames : Array<Drawable?> = Array(11) { null }

    init {
        if (!forward) {
            delta = -1
            step = 10
        }
        frames[0] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_standing, null
        )
        frames[1] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_arm_rising1, null
        )
        frames[2] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_arm_rising2, null
        )
        frames[3] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_arm_rising3, null
        )
        frames[4] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_arm_rising4, null
        )
        frames[5] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_arm_rising5, null
        )
        frames[6] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_arm_rising6, null
        )
        frames[7] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_arm_rising7, null
        )
        frames[8] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_arm_rising8, null
        )
        frames[9] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_arm_rising9, null
        )
        frames[10] = ResourcesCompat.getDrawable(
                view.resources, R.drawable.ic_android_arm_rising10, null
        )
    }

    override fun run() {

        if (delay != 0L) {
            val tmp = delay
            delay = 0
            view.postOnAnimationDelayed(this, tmp)
        } else {
            sentinels[which_one] = frames[step]!!.constantState!!.newDrawable()
            sentinels[which_one].setColorFilter(view.color_happy_droid, PorterDuff.Mode.SRC_ATOP)
            view.invalidate()
            step += delta
            if (step in range)
                view.postOnAnimationDelayed(this, 25)
        }

    }
}