package io.laonda.storestamp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import io.laonda.storestamp.R
import io.laonda.storestamp.databinding.ActivitySendSmsBinding

class SendSMSActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySendSmsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendSmsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        window.statusBarColor = getColor(R.color.main_color)
        window.navigationBarColor = getColor(R.color.main_color)
    }
}