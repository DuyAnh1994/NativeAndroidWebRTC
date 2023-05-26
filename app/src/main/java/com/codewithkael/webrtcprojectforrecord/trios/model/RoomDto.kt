package com.codewithkael.webrtcprojectforrecord.trios.model

import com.google.gson.annotations.SerializedName

data class RoomDto(
    @SerializedName("id") var id: String? = null,

    @SerializedName("participants") var participants: List<ParticipantDto>? = null
)
