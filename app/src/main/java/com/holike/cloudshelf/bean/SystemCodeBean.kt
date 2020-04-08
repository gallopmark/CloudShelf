package com.holike.cloudshelf.bean

import com.google.gson.annotations.SerializedName

//业务字典数据
class SystemCodeBean {
    @SerializedName("space_house")
    private var spaceHouseMap: HashMap<String, String>? = null  //全屋定制-空间

    @SerializedName("series_house")
    private var seriesHouseMap: HashMap<String, String>? = null  //全屋定制-系列

    @SerializedName("category_door")
    private var categoryDoorMap: HashMap<String, String>? = null //木门定制-品类

    @SerializedName("series_door")
    private var seriesDoorMap: HashMap<String, String>? = null //木门定制-系列

    @SerializedName("ambry_type")
    private var amBryMap: HashMap<String, String>? = null  //橱柜定制-类型

    @SerializedName("model_ambry")
    private var modelAmBryMap: HashMap<String, String>? = null  //橱柜定制-造型

    @SerializedName("series_ambry")
    private var seriesAmBryMap: HashMap<String, String>? = null     //橱柜定制-系列

    @SerializedName("brand_appliance")
    private var brandApplianceMap: HashMap<String, String>? = null //橱柜电器-品牌

    @SerializedName("function_appliance")
    private var functionApplianceMap: HashMap<String, String>? = null   //橱柜电器-功能

    @SerializedName("home_pro_cla")
    private var homeProClaMap: HashMap<String, String>? = null //家品分类(家居家品)

    @SerializedName("series_Furnished")
    private var seriesFurnishedMap: HashMap<String, String>? = null //系列(成品家具)

    @SerializedName("style_curtain")
    private var styleCurtainMap: HashMap<String, String>? = null //风格（定制窗帘）

    @SerializedName("show_navigation")
    private var showNavigationMap: HashMap<String, String>? = null

    fun getSpaceHouseCode() = nonNullWrap(spaceHouseMap)

    fun getSeriesHouseCode() = nonNullWrap(seriesHouseMap)

    fun getCategoryDoorCode() = nonNullWrap(categoryDoorMap)

    fun getSeriesDoorCode() = nonNullWrap(seriesDoorMap)

    fun getAmBryCode() = nonNullWrap(amBryMap)

    fun getModelAmBryCode() = nonNullWrap(modelAmBryMap)

    fun getSeriesAmBryCode() = nonNullWrap(seriesAmBryMap)

    fun getBrandApplianceCode() = nonNullWrap(brandApplianceMap)

    fun getFunctionApplianceCode() = nonNullWrap(functionApplianceMap)

    fun getHomeProClaCode() = nonNullWrap(homeProClaMap)

    fun getSeriesFurnishedCode() = nonNullWrap(seriesFurnishedMap)

    fun getStyleCurtainCode() = nonNullWrap(styleCurtainMap)

    fun getShowNavigationCode() = nonNullWrap(showNavigationMap)

    private fun nonNullWrap(source: HashMap<String, String>?): HashMap<String, String> {
        if (source == null) return HashMap()
        return source
    }
}