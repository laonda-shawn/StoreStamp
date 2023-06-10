package io.laonda.storestamp.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import io.laonda.storestamp.BuildConfig
import io.laonda.storestamp.R
import io.laonda.storestamp.databinding.ActivityMainBinding
import io.laonda.storestamp.ui.fragment.CouponFragment
import io.laonda.storestamp.ui.fragment.CustomerFragment
import io.laonda.storestamp.ui.fragment.SmsFragment
import io.laonda.storestamp.ui.fragment.StampFragment
import java.io.PrintWriter
import java.io.StringWriter

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        BuildConfig.DEBUG.takeIf { it == true }?.let {
            Thread.setDefaultUncaughtExceptionHandler(object : Thread.UncaughtExceptionHandler {
                override fun uncaughtException(thread: Thread, ex: Throwable) {
                    val sw = StringWriter()
                    val pw = PrintWriter(sw)
                    ex.printStackTrace(pw)

                    val intent = Intent(this@MainActivity, CrashLogActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra(CrashLogActivity.CRASH_LOG, "\n" + sw.toString())
                    startActivity(intent)

                    System.exit(10)
                }
            })
//        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        window.statusBarColor = getColor(R.color.main_color)
        window.navigationBarColor = getColor(R.color.main_color)

        val stampFragment = StampFragment.newInstance()
        val couponFragment = CouponFragment.newInstance()
        val customerFragment = CustomerFragment.newInstance()
        val smsFragment = SmsFragment.newInstance()

        replaseFragment(stampFragment)

        with (binding) {
            bottomNView.itemIconTintList = null
            bottomNView.setOnItemSelectedListener { menu ->
                when (menu.itemId) {
                    R.id.stamp -> {
                        replaseFragment(stampFragment)
                    }

//                    R.id.sms -> {
//                        replaseFragment(smsFragment)
//                    }

                    R.id.customer -> {
                        replaseFragment(customerFragment)
                    }

                    R.id.coupon -> {
                        replaseFragment(couponFragment)
                    }
                }

                if (menu.itemId != R.id.stamp) {
                    (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
                        ?.hideSoftInputFromWindow(bottomNView.windowToken, 0)
                }

                true
            }
        }
    }

    private fun replaseFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit()
    }

    override fun onBackPressed() {
        // backkey 방지.
    }
}