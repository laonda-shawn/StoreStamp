package io.laonda.customerstamp.net.points

import com.google.gson.annotations.SerializedName

data class CreateCouponReq(
    @SerializedName("coupon_content") val couponContent: String
)
