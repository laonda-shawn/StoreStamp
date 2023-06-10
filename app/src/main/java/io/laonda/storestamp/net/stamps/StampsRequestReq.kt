package io.laonda.customerstamp.net.stamps

import com.google.gson.annotations.SerializedName

data class StampsRequestReq(
    @SerializedName("membership_stamp") val membershipStamp: Int
    )
