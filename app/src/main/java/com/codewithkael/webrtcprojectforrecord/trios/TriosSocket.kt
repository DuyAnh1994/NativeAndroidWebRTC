package com.codewithkael.webrtcprojectforrecord.trios

import android.util.Log
import com.codewithkael.webrtcprojectforrecord.trios.model.RtcDto
import com.google.gson.Gson
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI


class TriosSocket(private val listener: TriosSocketListener) {

    companion object {
        private const val TAG = "TriosSocket"
        private const val WS_URL = "wss://dev.turn2.gtrios.io:8084/?id=bGlobal"
    }

    private var webSocket: WebSocketClient? = null
    private val gson = Gson()

    fun initSocket() {
        webSocket = object : WebSocketClient(URI(WS_URL)) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.d(TAG, "onOpen: connecting  httpStatus: ${handshakedata?.httpStatus}  httpMsg: ${handshakedata?.httpStatusMessage}")
            }

            override fun onMessage(message: String?) {
                Log.d(TAG, "onMessage raw data: $message")
                listener.onMessage(gson.fromJson(message, RtcDto::class.java))
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.d(TAG, "onClose() called with: code = $code, reason = $reason, remote = $remote")
            }

            override fun onError(ex: Exception?) {
                ex?.printStackTrace()
                Log.d(TAG, "onError: ${ex?.message}")
            }
        }

        webSocket?.connect()
    }

    fun sendMessageToSocket(rtcDto: RtcDto) {
        try {
            val json = gson.toJson(rtcDto)
            Log.d(TAG, "send json: $json")
            webSocket?.send(json)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
