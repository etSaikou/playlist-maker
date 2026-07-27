package com.saikou.playlistmaker.player.ui.custom_view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.saikou.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var playBitmap: Bitmap? = null
    private var pauseBitmap: Bitmap? = null
    private var isPlaying = false

    private val imageRect = RectF()

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.PlaybackButtonView, 0, 0).apply {
            try {
                val playResId = getResourceId(R.styleable.PlaybackButtonView_playIconRes, 0)
                val pauseResId = getResourceId(R.styleable.PlaybackButtonView_pauseIconRes, 0)

                val playbackButtonColor = context.getColor(R.color.settings_text_button)

                if (playResId != 0) {
                    val drawable = AppCompatResources.getDrawable(context, playResId)
                    drawable?.setTint(playbackButtonColor)
                    playBitmap = drawable?.toBitmap()
                }
                if (pauseResId != 0) {
                    val drawable = AppCompatResources.getDrawable(context, pauseResId)
                    drawable?.setTint(playbackButtonColor)
                    pauseBitmap = drawable?.toBitmap()
                }
            } finally {
                recycle()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val left = paddingLeft.toFloat()
        val top = paddingTop.toFloat()
        val right = w - paddingRight.toFloat()
        val bottom = h - paddingBottom.toFloat()
        imageRect.set(left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val bitmap = if (isPlaying) pauseBitmap else playBitmap
        bitmap?.let {
            canvas.drawBitmap(it, null, imageRect, null)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) return false

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }
            MotionEvent.ACTION_UP -> {
                isPlaying = !isPlaying
                invalidate()
                performClick()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    fun setState(isPlaying: Boolean) {
        if (this.isPlaying != isPlaying) {
            this.isPlaying = isPlaying
            invalidate()
        }
    }
}