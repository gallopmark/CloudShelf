package com.holike.cloudshelf.bean

import com.google.gson.annotations.SerializedName

/*产品大全标签数据*/
class ProductSpecBean {
    @SerializedName("templateId")
    var templateId: String? = null

    @SerializedName("specList")
    var specList: MutableList<SpecBean>? = null

    fun obtainSpecList(): MutableList<SpecBean> {
        val list = specList
        if (list.isNullOrEmpty()) return ArrayList()
        return list
    }

    class SpecBean {
        @SerializedName("id")
        var id: String? = null

        @SerializedName("spec_name")
        var specName: String? = null

        @SerializedName("options")
        var optionsList: MutableList<OptionBean>? = null

        fun obtainOptionsList(): MutableList<OptionBean> {
            val list = optionsList
            if (list.isNullOrEmpty()) return ArrayList()
            return list
        }

        class OptionBean {
            @SerializedName("id")
            var id: String? = null   //规格选项id

            @SerializedName("optionName")
            var optionName: String? = null //optionName

            @SerializedName("specId")
            var specId: String? = null //对应的规格id

            @SerializedName("parentId")
            var parentId: String? = null //该规格选项对应的父类id
        }
    }
}