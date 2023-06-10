package io.laonda.storestamp.ui.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import io.laonda.customerstamp.net.points.MemberRes
import io.laonda.storestamp.R
import io.laonda.storestamp.databinding.CustomerListItemLayoutBinding
import io.laonda.storestamp.databinding.FragmentCustomerBinding
import io.laonda.storestamp.net.RetrofitCallback
import io.laonda.storestamp.net.RetrofitClient
import io.laonda.storestamp.ui.activity.CustomerDetailActivity
import io.laonda.storestamp.ui.activity.CustomerDetailActivity.Companion.ID
import io.laonda.storestamp.ui.activity.CustomerDetailActivity.Companion.MOBILE
import io.laonda.storestamp.ui.activity.CustomerDetailActivity.Companion.STAMP_COUNT
import io.laonda.storestamp.ui.widget.CustomDecoration
import io.laonda.storestamp.utils.LoadingDialog
import io.laonda.storestamp.utils.displayError
import retrofit2.Callback
import retrofit2.Response


/**
 * A simple [Fragment] subclass.
 * Use the [CustomerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CustomerFragment : Fragment() {
    private var _binding: FragmentCustomerBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val membserItems = mutableListOf<MemberRes>()
    private lateinit var startForResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomerBinding.inflate(inflater, container, false)
        val view = binding.root

        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            getCustomerInfo()
        }

        getCustomerInfo()

        with(binding) {
            searchImageView.setOnClickListener {
                inputPhoneNumberTextView.text.toString().takeIf { it.length > 0 }?.let { inputNumber ->
                    val tempItems = membserItems.takeIf { it.size > 0 }?.filter { it.user.mobile.contains(inputNumber) } ?: membserItems

                    searchResultTextView.text = getString(R.string.serarch_result, tempItems.size.toString())

                    list.apply {
                        adapter = MemberAdapter(tempItems.toMutableList())
                        addItemDecoration(CustomDecoration(23f, 0f, Color.TRANSPARENT))
                    }
                } ?: run {
                    searchResultTextView.text = getString(R.string.serarch_result, membserItems.size.toString())
                    list.apply {
                        adapter = MemberAdapter(membserItems)
                        addItemDecoration(CustomDecoration(23f, 0f, Color.TRANSPARENT))
                    }
                }
            }

            keyboard.setInputView(inputPhoneNumberTextView, "")
        }

        return view
    }

    fun getCustomerInfo() {
        LoadingDialog.show(requireContext())
        RetrofitClient.getInstance().getStoresMembers().enqueue(object :
            RetrofitCallback<List<MemberRes>>(requireContext()) {
            override fun onResponse(response: Response<List<MemberRes>>) {
                if(response.isSuccessful) {
                    response.body()?.let { members ->
                        membserItems.clear()
                        membserItems.addAll(members.toMutableList())

                        binding.searchResultTextView.text = getString(R.string.serarch_result, membserItems.size.toString())
                        binding.list.apply {
                            adapter = MemberAdapter(members.toMutableList())
                            addItemDecoration(CustomDecoration(23f, 0f, Color.TRANSPARENT))
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
         * @return A new instance of fragment CustomerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = CustomerFragment()
    }

    inner class MemberAdapter(private val items: MutableList<MemberRes>) : RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
            return MemberViewHolder(CustomerListItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size

        inner class MemberViewHolder(val binding: CustomerListItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(memberRes: MemberRes) {
                with(binding) {
                    nameTextView.text = memberRes.user.userName
                    mobileNumberTextView.text = memberRes.user.mobile
                    birthdayTextView.text = memberRes.user.birthday

                    itemView.setOnClickListener {
                        val intent = Intent(it.context, CustomerDetailActivity::class.java)
                        intent.putExtra(ID, memberRes.id)
                        intent.putExtra(MOBILE, memberRes.user.mobile)
                        intent.putExtra(STAMP_COUNT, memberRes.membershipStamp)
                        startForResult.launch(intent)
                    }
                }
            }
        }
    }
}