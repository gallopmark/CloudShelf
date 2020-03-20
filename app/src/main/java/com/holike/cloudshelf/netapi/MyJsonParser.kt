package com.holike.cloudshelf.netapi

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.internal.`$Gson$Types`
import com.holike.cloudshelf.util.LogCat
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class MyJsonParser {
    companion object {
        private const val FIELD_CODE = "code"  //接口返回的json是否保存code字段
        private const val FIELD_RESULT = "result" //接口返回的json中result字段
        private const val FIELD_MSG = "msg" //json中msg字段
        const val SUCCESS_CODE = 0
        const val INVALID_CODE = 210819
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

        private fun has(json: String?, key: String?): Boolean {
            return if (isEmpty(json)) false else try {
                getAsJsonObject(json).has(key)
            } catch (e: Exception) {
                LogCat.e(e)
                false
            }
        }

        private fun hasCode(json: String?): Boolean {
            return has(json, FIELD_CODE)
        }

        /*接口返回结果 code字段 int类型*/
        fun getCode(json: String?): Int {
            return if (!hasCode(json)) DEFAULT_CODE else try {
                getAsJsonObject(json)[FIELD_CODE].asInt
            } catch (e: Exception) {
                LogCat.e(e)
                DEFAULT_CODE
            }
        }

        private fun hasMsg(json: String?): Boolean {
            return has(json, FIELD_MSG)
        }

        private fun getMsgElement(json: String?): JsonElement {
            return getAsJsonObject(json)[FIELD_MSG]
        }

        /*接口返回结果 msg字段 String类型*/
        fun getMsg(json: String?): String {
            return if (!hasMsg(json)) "" else try {
                val element = getMsgElement(json)
                if (element.isJsonNull) "" else element.asString
            } catch (e: Exception) {
                LogCat.e(e)
                ""
            }
        }

        private fun hasResult(json: String?): Boolean {
            return has(json, FIELD_RESULT)
        }

        private fun getResultElement(json: String?): JsonElement {
            return getAsJsonObject(json)[FIELD_RESULT]
        }

        /*接口返回结果 result字段*/
        fun getResultAsString(json: String?): String {
            return if (!hasResult(json)) "" else try {
                val element = getResultElement(json)
                if (element.isJsonNull) "" else element.asString
            } catch (e: Exception) {
                LogCat.e(e)
                ""
            }
        }

        /*接口返回结果 result字段*/
        fun getResult(json: String?): String {
            return if (!hasResult(json)) "" else try {
                val element = getResultElement(json)
                if (element.isJsonNull) "" else element.toString()
            } catch (e: Exception) {
                LogCat.e(e)
                ""
            }
        }

        fun <T> parseResult(json: String?, type: Type): T? {
            if (json.isNullOrEmpty()) return null
            val result = getResult(json)
            if (result.isEmpty()) return null
            return try {
                Gson().fromJson<T>(result, type)
            } catch (e: Exception) {
                null
            }
        }
    }
}