package com.holike.cloudshelf.bean

/*高德地图 ip定位数据*/
class AMapLocationBean {

    var status: String? = null //返回结果状态值  值为0或1,0表示失败；1表示成功
    var info: String? = null  // 返回状态说明，status为0时，info返回错误原因，否则返回“OK”。
    var infocode: String? = null //返回状态说明,10000代表正确,详情参阅info状态表
    var province: String? = null //省份名称
    var city: String? = null //城市名称
    var adcode: String? = null //城市的adcode编码
}