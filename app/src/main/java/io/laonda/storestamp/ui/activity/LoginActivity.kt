package io.laonda.storestamp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.laonda.customerstamp.net.login.LoginReq
import io.laonda.customerstamp.net.login.LoginRes
import io.laonda.storestamp.R
import io.laonda.storestamp.databinding.ActivityLoginBinding
import io.laonda.storestamp.net.RetrofitCallback
import io.laonda.storestamp.net.RetrofitClient
import io.laonda.storestamp.utils.displayError
import retrofit2.Response
import java.io.PrintWriter
import java.io.StringWriter

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        window.statusBarColor = getColor(R.color.main_color)
        window.navigationBarColor = getColor(R.color.main_color)

//        BuildConfig.DEBUG.takeIf { it == true }?.let {
            Thread.setDefaultUncaughtExceptionHandler(object : Thread.UncaughtExceptionHandler {
                override fun uncaughtException(thread: Thread, ex: Throwable) {
                    val sw = StringWriter()
                    val pw = PrintWriter(sw)
                    ex.printStackTrace(pw)

                    val intent = Intent(this@LoginActivity, CrashLogActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra(CrashLogActivity.CRASH_LOG, "\n" + sw.toString())
                    startActivity(intent)

                    System.exit(10)
                }
            })
//        }

        with (binding) {
            signInButton.setOnClickListener {
                val id = idEditText.text.toString()
                val pw = pwEditText.text.toString()
                // Test [
//                val id = "aaaadddd@gmail.com"
//                val pw = "123456"
                // ]

                if (id.isEmpty() == true) {
                    Toast.makeText(this@LoginActivity, R.string.enter_id, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (pw.isEmpty() == true) {
                    Toast.makeText(this@LoginActivity, R.string.enter_pw, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                RetrofitClient.getInstance().postLogin(LoginReq(id, pw)).enqueue(object: RetrofitCallback<LoginRes>(this@LoginActivity) {
                    override fun onResponse(response: Response<LoginRes>) {
                        if(response.isSuccessful) {
                            response.body()?.let {
                                RetrofitClient.accessToken = it.accessToken
                                RetrofitClient.refreshToken = it.refreshToken

                                Log.e("TAG", "LJS== it.accessToken : " + it.accessToken)
                                Log.e("TAG", "LJS== it.refreshToken : " + it.refreshToken)
                            }

                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
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