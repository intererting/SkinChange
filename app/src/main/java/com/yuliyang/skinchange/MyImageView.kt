package com.yuliyang.skinchange

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView

class MyImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ImageView(context, attrs, defStyleAttr), AutoChangeableA {
    override fun changeWithDrawable(drawable: Drawable) {
        setImageDrawable(drawable)
    }
}