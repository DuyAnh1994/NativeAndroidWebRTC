package com.codewithkael.webrtcprojectforrecord.trios

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.codewithkael.webrtcprojectforrecord.databinding.ActivityTriosCallBinding
import com.codewithkael.webrtcprojectforrecord.trios.model.call.response.RtcDtoResponse
import com.codewithkael.webrtcprojectforrecord.trios.model.call.update.RtcDtoUpdate
import com.codewithkael.webrtcprojectforrecord.trios.model.event.response.EventDtoResponse
import com.codewithkael.webrtcprojectforrecord.utils.PeerConnectionObserver
import com.codewithkael.webrtcprojectforrecord.utils.RTCAudioManager
import com.codewithkael.webrtcprojectforrecord.utils.gone
import com.codewithkael.webrtcprojectforrecord.utils.show
import org.webrtc.SessionDescription

class TriosCallActivity : AppCompatActivity(), TriosSocketListener {

    companion object {
        private const val TAG = "TriosCallActivity"
    }

    lateinit var binding: ActivityTriosCallBinding
    private var socketClient: TriosSocket? = null
    private var rtcClient: TriosRTCClient? = null
    private var peerConnectionObserver: PeerConnectionObserver? = object : PeerConnectionObserver() {
//        override fun onSignalingChange(p0: PeerConnection.SignalingState?) {
//            Log.d(TAG, "onSignalingChange() called with: p0 = ${p0?.name}")
//        }
//
//        override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {
//            Log.d(TAG, "onIceConnectionChange() called with: p0 = ${p0?.name}")
//        }
//
//        override fun onIceConnectionReceivingChange(p0: Boolean) {
//            Log.d(TAG, "onIceConnectionReceivingChange() called with: p0 = $p0")
//        }
//
//        override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {
//            Log.d(TAG, "onIceGatheringChange: ${p0?.name}")
//
//        }
//
//        override fun onIceCandidate(p0: IceCandidate?) {
//            Log.d(TAG, "onIceCandidate: ${p0?.sdpMid}")
//            rtcClient?.addIceCandidate(p0)
//        }
//
//        override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {
//            Log.d(TAG, "onIceCandidatesRemoved() called with: p0 = $p0")
//        }
//
//        override fun onAddStream(p0: MediaStream?) {
//            Log.d(TAG, "onAddStream() called with: p0 = $p0")
//        }
//
//        override fun onRemoveStream(p0: MediaStream?) {
//            Log.d(TAG, "onRemoveStream() called with: p0 = $p0")
//        }
//
//        override fun onDataChannel(p0: DataChannel?) {
//            Log.d(TAG, "onDataChannel() called with: p0 = $p0")
//        }
//
//        override fun onRenegotiationNeeded() {
//            Log.d(TAG, "onRenegotiationNeeded() called")
//        }
//
//        override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {
//            Log.d(TAG, "onAddTrack() called with: p0 = $p0, p1 = $p1")
//        }
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
        Log.d(TAG, "onRtcResponse() called with: rtcDto = ${rtcDto.type}")

    }

    override fun onRtcEvent(eventDto: EventDtoResponse) {
        Log.d(TAG, "onRtcEvent() called with: eventDto = ${eventDto.type}")
    }

    override fun onRtcUpdate(rtcDto: RtcDtoUpdate) {
        Log.d(TAG, "onRtcUpdate() called with: eventDto = ${rtcDto.type}")
        offerResponse(rtcDto.dataDto?.sdp)
    }

    private fun callRequest() {
        runOnUiThread {
            setWhoToCallLayoutGone()
            setCallLayoutVisible()
            binding.apply {
                rtcClient?.initializeSurfaceView(localView)
                rtcClient?.initializeSurfaceView(remoteView)
                rtcClient?.startLocalVideo(localView)
//                rtcClient?.createDataChannel("room 1")
                rtcClient?.createOffer(targetUserNameEt.text.toString())
            }
        }
    }

    private fun offerResponse(sdp: String?) {
        Log.d(TAG, "offerResponse: ccccccccc")
//        runOnUiThread {
//            val session = SessionDescription(SessionDescription.Type.OFFER, sdp)
//            rtcClient?.setRemoteDesc(session)
//            rtcClient?.createAnswer() {
//                hideLoading()
//            }
//        }
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
