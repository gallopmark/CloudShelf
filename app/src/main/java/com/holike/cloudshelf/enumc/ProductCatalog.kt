package com.holike.cloudshelf.enumc


class ProductCatalog private constructor(){
    companion object{
        const val WHOLE_HOUSE = "WHOLE_HOUSE"  //全屋定制
        const val AMBRY = "AMBRY" //橱柜定制
        //橱柜定制 选择的类型  选择了橱柜，字典跟着变为“造型”和“系列”
        const val AMBRY_CUSTOM_MADE = "AMBRY_CUSTOM_MADE" //橱柜
        //选择了“电器” 字典跟着变为“品牌”和“功能”
        const val AMBRY_APPLIANCES = "AMBRY_APPLIANCES" //电器
        //--
        const val DOOR = "DOOR"  //木门定制
        const val HOME_PRO = "HOME_PRO" //家居家品
        const val HOME_PRO_FURNISHED = "HOME_PRO_FURNISHED" //家居家品-成品家具
        const val HOME_PRO_CURTAIN = "HOME_PRO_CURTAIN" //家居家品-定制窗帘

        /*************************************************************/
        const val DICT_SPACE_HOUSE = "space_house"  //全屋定制-空间
        const val DICT_SERIES_HOUSE = "series_house" //全屋定制-系列
        const val DICT_CUPBOARD_TYPE = "ambry_type" //橱柜定制-类型
        const val DICT_CUPBOARD_MODEL = "model_ambry" //橱柜定制-造型
        const val DICT_CUPBOARD_SERIES = "series_ambry" //橱柜定制-系列
        const val DICT_BRAND_APPLIANCE = "brand_appliance" //橱柜电器-品牌
        const val DICT_FUNCTION_APPLIANCE   ="function_appliance"  //(橱柜电器) -功能
        const val DICT_DOOR_CATEGORY = "category_door" //木门定制-品类
        const val DICT_DOOR_SERIES = "series_door" //木门定制-系列
        const val DICT_HOME_PRO_FURNISHED = "HOME_PRO_FURNISHED" //家居家品-成品家具
        const val DICT_HOME_PRO_CURTAIN = "HOME_PRO_CURTAIN" //家居家品-定制窗帘
    }
}