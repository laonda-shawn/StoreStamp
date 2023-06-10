package io.laonda.storestamp.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import io.laonda.storestamp.R
import io.laonda.storestamp.databinding.NumberKeyboardLayoutBinding

class NumberKeyboardLayout constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {
    val binding: NumberKeyboardLayoutBinding

    init {
        LayoutInflater.from(context).inflate(R.layout.number_keyboard_layout, this).also { view ->
            binding = NumberKeyboardLayoutBinding.bind(view)
        }
    }

    fun setInputView(inputView: TextView, defalutValue: String = "") {
        with (binding) {
            setkeyOnClickListener(oneTextView, inputView)
            setkeyOnClickListener(twoTextView, inputView)
            setkeyOnClickListener(threeTextView, inputView)
            setkeyOnClickListener(fourTextView, inputView)
            setkeyOnClickListener(fiveTextView, inputView)
            setkeyOnClickListener(sixTextView, inputView)
            setkeyOnClickListener(sevenTextView, inputView)
            setkeyOnClickListener(eightTextView, inputView)
            setkeyOnClickListener(nineTextView, inputView)
            setkeyOnClickListener(zeroTextView, inputView)

            deleteLayout.setOnClickListener {
                inputView.text = inputView.text.toString().takeIf { it.isNotEmpty() }?.let {
                    it.substring(0, it.length -1).takeIf { it.length > 0 }?.let {
                        it
                    } ?: defalutValue
                }
            }
        }
    }

    fun setkeyOnClickListener(view: View, inputView: TextView) {
        view.setOnClickListener {
            inputView.text = inputView.text.toString() + (it as TextView).text.toString()
        }
    }
}