package com.holike.cloudshelf.util

//list差分
class ListUtils {
    companion object {
        fun <T> averageAssignFixLength(source: List<T>?, splitItemNum: Int): List<List<T>> {
            val result = ArrayList<List<T>>()
            source?.let {
                if(it.isNotEmpty() && splitItemNum > 0){
                    if (it.size <= splitItemNum) {
                        // 源List元素数量小于等于目标分组数量
                        result.add(it)
                    } else {
                        // 计算拆分后list数量
                        val splitNum = if (it.size % splitItemNum == 0) it.size / splitItemNum else it.size / splitItemNum + 1
                        var value: List<T>
                        for (i in 0 until splitNum) {
                            value = if (i < splitNum - 1) {
                                it.subList(i * splitItemNum, (i + 1) * splitItemNum)
                            } else {
                                // 最后一组
                                it.subList(i * splitItemNum, it.size)
                            }
                            result.add(value)
                        }
                    }
                }
            }
            return result
        }
    }
}