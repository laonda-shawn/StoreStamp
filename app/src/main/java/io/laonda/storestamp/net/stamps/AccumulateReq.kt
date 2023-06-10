package io.laonda.storestamp.net.stamps

import com.google.gson.annotations.SerializedName
import io.laonda.storestamp.net.User

data class AccumulateReq(
    @SerializedName("membership_stamp") val membershipStamp: Int,
)
