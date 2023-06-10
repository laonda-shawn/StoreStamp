package io.laonda.storestamp.net

import com.google.gson.annotations.SerializedName


data class User(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("user_name") val userName: String,
    @SerializedName("mobile") val mobile: String,
    @SerializedName("email") val email: String,
    @SerializedName("birthday") val birthday: String,
    @SerializedName("created_at") val createdAt: String
    )
