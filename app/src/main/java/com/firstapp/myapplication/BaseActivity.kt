package com.firstapp.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Base activity that applies smooth slide transitions between screens.
 *
 * - Launching a new screen slides it in from the right while the current
 *   screen slides out to the left.
 * - Pressing Back (or finishing normally) slides the previous screen back in
 *   from the left while the current screen slides out to the right.
 *
 * Every screen in the app extends this class so navigation feels consistent
 * and never abrupt. Ripple feedback on buttons / FABs is provided by the
 * Material components themselves; [com.firstapp.myapplication.utils.UiAnimations]
 * adds the extra press-scale and list-item entrance animations.
 */
open class BaseActivity : AppCompatActivity() {

    /** When true, the next [finish] call will not play an exit animation. */
    private var skipFinishAnimation = false

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun startActivity(intent: Intent?, options: Bundle?) {
        super.startActivity(intent, options)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    @Suppress("DEPRECATION")
    override fun startActivityForResult(intent: Intent, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    @Suppress("DEPRECATION")
    override fun startActivityForResult(intent: Intent, requestCode: Int, options: Bundle?) {
        super.startActivityForResult(intent, requestCode, options)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun finish() {
        super.finish()
        if (!skipFinishAnimation) {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    /**
     * Finishes this activity without playing the exit transition. Used when
     * the whole task is being replaced (e.g. the first-time setup redirect),
     * where an exit animation would be jarring.
     */
    protected fun finishWithoutAnimation() {
        skipFinishAnimation = true
        finish()
    }
}
