package io.laonda.storestamp.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import io.laonda.customerstamp.net.points.CouponRes
import io.laonda.customerstamp.net.points.CreateCouponReq
import io.laonda.customerstamp.net.points.MemberRes
import io.laonda.storestamp.R
import io.laonda.storestamp.databinding.CouponListItemLayoutBinding
import io.laonda.storestamp.databinding.FragmentCouponBinding
import io.laonda.storestamp.net.RetrofitCallback
import io.laonda.storestamp.net.RetrofitClient
import io.laonda.storestamp.ui.widget.CustomDecoration
import io.laonda.storestamp.utils.LoadingDialog
import io.laonda.storestamp.utils.displayError
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 * Use the [CouponFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CouponFragment : Fragment() {
    private var _binding: FragmentCouponBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val smsContent: MutableLiveData<String> = MutableLiveData<String>().apply { "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCouponBinding.inflate(inflater, container, false)
        val view = binding.root

        getCoupons()

        with(binding) {
            createCouponButton.setOnClickListener {
                couponContentEditText.text.toString().takeIf { it.length > 0 }?.let {
                    RetrofitClient.getInstance().postStoreCoupons(CreateCouponReq(it)).enqueue(object :
                        RetrofitCallback<Void>(requireContext()) {
                        override fun onResponse(response: Response<Void>) {
                            if(response.isSuccessful) {
                                Toast.makeText(requireContext(), R.string.create_coupon, Toast.LENGTH_SHORT).show()
                                getCoupons()
                            } else {
                                response.errorBody().displayError(requireContext())
                            }
                        }

                        override fun onFailure(t: Throwable) {
                            Log.e("TAG", "LJS== ex : " + t.message)
                        }
                    })

                } ?: Toast.makeText(requireContext(), R.string.empty_content, Toast.LENGTH_SHORT).show()
            }

            smsContent.observe(this@CouponFragment as LifecycleOwner, Observer { content ->
                couponContentEditText.setText(content)
            })
        }

        return view
    }

    fun getCoupons() {
        LoadingDialog.show(requireContext())
        RetrofitClient.getInstance().getStoreCoupons().enqueue(object : RetrofitCallback<List<CouponRes>>(requireContext()) {
            override fun onResponse(response: Response<List<CouponRes>>) {
                if(response.isSuccessful) {
                    response.body()?.let { couponRes ->
                        if (couponRes.size > 0) {
                            smsContent.postValue(couponRes[0].couponContent)
                        }

                        binding.registeredCouponList.apply {
                            adapter = CouponAdapter(couponRes.toMutableList())
                            addItemDecoration(CustomDecoration(18f, 0f, Color.TRANSPARENT))
                        }
                    }
                } else {
                    response.errorBody().displayError(requireContext())
                }
            }

            override fun onFailure(t: Throwable) {
                Log.e("TAG", "LJS== ex : " + t.message)
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CouponFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = CouponFragment()
    }

    inner class CouponAdapter(private val items: MutableList<CouponRes>) : RecyclerView.Adapter<CouponAdapter.CouponViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
            return CouponViewHolder(CouponListItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size

        inner class CouponViewHolder(val binding: CouponListItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(couponRes: CouponRes) {
                with(binding) {
                    nameTextView.text = couponRes.couponContent
                    applyButton.setOnClickListener {
                        smsContent.postValue(couponRes.couponContent)
                    }
                }
            }
        }
    }
}