package com.holike.cloudshelf.bean.internal

//字典标签  - 内部数据
class LabelSpec(var id: String?, var iconRes: Int,
                var name: String?, var specList: MutableList<Spec>) {

    override fun equals(other: Any?): Boolean {
        return when (other) {
            !is LabelSpec -> false
            else -> this === other || this.id == other.id
        }
    }

    override fun hashCode(): Int {
        return 31 + (id?.hashCode() ?: 0)
    }

    class Spec {

        var id: String? = null
        var name: String? = null
        private var specId: String? = null
        private var parentId: String? = null
        var isMore = false  //是否是更多按钮

        constructor(id: String?, name: String?) {
            this.id = id
            this.name = name
        }

        constructor(id: String?, name: String?, isMore: Boolean) {
            this.id = id
            this.name = name
            this.isMore = isMore
        }

        constructor(id: String?, name: String?, specId: String?, parentId: String?) {
            this.id = id
            this.name = name
            this.specId = specId
            this.parentId = parentId
        }

        override fun equals(other: Any?): Boolean {
            return when (other) {
                !is LabelSpec -> false
                else -> this === other || this.id == other.id
            }
        }

        override fun hashCode(): Int {
            return 31 + (id?.hashCode() ?: 0)
        }

    }
}