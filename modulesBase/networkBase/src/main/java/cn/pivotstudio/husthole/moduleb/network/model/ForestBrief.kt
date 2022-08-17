package cn.pivotstudio.husthole.moduleb.network.model

import com.squareup.moshi.Json

data class ForestBrief(
    val backUrl: String? = null,

    /**
     * 小树林简介
     */
    val brief: String? = null,

    /**
     * 小树林id
     */
    @Json(name = "forestId")
    val forestId: String? = null,

    /**
     * 小树林名字
     */
    val forestName: String? = null,

    /**
     * 小树林中的树洞数量
     */
    val holeNum: String? = null,

    /**
     * 图标链接
     */
    val iconUrl: String? = null,

    /**
     * 是否加入
     */
    val joined: Boolean? = null,

    /**
     * 小树林类型
     */
    val type: String? = null,

    /**
     * 加入小树林的用户数
     */
    val userNum: String? = null
)
