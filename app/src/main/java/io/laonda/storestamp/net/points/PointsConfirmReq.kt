package io.laonda.customerstamp.net.points

import com.google.gson.annotations.SerializedName

data class PointsConfirmReq(
    @SerializedName("member_mobile") val memberMobile: String
    )
