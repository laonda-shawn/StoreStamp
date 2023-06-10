package io.laonda.storestamp.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import io.laonda.customerstamp.net.points.MemberCoupon
import io.laonda.storestamp.R
import io.laonda.storestamp.databinding.ActivityCustomerCouponListBinding
import io.laonda.storestamp.databinding.CustomCouponListItemLayoutBinding
import io.laonda.storestamp.net.RetrofitCallback
import io.laonda.storestamp.net.RetrofitClient
import io.laonda.storestamp.ui.widget.CustomDecoration
import io.laonda.storestamp.utils.LoadingDialog
import io.laonda.storestamp.utils.displayError
import io.laonda.storestamp.utils.toDate
import retrofit2.Response
import java.io.PrintWriter
import java.io.StringWriter

class CustomerCouponListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCustomerCouponListBinding

    companion object {
        val CUSTOMER_ID = "CUSTOMER_ID"
        val PHONE_NUMBER = "PHONE_NUMBER"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerCouponListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        window.statusBarColor = getColor(R.color.main_color)
        window.navigationBarColor = getColor(R.color.main_color)

        binding.phoneNumberTextView.apply {
            text = intent.getStringExtra(PHONE_NUMBER)
            setOnClickListener {
                finish()
            }
        }

        getCoupons()

//        BuildConfig.DEBUG.takeIf { it == true }?.let {
            Thread.setDefaultUncaughtExceptionHandler(object : Thread.UncaughtExceptionHandler {
                override fun uncaughtException(thread: Thread, ex: Throwable) {
                    val sw = StringWriter()
                    val pw = PrintWriter(sw)
                    ex.printStackTrace(pw)

                    val intent = Intent(this@CustomerCouponListActivity, CrashLogActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra(CrashLogActivity.CRASH_LOG, "\n" + sw.toString())
                    startActivity(intent)

                    System.exit(10)
                }
            })
//        }
    }

    fun getCoupons() {
        LoadingDialog.show(this@CustomerCouponListActivity)
        RetrofitClient.getInstance()
            .getCouponsMembers("/coupons/members/${intent.getIntExtra(CUSTOMER_ID, -1)}")
            .enqueue(object : RetrofitCallback<List<MemberCoupon>>(this@CustomerCouponListActivity) {
                override fun onResponse(response: Response<List<MemberCoupon>>) {
                    if(response.isSuccessful) {
                        response.body()?.let {
                            if(it.size > 0) {
                                binding.titleTextView.text = getString(R.string.accumulate_coupon, it.size.toString())
                                binding.emptyTextView.isVisible = false
                                binding.coupontList.apply {
                                    adapter = CouponAdapter(it.toMutableList())
                                    addItemDecoration(CustomDecoration(18f, 0f, Color.TRANSPARENT))
                                }
                            } else {
                                binding.emptyTextView.isVisible = true
                            }
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

    inner class CouponAdapter(private val items: MutableList<MemberCoupon>) : RecyclerView.Adapter<CouponAdapter.CouponViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
            return CouponViewHolder(CustomCouponListItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size

        inner class CouponViewHolder(val binding: CustomCouponListItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(memberCoupon: MemberCoupon) {
                with(binding) {
                    nameTextView.text = memberCoupon.couponContent
                    if (memberCoupon.useDate.isNullOrEmpty() == false) {
                        useButton.isEnabled = false
                        useButton.text = getString(R.string.used)
                        usedDateTextView.text = memberCoupon.useDate?.toDate(applicationContext) ?: ""
                    } else {
                        useButton.isEnabled = true
                        useButton.text = getString(R.string.use)
                        useButton.setOnClickListener {
                            RetrofitClient.getInstance().putCoupons("/coupons/${memberCoupon.id}").enqueue(object : RetrofitCallback<Void>(this@CustomerCouponListActivity) {
                                override fun onResponse(response: Response<Void>) {
                                    if(response.isSuccessful) {
                                        Toast.makeText(applicationContext, R.string.used, Toast.LENGTH_SHORT).show()
                                        getCoupons()
                                    } else {
                                        response.errorBody().displayError(applicationContext)
                                    }
                                }

                                override fun onFailure(t: Throwable) {
                                    Log.e("TAG", "LJS== ex : " + t.message)
                                }
                            })
                        }
                    }
                }
            }
        }
    }
}