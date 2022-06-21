package cn.pivotstudio.modulec.homescreen.network

import cn.pivotstudio.modulec.homescreen.model.ForestHole
import com.google.gson.annotations.SerializedName

data class ForestHolesSearchResponse(
    val items: List<ForestHole> = emptyList()
)
