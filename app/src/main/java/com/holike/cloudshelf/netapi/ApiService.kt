package com.holike.cloudshelf.netapi

import io.reactivex.Observable
import retrofit2.http.*


interface ApiService {
    /*获取版本信息*/
    @GET("/cloud/portal/version/getVersionUpdateInfo")
    fun getVersionInfo(): Observable<String?>

    /*首页获取广告位*/
    @GET("/cloud/portal/content")
    fun getAdvertising(): Observable<String?>

    /*登陆短信验证码*/
    @GET("/cloud/portal/sendSmsCaptcha")
    fun getVerificationCode(@Query("phone") phone: String): Observable<String?>

    /*登录接口*/
    @FormUrlEncoded
    @POST("/cloud/portal/login")
    fun doLogin(@FieldMap fieldMap: HashMap<String, String>): Observable<String?>

    /*登出*/
    @GET("/cloud/portal/logout")
    fun doLogout(@Query("cliId") cliId: String, @Query("token") token: String): Observable<String?>

    /*产品大全模块列表查询*/
    @POST("/cloud/productDaquan/getPlanContentList")
    fun getPlanContentList(): Observable<String?>

    /*高德地图web服务 ip定位*/
    @GET
    fun getLocationInfo(@Url url: String): Observable<String?>

    /*3D万套案例*/
    @FormUrlEncoded
    @POST("/program/getTableModelHouse")
    fun getTableModeHouse(@FieldMap fieldMap: HashMap<String, String>): Observable<String?>

    /*案例详情*/
    @FormUrlEncoded
    @POST("/program/getTableModel")
    fun getTableModel(@Field("id") id: String?, @Field("openid") phone: String?): Observable<String?>

    /*产品大全各模块标签查询*/
    @FormUrlEncoded
    @POST("/cloud/productDaquan/product/getSpecList")
    fun getProductSpecList(@Field("templateId") templateId: String?): Observable<String?>

    /*产品大全各模块的方案列表(分页)*/
    @FormUrlEncoded
    @POST("/cloud/productDaquan/product/searchBySpecOption")
    fun getProductOptionList(@Field("pageNo") pageNo: String, @Field("pageSize") pageSize: String,
                             @Field("templateId") templateId: String?, @Field("specificationOptionIds") optionIds: String?): Observable<String?>

    /*晒晒好晒图家列表查询(分页)*/
    @FormUrlEncoded
    @POST("/cloud/blueprintShow/getBlueprintList")
    fun getBlueprintList(@Field("pageNo") pageNo: String, @Field("pageSize") pageSize: String): Observable<String?>

    /*晒晒好家的晒图详情查询*/
    @FormUrlEncoded
    @POST("/cloud/blueprintShow/detail/getBlueprintInfo")
    fun getBlueprintInfo(@Field("blueprintId") blueprintId: String?): Observable<String?>

    /*搜搜我家小区列表(前提定位获取城市名)*/
    @FormUrlEncoded
    @POST("/cloud/searchNearby/searchCommunityList")
    fun getCommunityList(@Field("currentCity") currentCity: String?, @Field("searchName") searchName: String?,
                         @Field("pageNo") pageNo: String, @Field("pageSize") pageSize: String): Observable<String?>

    /*搜搜我家户型列表(分页)*/
    @FormUrlEncoded
    @POST("/cloud/searchNearby/getHouseTypeByCommunityId")
    fun getHouseTypeListById(@Field("pageNo") pageNo: String, @Field("pageSize") pageSize: String, @Field("communityId") communityId: String?): Observable<String?>

    /*搜搜我家户型详情查询*/
    @FormUrlEncoded
    @POST("/cloud/searchNearby/houseType/detail/getHouseTypeInfo")
    fun getHouseTypeInfoById(@Field("houseTypeId") houseTypeId: String?): Observable<String?>
}