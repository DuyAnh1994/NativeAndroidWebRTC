package com.codewithkael.webrtcprojectforrecord.trios

import com.codewithkael.webrtcprojectforrecord.trios.model.RtcDto

interface TriosSocketListener {
    fun  onMessage(rtcDto: RtcDto)
}
