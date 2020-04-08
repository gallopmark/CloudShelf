package com.holike.cloudshelf.netapi

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.internal.`$Gson$Types`
import com.holike.cloudshelf.util.LogCat
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
//json解析类
class JsonParserHelper {
    companion object {
        private const val FIELD_CODE = "code"  //接口返回的json是否保存code字段
        private const val FIELD_RESULT = "result" //接口返回的json中result字段
        private const val FIELD_DATA = "data" //接口返回的json中的data字段
        private const val FIELD_MSG = "msg" //json中msg字段
        const val SUCCESS_CODE = 0      //成功返回码
        const val INVALID_CODE = 210819  //登录认证失败返回码
        const val DEFAULT_CODE = -123456789
        private fun isEmpty(json: String?): Boolean {
            return json.isNullOrEmpty()
        }

        fun getSuperclassTypeParameter(subclass: Class<*>): Type {
            val superclass = subclass.genericSuperclass
            return if (superclass is ParameterizedType) {
                if (superclass.actualTypeArguments.isEmpty()) Any::class.java
                else `$Gson$Types`.canonicalize(superclass.actualTypeArguments[0])
            } else {
                Any::class.java
            }
        }

        private fun getAsJsonObject(json: String?): JsonObject {
            return try {
                JsonParser.parseString(json).asJsonObject
            } catch (e: Exception) {
                JsonObject()
            }
        }

        //判断json字符串中是否包含某个字段
        private fun has(json: String?, key: String?): Boolean {
            return if (isEmpty(json)) false else try {
                getAsJsonObject(json).has(key)
            } catch (e: Exception) {
                LogCat.e(e)
                false
            }
        }

        /*接口返回结果 code字段 int类型*/
        fun getCode(json: String?): Int {
            return if (!has(json, FIELD_CODE)) DEFAULT_CODE else try {
                getAsJsonObject(json)[FIELD_CODE].asInt
            } catch (e: Exception) {
                LogCat.e(e)
                DEFAULT_CODE
            }
        }

        /*接口返回结果 msg字段 String类型*/
        fun getMsg(json: String?): String {
            return if (!has(json, FIELD_MSG)) "" else try {
                val element = getAsJsonObject(json)[FIELD_MSG]
                if (element.isJsonNull) "" else element.asString
            } catch (e: Exception) {
                LogCat.e(e)
                ""
            }
        }

        /*接口返回结果 result字段*/
        fun getResult(json: String?): String {
            return if (!has(json, FIELD_RESULT)) "" else try {
                val element = getAsJsonObject(json)[FIELD_RESULT]
                if (element.isJsonNull) "" else element.toString()
            } catch (e: Exception) {
                LogCat.e(e)
                ""
            }
        }

        //获取json字符串里面的data字段
        fun getData(json: String?): String {
            if (!has(json, FIELD_DATA)) return ""
            return try {
                val element = getAsJsonObject(json)[FIELD_DATA]
                if (element.isJsonNull) "" else element.toString()
            } catch (e: Exception) {
                LogCat.e(e)
                ""
            }
        }

        /**
         * 解析json里面的result字段或data字段，如果两者都没有，则返回null代表无数据
         */
        fun <T> parseHttpJson(json: String?, type: Type): T? {
            if (json.isNullOrEmpty()) return null
            //首先获取result字段
            val result = getResult(json)
            if (result.isNotEmpty()) {
                //result字段不为空
                return try {
                    Gson().fromJson<T>(result, type)
                } catch (e: Exception) {
                    null
                }
            } else {
                //否则获取data字段
                val data = getData(json)
                return if (data.isNotEmpty()) {
                    try {
                        Gson().fromJson<T>(data, type)
                    } catch (e: Exception) {
                        null
                    }
                } else {
                    //两者都没有返回null
                    null
                }
            }
        }

        //object转json字符串
        fun toJson(obj: Any): String {
            return try {
                Gson().toJson(obj)
            } catch (e: Exception) {
                ""
            }
        }
    }
}