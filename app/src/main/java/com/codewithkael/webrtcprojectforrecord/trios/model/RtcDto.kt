package com.codewithkael.webrtcprojectforrecord.trios.model

import com.google.gson.annotations.SerializedName

data class RtcDto(
    @SerializedName("type") var type: String? = null,

    @SerializedName("transId") var transId: Int? = null,

    @SerializedName("name") var name: String? = null,

    @SerializedName("data") var dataDto: DataDto? = null
)
