package io.laonda.customerstamp.net.points

import com.google.gson.annotations.SerializedName
import io.laonda.storestamp.net.User

data class MemberCoupon(
    @SerializedName("id") val id: Int,
    @SerializedName("uuid") val uuid: String,
    @SerializedName("coupon_content") val couponContent: String,
    @SerializedName("create_date") val createDate: String,
    @SerializedName("use_date") val useDate: String,
    @SerializedName("expire_date") val expireDate: String,
)
