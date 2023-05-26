package com.codewithkael.webrtcprojectforrecord.trios.model

import com.google.gson.annotations.SerializedName

data class DataDto(
//    @SerializedName("room") var roomDto: RoomDto? = null,

    @SerializedName("name") var name: String? = null,

    @SerializedName("sdp") var sdp: String? = null
)
