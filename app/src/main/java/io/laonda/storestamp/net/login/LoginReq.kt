package io.laonda.customerstamp.net.login

import com.google.gson.annotations.SerializedName

data class LoginReq(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String)
