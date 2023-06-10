package io.laonda.customerstamp.net.login

import com.google.gson.annotations.SerializedName

data class LoginRes(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    )
