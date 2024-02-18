package com.uyoung.sportsmatch.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.uyoung.sportsmatch.databinding.ViewDialogIndicatorBinding

class DialogIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    init {
        ViewDialogIndicatorBinding.inflate(LayoutInflater.from(context), this)
    }
}