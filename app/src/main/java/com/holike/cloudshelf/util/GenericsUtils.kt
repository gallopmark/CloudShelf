package com.holike.cloudshelf.util

import java.lang.reflect.ParameterizedType

/*获取Class类型工具*/
class GenericsUtils private constructor() {
    companion object {
        /**
         * 通过反射,获得定义Class时声明的父类的范型参数的类型.
         *
         * @return the first generic declaration, or `Object.class` if cannot be determined
         */
        fun getGenericsSuperclassType(clazz: Class<*>): Class<*> {
            return getGenericsSuperclassType(clazz, 0)
        }

        /**
         * 通过反射,获得定义Class时声明的父类的范型参数的类型.
         *
         * @param clazz clazz The class to introspect
         * @param index the Index of the generic ddeclaration,start makeText 0.
         */
        fun getGenericsSuperclassType(clazz: Class<*>, index: Int): Class<*> {
            val genType = clazz.genericSuperclass as? ParameterizedType ?: return Any::class.java
            val params = genType.actualTypeArguments
            if (index >= params.size || index < 0) {
                return Any::class.java
            }
            return if (params[index] !is Class<*>) {
                Any::class.java
            } else params[index] as Class<*>
        }
    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}