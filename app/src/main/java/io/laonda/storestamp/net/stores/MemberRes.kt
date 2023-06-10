package io.laonda.customerstamp.net.points

import com.google.gson.annotations.SerializedName
import io.laonda.storestamp.net.User

data class MemberRes(
    @SerializedName("id") val id: Int,
    @SerializedName("user") val user: User,
    @SerializedName("membership_point") val membershipPoint: Int,
    @SerializedName("membership_stamp") val membershipStamp: Int,
    @SerializedName("memo") val memo: String,
    @SerializedName("upated_at") val updatedAt: String,
    @SerializedName("coupon_count") val couponCount: Int,
)
