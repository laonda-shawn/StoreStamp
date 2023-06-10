package io.laonda.storestamp.utils

import android.app.Dialog
import android.content.Context
import android.widget.LinearLayout

import android.widget.ProgressBar

class LoadingDialog {
    companion object {
        private var dialog: Dialog? = null

        fun show(context: Context) {
            if (dialog == null) {
                createDialog(context)
            } else if (dialog?.isShowing == false) {
                createDialog(context)
            }
        }

        fun hide() {
            dialog.takeIf { (it != null && it.isShowing == true) }?.let {
                it.dismiss()
                dialog = null
            }
        }

        private fun createDialog(context: Context) {
            val pb = ProgressBar(context)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            dialog = null
            dialog = Dialog(context).apply {
                setContentView(pb, params)
                setCancelable(false)
            }
            dialog?.show()
        }
    }
}