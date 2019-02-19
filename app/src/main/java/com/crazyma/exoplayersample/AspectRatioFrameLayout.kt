package com.crazyma.exoplayersample

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout

class AspectRatioFrameLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var aspectRatioWidth = 0f
    private var aspectRatioHeight = 0f

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.AspectRatioFrameLayout,
            0,0).apply {
                try{
                    aspectRatioWidth = getFloat(R.styleable.AspectRatioFrameLayout_ratioWidth, 0f)
                    aspectRatioHeight = getFloat(R.styleable.AspectRatioFrameLayout_ratioHeight, 0f)
                }finally {
                    recycle()
                }
            }
    }

    fun removeRatio() {
        aspectRatioWidth = 0f
        aspectRatioHeight = 0f
        requestLayout()
    }

    fun setRatio(width: Float, height: Float) {
        aspectRatioWidth = width
        aspectRatioHeight = height
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val ratioWidth = aspectRatioWidth
        val ratioHeight = aspectRatioHeight
        if (ratioWidth == 0f || ratioHeight == 0f) {
            return super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
        val ratio = ratioWidth / ratioHeight

        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)

        var byWidth = widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST
        var byHeight = heightMode == MeasureSpec.EXACTLY || heightMode == MeasureSpec.AT_MOST
        if (byWidth && byHeight) {
            if (widthSize / heightSize.toFloat() > ratio) {
                byWidth = false
            } else {
                byHeight = false
            }
        }

        val finalWidth: Int
        val finalHeight: Int
        when {
            byWidth -> {
                finalWidth = widthSize
                finalHeight = (widthSize / ratio).toInt()
            }
            byHeight -> {
                finalWidth = (heightSize * ratio).toInt()
                finalHeight = heightSize
            }
            else -> {
                return super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            }
        }
        super.onMeasure(
            View.MeasureSpec.makeMeasureSpec(finalWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(finalHeight, View.MeasureSpec.EXACTLY)
        )
    }
}
