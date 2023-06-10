package io.laonda.storestamp.net

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import io.laonda.customerstamp.net.login.AuthRes
import io.laonda.storestamp.ui.activity.LoginActivity
import io.laonda.storestamp.utils.LoadingDialog
import io.laonda.storestamp.utils.displayError
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


abstract class RetrofitCallback<T>(val context: Context) : Callback<T> {
    abstract fun onResponse(response: Response<T>)
    abstract fun onFailure(t: Throwable)

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.raw().code() == 401) {
            if (response.raw().request().url().toString().contains("/auth/refresh") == true) {
                LoadingDialog.hide()
                // 로그인 화면으로 이동
                val loginIntent = Intent(context, LoginActivity::class.java)
                loginIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(loginIntent)
            } else {
                // refresh 호출
                RetrofitClient.getInstance().getAuth().enqueue(object: RetrofitCallback<AuthRes>(context) {
                    override fun onResponse(response: Response<AuthRes>) {
                        if(response.isSuccessful) {
                            response.body()?.let {
                                RetrofitClient.accessToken = it.accessToken
                                RetrofitClient.refreshToken = it.refreshToken

                                retry(call)
                            }
                        } else {
                            LoadingDialog.hide()
                            response.errorBody().displayError(context)
                        }
                    }

                    override fun onFailure(t: Throwable) {
                        Log.e("TAG", "LJS== ex : " + t.message)
                    }
                })
            }
        } else {
            LoadingDialog.hide()
            onResponse(response)
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        LoadingDialog.hide()
        onFailure(t)
    }

    private fun retry(call: Call<T>) {
        call.clone().enqueue(this)
    }
}