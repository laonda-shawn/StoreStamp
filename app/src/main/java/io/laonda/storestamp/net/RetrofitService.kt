package io.laonda.storestamp.net

import io.laonda.customerstamp.net.login.AuthRes
import io.laonda.customerstamp.net.login.LoginReq
import io.laonda.customerstamp.net.login.LoginRes
import io.laonda.customerstamp.net.points.*
import io.laonda.customerstamp.net.stamps.StampsRequestReq
import io.laonda.storestamp.net.stamps.AccumulateReq
import retrofit2.Call
import retrofit2.http.*

interface CsRetrofitService {

    @POST("/auth/login")
    fun postLogin(@Body login: LoginReq) : Call<LoginRes>

    @POST("/points/confirm")
    fun postPointsConfirm(@Body confirm: PointsConfirmReq) : Call<PointsConfirmRes>

    @POST("/stamps/request")
    fun postStampsRequest(@Body confirm: StampsRequestReq) : Call<Void>

    @POST("/store-coupons")
    fun postStoreCoupons(@Body createCoupon: CreateCouponReq) : Call<Void>

    @POST
    fun postStampsAccumulate(@Url url: String, @Body accumulateReq: AccumulateReq) : Call<MemberRes>

    @GET("/auth/refresh")
    fun getAuth() : Call<AuthRes>

    @GET("/stores/members")
    fun getStoresMembers() : Call<List<MemberRes>>

    @GET("/store-coupons")
    fun getStoreCoupons() : Call<List<CouponRes>>

    @GET
    fun getCouponsMembers(@Url url: String) : Call<List<MemberCoupon>>

    @DELETE("/stamps/request")
    fun deleteStampRequest() : Call<Void>

    @PUT
    fun putCoupons(@Url url: String) : Call<Void>
}