package cn.pivotstudio.modulec.homescreen.model

import com.google.gson.annotations.SerializedName

data class ForestCard(
    @field:SerializedName("background_image_url") val backgroundImageUrl: String,
    @field:SerializedName("cover_url") val coverUrl: String,
    @field:SerializedName("description") val description: String,
    @field:SerializedName("forest_id") val forestId: Int,
    @field:SerializedName("hole_number") val holeNumber: Int,
    @field:SerializedName("joined_number") val joinedNumber: Int,
    @field:SerializedName("last_active_time") val lastActiveTime: String,
    @field:SerializedName("name") val name: String,
    var Joined: Boolean = false
)

data class ForestCardList(
    val forests: List<ForestCard>
)

data class ForestTypes(
    val types: List<String>
)

