package io.laonda.storestamp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.laonda.customerstamp.net.points.MemberRes
import io.laonda.storestamp.net.stamps.AccumulateReq
import io.laonda.storestamp.R
import io.laonda.storestamp.databinding.ActivityCustomerDetailBinding
import io.laonda.storestamp.net.RetrofitCallback
import io.laonda.storestamp.net.RetrofitClient
import io.laonda.storestamp.ui.activity.CustomerCouponListActivity.Companion.CUSTOMER_ID
import io.laonda.storestamp.ui.activity.CustomerCouponListActivity.Companion.PHONE_NUMBER
import io.laonda.storestamp.utils.LoadingDialog
import io.laonda.storestamp.utils.displayError
import retrofit2.Response
import java.io.PrintWriter
import java.io.StringWriter

class CustomerDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCustomerDetailBinding

    companion object {
        val ID = "ID"
        val MOBILE = "MOBILE"
        val STAMP_COUNT = "STAMP_COUNT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        BuildConfig.DEBUG.takeIf { it == true }?.let {
            Thread.setDefaultUncaughtExceptionHandler(object : Thread.UncaughtExceptionHandler {
                override fun uncaughtException(thread: Thread, ex: Throwable) {
                    val sw = StringWriter()
                    val pw = PrintWriter(sw)
                    ex.printStackTrace(pw)

                    val intent = Intent(this@CustomerDetailActivity, CrashLogActivity::class.java)
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

        val customerId = getIntent().getIntExtra(ID, -1)

        with (binding) {
            phoneNumberTextView.apply {
                text = intent.getStringExtra(MOBILE)
                setOnClickListener {
                    finish()
                }
            }
            stampTitleTextView.text = getString(R.string.accumulate_stamps, intent.getIntExtra(STAMP_COUNT, 0).toString())
            stampCountTextView.text = "1"

            plusButton.setOnClickListener {
                stampCountTextView.text = stampCountTextView.text.toString().takeIf { it.length > 0 && it.equals("-") == false }?.let {
                    (it.toLong() + 1).toString()
                }?: "1"
            }

            minusButton.setOnClickListener {
                stampCountTextView.text = stampCountTextView.text.toString().takeIf { it.length > 0 && it.equals("-") == false }?.let {
                    (it.toLong() - 1).toString()
                }?: "-1"
            }

            applyButton.setOnClickListener {
                LoadingDialog.show(this@CustomerDetailActivity)
                RetrofitClient.getInstance().postStampsAccumulate("/stamps/accumulate/${customerId}", AccumulateReq(stampCountTextView.text.toString().toInt())).enqueue(object :
                    RetrofitCallback<MemberRes>(this@CustomerDetailActivity) {
                    override fun onResponse(response: Response<MemberRes>) {
                        if(response.isSuccessful) {
                            response.body()?.let {
                                Toast.makeText(applicationContext, R.string.applied, Toast.LENGTH_SHORT).show()

                                stampTitleTextView.text = getString(R.string.accumulate_stamps, it.membershipStamp.toString())
                                stampCountTextView.text = "1"
                            }
                        } else {
                            response.errorBody().displayError(applicationContext)
                        }
                    }

                    override fun onFailure(t: Throwable) {
                        Log.e("TAG", "LJS== ex : " + t.message)
                    }
                })
            }

            couponListButton.setOnClickListener {
                val intent = Intent(this@CustomerDetailActivity, CustomerCouponListActivity::class.java)
                intent.putExtra(PHONE_NUMBER, phoneNumberTextView.text.toString())
                intent.putExtra(CUSTOMER_ID, customerId)
                startActivity(intent)
            }

            keyboard.setInputView(stampCountTextView, "")
        }

    }
}