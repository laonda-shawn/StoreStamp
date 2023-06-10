package io.laonda.storestamp.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import io.laonda.customerstamp.net.stamps.StampsRequestReq
import io.laonda.storestamp.R
import io.laonda.storestamp.databinding.FragmentStampBinding
import io.laonda.storestamp.net.RetrofitCallback
import io.laonda.storestamp.net.RetrofitClient
import io.laonda.storestamp.utils.LoadingDialog
import io.laonda.storestamp.utils.displayError
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 * Use the [StampFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StampFragment : Fragment() {
    private var _binding: FragmentStampBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStampBinding.inflate(inflater, container, false)
        val view = binding.root

        with (binding) {
            plusButton.setOnClickListener {
                val stampCount = stampCountTextView.text.toString().let {
                    if (it.trim().equals("") == false) {
                        (it.toInt() + 1).toString()
                    } else {
                        "1"
                    }
                }

                stampCountTextView.setText(stampCount)
            }

            minusButton.setOnClickListener {
                val stampCount = stampCountTextView.text.toString().let {
                    if (it.trim().equals("") == false && it.toInt() > 1) {
                        (it.toInt() - 1).toString()
                    } else {
                        "1"
                    }
                }

                stampCountTextView.setText(stampCount)
            }

            keyboard.setInputView(stampCountTextView, "")

            skipButton.setOnClickListener {
                RetrofitClient.getInstance().deleteStampRequest().enqueue(object :
                    RetrofitCallback<Void>(requireContext()) {
                    override fun onResponse(response: Response<Void>) {
                        if(response.isSuccessful) {
                            Toast.makeText(requireContext(), R.string.skip, Toast.LENGTH_SHORT).show()
                        } else {
                            response.errorBody().displayError(requireContext())
                        }
                    }

                    override fun onFailure(t: Throwable) { }

                })
                resetCount()
            }

            applyButton.setOnClickListener {
                stampCountTextView.text.toString().takeIf { it.length > 0 }?.let { stampCount ->
                    LoadingDialog.show(requireContext())
                    RetrofitClient.getInstance().postStampsRequest(StampsRequestReq(stampCount.toInt())).enqueue(object:
                        RetrofitCallback<Void>(requireContext()) {
                        override fun onResponse(response: Response<Void>) {
                            if(response.isSuccessful) {
                                Toast.makeText(requireContext(), R.string.apply, Toast.LENGTH_SHORT).show()
                            } else {
                                response.errorBody().displayError(requireContext())
                            }
                        }

                        override fun onFailure(t: Throwable) {
                            Log.e("TAG", "LJS== ex : " + t.message)
                        }
                    })
                }

                resetCount()
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun resetCount() {
        binding.stampCountTextView.text = "1"
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AdminFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = StampFragment()
    }
}