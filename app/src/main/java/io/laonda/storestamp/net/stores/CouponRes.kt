package io.laonda.customerstamp.net.points

import com.google.gson.annotations.SerializedName

data class CouponRes(
    @SerializedName("id") val id: Int,
    @SerializedName("coupon_content") val couponContent: String,
    @SerializedName("create_date") val createDate: String,
    @SerializedName("validity_days") val validityDays: Int,
)
