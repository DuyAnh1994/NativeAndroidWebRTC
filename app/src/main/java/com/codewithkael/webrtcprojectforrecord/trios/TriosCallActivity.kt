package com.codewithkael.webrtcprojectforrecord.trios

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.codewithkael.webrtcprojectforrecord.databinding.ActivityTriosCallBinding
import com.codewithkael.webrtcprojectforrecord.trios.model.call.response.RtcDtoResponse
import com.codewithkael.webrtcprojectforrecord.trios.model.event.response.EventDtoResponse
import com.codewithkael.webrtcprojectforrecord.utils.PeerConnectionObserver
import com.codewithkael.webrtcprojectforrecord.utils.RTCAudioManager
import com.codewithkael.webrtcprojectforrecord.utils.gone
import com.codewithkael.webrtcprojectforrecord.utils.show
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

class TriosCallActivity : AppCompatActivity(), TriosSocketListener {

    companion object {
        private const val TAG = "TriosCallActivity"
    }

    lateinit var binding: ActivityTriosCallBinding
    private var socketClient: TriosSocket? = null
    private var rtcClient: TriosRTCClient? = null
    private var peerConnectionObserver: PeerConnectionObserver? = object : PeerConnectionObserver() {
        override fun onIceCandidate(p0: IceCandidate?) {
            super.onIceCandidate(p0)
            rtcClient?.addIceCandidate(p0)
        }
    }
    private val rtcAudioManager by lazy { RTCAudioManager.create(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTriosCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        socketClient = TriosSocket(this)
        socketClient?.initSocket()
        rtcClient = TriosRTCClient(
            application = application,
            socket = socketClient!!,
            observer = peerConnectionObserver!!
        )

        rtcAudioManager.setDefaultAudioDevice(RTCAudioManager.AudioDevice.SPEAKER_PHONE)
        binding.sendCmd.setOnClickListener {
            callRequest()
        }
    }

    override fun onRtcResponse(rtcDto: RtcDtoResponse) {
        Log.d(TAG, "onMessage() called with: rtcDto = $rtcDto")
        offerResponse(rtcDto.dataDto?.sdp)
    }

    override fun onRtcEvent(eventDto: EventDtoResponse) {
        Log.d(TAG, "onRtcEvent() called with: eventDto = $eventDto")
    }

    private fun callRequest() {
        runOnUiThread {
            setWhoToCallLayoutGone()
            setCallLayoutVisible()
            binding.apply {
                rtcClient?.initializeSurfaceView(localView)
                rtcClient?.initializeSurfaceView(remoteView)
                rtcClient?.startLocalVideo(localView)
                rtcClient?.call(targetUserNameEt.text.toString())
            }
        }
    }

    private fun offerResponse(sdp: String?) {
        runOnUiThread {
            setIncomingCallLayoutVisible()
            binding.incomingNameTV.text = "... is calling you"

            setIncomingCallLayoutGone()
            setCallLayoutVisible()
            setWhoToCallLayoutGone()

            val session = SessionDescription(SessionDescription.Type.OFFER, sdp)
            rtcClient?.setRemoteDesc(session)
            rtcClient?.answer()
            hideLoading()
        }
    }

    private fun answerResponse() {
        val sdp = "sdp..."
//        Log.d(TAG, "sdp: $sdp")

        val session = SessionDescription(SessionDescription.Type.ANSWER, sdp)
        rtcClient?.setRemoteDesc(session)
        runOnUiThread {
            hideLoading()
        }
    }

    private fun setIncomingCallLayoutGone() {
        binding.incomingCallLayout.gone()
    }

    private fun setIncomingCallLayoutVisible() {
        binding.incomingCallLayout.show()
    }

    private fun setCallLayoutGone() {
        binding.callLayout.gone()
    }

    private fun setCallLayoutVisible() {
        binding.callLayout.show()
    }

    private fun setWhoToCallLayoutGone() {
        binding.whoToCallLayout.gone()
    }

    private fun setWhoToCallLayoutVisible() {
        binding.whoToCallLayout.show()
    }

    private fun showLoading() {
        binding.remoteViewLoading.show()
    }

    private fun hideLoading() {
        binding.remoteViewLoading.gone()
    }
}
