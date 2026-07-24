package com.oduit.app

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private val BLUE = Color.parseColor("#1D74B8")
    private val CHARCOAL = Color.parseColor("#222222")
    private val TAGLINE_GRAY = Color.parseColor("#333333")

    private val isDarkMode: Boolean
        get() = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

    private val bgColor: Int get() = if (isDarkMode) Color.parseColor("#121212") else Color.WHITE
    private val textColor: Int get() = if (isDarkMode) Color.WHITE else CHARCOAL
    private val tagColor: Int get() = if (isDarkMode) Color.parseColor("#AAAAAA") else TAGLINE_GRAY

    companion object {
        private const val DURATION_LOGO = 900L
        private const val DURATION_TEXT = 500L
        private const val DURATION_TAGLINE = 400L
        private const val HOLD_BEFORE_EXIT = 600L
    }

    private lateinit var logoView: LogoView
    private lateinit var brandText: TextView
    private lateinit var taglineText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(buildLayout())
        runAnimation()
    }

    private fun buildLayout(): View {
        val root = FrameLayout(this).apply { setBackgroundColor(bgColor) }

        val column = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER,
            )
        }

        val logoSize = dp(130)
        logoView = LogoView(this).apply {
            layoutParams = LinearLayout.LayoutParams(logoSize, logoSize)
            alpha = 0f; scaleX = 0.4f; scaleY = 0.4f
        }
        column.addView(logoView)

        brandText = TextView(this).apply {
            text = "O'Duit"
            setTextColor(textColor)
            textSize = 34f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            alpha = 0f; translationY = dp(20).toFloat()
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            )
            lp.topMargin = dp(20); layoutParams = lp
        }
        column.addView(brandText)

        taglineText = TextView(this).apply {
            text = "- DUIT KAMU, KENDALI KAMU -"
            setTextColor(tagColor)
            textSize = 11f
            letterSpacing = 0.15f
            alpha = 0f
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            )
            lp.topMargin = dp(6); layoutParams = lp
        }
        column.addView(taglineText)
        root.addView(column)

        return root
    }

    private fun runAnimation() {
        val logoScaleX = ObjectAnimator.ofFloat(logoView, View.SCALE_X, 0.4f, 1f)
        val logoScaleY = ObjectAnimator.ofFloat(logoView, View.SCALE_Y, 0.4f, 1f)
        val logoAlpha = ObjectAnimator.ofFloat(logoView, View.ALPHA, 0f, 1f)
        val logoSet = AnimatorSet().apply {
            playTogether(logoScaleX, logoScaleY, logoAlpha)
            duration = DURATION_LOGO; interpolator = OvershootInterpolator(1.2f)
        }

        val brandAlpha = ObjectAnimator.ofFloat(brandText, View.ALPHA, 0f, 1f)
        val brandTrans = ObjectAnimator.ofFloat(brandText, View.TRANSLATION_Y, dp(20).toFloat(), 0f)
        val brandSet = AnimatorSet().apply {
            playTogether(brandAlpha, brandTrans); duration = DURATION_TEXT
        }
        val taglineAlpha = ObjectAnimator.ofFloat(taglineText, View.ALPHA, 0f, 1f).apply { duration = DURATION_TAGLINE }

        logoSet.addListener(onEnd { brandSet.start() })
        brandSet.addListener(onEnd {
            taglineAlpha.start()
            taglineAlpha.addListener(onEnd {
                logoView.postDelayed({ goToMain() }, HOLD_BEFORE_EXIT)
            })
        })
        logoSet.start()
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    private inline fun onEnd(crossinline fn: () -> Unit) = object : Animator.AnimatorListener {
        override fun onAnimationStart(a: Animator) {}
        override fun onAnimationCancel(a: Animator) {}
        override fun onAnimationRepeat(a: Animator) {}
        override fun onAnimationEnd(a: Animator) = fn()
    }

    private inner class LogoView(ctx: android.content.Context) : View(ctx) {
        private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = BLUE }
        private val oPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE; style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND; strokeWidth = 28f
        }
        private val bgRect = RectF()
        private val arcRect = RectF()

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val w = width.toFloat(); val h = height.toFloat(); val s = w / 100f

            // Squircle background
            bgRect.set(4f * s, 4f * s, w - 4f * s, h - 4f * s)
            canvas.drawRoundRect(bgRect, 16f * s, 16f * s, bgPaint)

            // O Ring — huruf 'O' putih (nama panggilan)
            val r = 24f * s
            oPaint.style = Paint.Style.STROKE
            oPaint.strokeWidth = 28f
            arcRect.set(w / 2f - r, h / 2f - r, w / 2f + r, h / 2f + r)
            canvas.drawOval(arcRect, oPaint)
        }
    }
}
