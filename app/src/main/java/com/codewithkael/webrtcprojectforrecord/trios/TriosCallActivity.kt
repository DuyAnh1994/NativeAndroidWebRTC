package com.codewithkael.webrtcprojectforrecord.trios

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.codewithkael.webrtcprojectforrecord.databinding.ActivityTriosCallBinding
import com.codewithkael.webrtcprojectforrecord.trios.model.DataDto
import com.codewithkael.webrtcprojectforrecord.trios.model.RtcDto
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


//        val dataDto = DataDto(
//            name = "a",
//            sdp = "v=0\\r\\no=- 838167271834414453 2 IN IP4 127.0.0.1\\r\\ns=-\\r\\nt=0 0\\r\\na=group:BUNDLE 0 1\\r\\na=extmap-allow-mixed\\r\\na=msid-semantic: WMS\\r\\nm=audio 9 UDP/TLS/RTP/SAVPF 111 63 9 0 8 13 110 126\\r\\nc=IN IP4 0.0.0.0\\r\\na=rtcp:9 IN IP4 0.0.0.0\\r\\na=ice-ufrag:Hjnx\\r\\na=ice-pwd:VopbeuzV3V6LebneFX5s/6bf\\r\\na=ice-options:trickle\\r\\na=fingerprint:sha-256 83:B1:19:B1:CD:D0:35:F2:D5:B3:C5:32:02:28:E0:15:8B:1F:3C:F9:19:A6:23:EB:66:B2:94:01:4F:3A:57:73\\r\\na=setup:actpass\\r\\na=mid:0\\r\\na=extmap:1 urn:ietf:params:rtp-hdrext:ssrc-audio-level\\r\\na=extmap:2 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\\r\\na=extmap:3 http://www.ietf.org/id/draft-holmer-rmcat-transport-wide-cc-extensions-01\\r\\na=extmap:4 urn:ietf:params:rtp-hdrext:sdes:mid\\r\\na=recvonly\\r\\na=rtcp-mux\\r\\na=rtpmap:111 opus/48000/2\\r\\na=rtcp-fb:111 transport-cc\\r\\na=fmtp:111 minptime=10;useinbandfec=1\\r\\na=rtpmap:63 red/48000/2\\r\\na=fmtp:63 111/111\\r\\na=rtpmap:9 G722/8000\\r\\na=rtpmap:0 PCMU/8000\\r\\na=rtpmap:8 PCMA/8000\\r\\na=rtpmap:13 CN/8000\\r\\na=rtpmap:110 telephone-event/48000\\r\\na=rtpmap:126 telephone-event/8000\\r\\nm=video 9 UDP/TLS/RTP/SAVPF 96 97 98 99 100 101 35 36 37 38 102 103 104 105 106 107 108 109 127 125 39 40 41 42 43 44 45 46 47 48 112 113 114 115 116 117 118 49\\r\\nc=IN IP4 0.0.0.0\\r\\na=rtcp:9 IN IP4 0.0.0.0\\r\\na=ice-ufrag:Hjnx\\r\\na=ice-pwd:VopbeuzV3V6LebneFX5s/6bf\\r\\na=ice-options:trickle\\r\\na=fingerprint:sha-256 83:B1:19:B1:CD:D0:35:F2:D5:B3:C5:32:02:28:E0:15:8B:1F:3C:F9:19:A6:23:EB:66:B2:94:01:4F:3A:57:73\\r\\na=setup:actpass\\r\\na=mid:1\\r\\na=extmap:14 urn:ietf:params:rtp-hdrext:toffset\\r\\na=extmap:2 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\\r\\na=extmap:13 urn:3gpp:video-orientation\\r\\na=extmap:3 http://www.ietf.org/id/draft-holmer-rmcat-transport-wide-cc-extensions-01\\r\\na=extmap:5 http://www.webrtc.org/experiments/rtp-hdrext/playout-delay\\r\\na=extmap:6 http://www.webrtc.org/experiments/rtp-hdrext/video-content-type\\r\\na=extmap:7 http://www.webrtc.org/experiments/rtp-hdrext/video-timing\\r\\na=extmap:8 http://www.webrtc.org/experiments/rtp-hdrext/color-space\\r\\na=extmap:4 urn:ietf:params:rtp-hdrext:sdes:mid\\r\\na=extmap:10 urn:ietf:params:rtp-hdrext:sdes:rtp-stream-id\\r\\na=extmap:11 urn:ietf:params:rtp-hdrext:sdes:repaired-rtp-stream-id\\r\\na=recvonly\\r\\na=rtcp-mux\\r\\na=rtcp-rsize\\r\\na=rtpmap:96 VP8/90000\\r\\na=rtcp-fb:96 goog-remb\\r\\na=rtcp-fb:96 transport-cc\\r\\na=rtcp-fb:96 ccm fir\\r\\na=rtcp-fb:96 nack\\r\\na=rtcp-fb:96 nack pli\\r\\na=rtpmap:97 rtx/90000\\r\\na=fmtp:97 apt=96\\r\\na=rtpmap:98 VP9/90000\\r\\na=rtcp-fb:98 goog-remb\\r\\na=rtcp-fb:98 transport-cc\\r\\na=rtcp-fb:98 ccm fir\\r\\na=rtcp-fb:98 nack\\r\\na=rtcp-fb:98 nack pli\\r\\na=fmtp:98 profile-id=0\\r\\na=rtpmap:99 rtx/90000\\r\\na=fmtp:99 apt=98\\r\\na=rtpmap:100 VP9/90000\\r\\na=rtcp-fb:100 goog-remb\\r\\na=rtcp-fb:100 transport-cc\\r\\na=rtcp-fb:100 ccm fir\\r\\na=rtcp-fb:100 nack\\r\\na=rtcp-fb:100 nack pli\\r\\na=fmtp:100 profile-id=2\\r\\na=rtpmap:101 rtx/90000\\r\\na=fmtp:101 apt=100\\r\\na=rtpmap:35 VP9/90000\\r\\na=rtcp-fb:35 goog-remb\\r\\na=rtcp-fb:35 transport-cc\\r\\na=rtcp-fb:35 ccm fir\\r\\na=rtcp-fb:35 nack\\r\\na=rtcp-fb:35 nack pli\\r\\na=fmtp:35 profile-id=1\\r\\na=rtpmap:36 rtx/90000\\r\\na=fmtp:36 apt=35\\r\\na=rtpmap:37 VP9/90000\\r\\na=rtcp-fb:37 goog-remb\\r\\na=rtcp-fb:37 transport-cc\\r\\na=rtcp-fb:37 ccm fir\\r\\na=rtcp-fb:37 nack\\r\\na=rtcp-fb:37 nack pli\\r\\na=fmtp:37 profile-id=3\\r\\na=rtpmap:38 rtx/90000\\r\\na=fmtp:38 apt=37\\r\\na=rtpmap:102 H264/90000\\r\\na=rtcp-fb:102 goog-remb\\r\\na=rtcp-fb:102 transport-cc\\r\\na=rtcp-fb:102 ccm fir\\r\\na=rtcp-fb:102 nack\\r\\na=rtcp-fb:102 nack pli\\r\\na=fmtp:102 level-asymmetry-allowed=1;packetization-mode=1;profile-level-id=42001f\\r\\na=rtpmap:103 rtx/90000\\r\\na=fmtp:103 apt=102\\r\\na=rtpmap:104 H264/90000\\r\\na=rtcp-fb:104 goog-remb\\r\\na=rtcp-fb:104 transport-cc\\r\\na=rtcp-fb:104 ccm fir\\r\\na=rtcp-fb:104 nack\\r\\na=rtcp-fb:104 nack pli\\r\\na=fmtp:104 level-asymmetry-allowed=1;packetization-mode=0;profile-level-id=42001f\\r\\na=rtpmap:105 rtx/90000\\r\\na=fmtp:105 apt=104\\r\\na=rtpmap:106 H264/90000\\r\\na=rtcp-fb:106 goog-remb\\r\\na=rtcp-fb:106 transport-cc\\r\\na=rtcp-fb:106 ccm fir\\r\\na=rtcp-fb:106 nack\\r\\na=rtcp-fb:106 nack pli\\r\\na=fmtp:106 level-asymmetry-allowed=1;packetization-mode=1;profile-level-id=42e01f\\r\\na=rtpmap:107 rtx/90000\\r\\na=fmtp:107 apt=106\\r\\na=rtpmap:108 H264/90000\\r\\na=rtcp-fb:108 goog-remb\\r\\na=rtcp-fb:108 transport-cc\\r\\na=rtcp-fb:108 ccm fir\\r\\na=rtcp-fb:108 nack\\r\\na=rtcp-fb:108 nack pli\\r\\na=fmtp:108 level-asymmetry-allowed=1;packetization-mode=0;profile-level-id=42e01f\\r\\na=rtpmap:109 rtx/90000\\r\\na=fmtp:109 apt=108\\r\\na=rtpmap:127 H264/90000\\r\\na=rtcp-fb:127 goog-remb\\r\\na=rtcp-fb:127 transport-cc\\r\\na=rtcp-fb:127 ccm fir\\r\\na=rtcp-fb:127 nack\\r\\na=rtcp-fb:127 nack pli\\r\\na=fmtp:127 level-asymmetry-allowed=1;packetization-mode=1;profile-level-id=4d001f\\r\\na=rtpmap:125 rtx/90000\\r\\na=fmtp:125 apt=127\\r\\na=rtpmap:39 H264/90000\\r\\na=rtcp-fb:39 goog-remb\\r\\na=rtcp-fb:39 transport-cc\\r\\na=rtcp-fb:39 ccm fir\\r\\na=rtcp-fb:39 nack\\r\\na=rtcp-fb:39 nack pli\\r\\na=fmtp:39 level-asymmetry-allowed=1;packetization-mode=0;profile-level-id=4d001f\\r\\na=rtpmap:40 rtx/90000\\r\\na=fmtp:40 apt=39\\r\\na=rtpmap:41 H264/90000\\r\\na=rtcp-fb:41 goog-remb\\r\\na=rtcp-fb:41 transport-cc\\r\\na=rtcp-fb:41 ccm fir\\r\\na=rtcp-fb:41 nack\\r\\na=rtcp-fb:41 nack pli\\r\\na=fmtp:41 level-asymmetry-allowed=1;packetization-mode=1;profile-level-id=f4001f\\r\\na=rtpmap:42 rtx/90000\\r\\na=fmtp:42 apt=41\\r\\na=rtpmap:43 H264/90000\\r\\na=rtcp-fb:43 goog-remb\\r\\na=rtcp-fb:43 transport-cc\\r\\na=rtcp-fb:43 ccm fir\\r\\na=rtcp-fb:43 nack\\r\\na=rtcp-fb:43 nack pli\\r\\na=fmtp:43 level-asymmetry-allowed=1;packetization-mode=0;profile-level-id=f4001f\\r\\na=rtpmap:44 rtx/90000\\r\\na=fmtp:44 apt=43\\r\\na=rtpmap:45 AV1/90000\\r\\na=rtcp-fb:45 goog-remb\\r\\na=rtcp-fb:45 transport-cc\\r\\na=rtcp-fb:45 ccm fir\\r\\na=rtcp-fb:45 nack\\r\\na=rtcp-fb:45 nack pli\\r\\na=rtpmap:46 rtx/90000\\r\\na=fmtp:46 apt=45\\r\\na=rtpmap:47 AV1/90000\\r\\na=rtcp-fb:47 goog-remb\\r\\na=rtcp-fb:47 transport-cc\\r\\na=rtcp-fb:47 ccm fir\\r\\na=rtcp-fb:47 nack\\r\\na=rtcp-fb:47 nack pli\\r\\na=fmtp:47 profile=1\\r\\na=rtpmap:48 rtx/90000\\r\\na=fmtp:48 apt=47\\r\\na=rtpmap:112 H264/90000\\r\\na=rtcp-fb:112 goog-remb\\r\\na=rtcp-fb:112 transport-cc\\r\\na=rtcp-fb:112 ccm fir\\r\\na=rtcp-fb:112 nack\\r\\na=rtcp-fb:112 nack pli\\r\\na=fmtp:112 level-asymmetry-allowed=1;packetization-mode=1;profile-level-id=64001f\\r\\na=rtpmap:113 rtx/90000\\r\\na=fmtp:113 apt=112\\r\\na=rtpmap:114 H264/90000\\r\\na=rtcp-fb:114 goog-remb\\r\\na=rtcp-fb:114 transport-cc\\r\\na=rtcp-fb:114 ccm fir\\r\\na=rtcp-fb:114 nack\\r\\na=rtcp-fb:114 nack pli\\r\\na=fmtp:114 level-asymmetry-allowed=1;packetization-mode=0;profile-level-id=64001f\\r\\na=rtpmap:115 rtx/90000\\r\\na=fmtp:115 apt=114\\r\\na=rtpmap:116 red/90000\\r\\na=rtpmap:117 rtx/90000\\r\\na=fmtp:117 apt=116\\r\\na=rtpmap:118 ulpfec/90000\\r\\na=rtpmap:49 flexfec-03/90000\\r\\na=rtcp-fb:49 goog-remb\\r\\na=rtcp-fb:49 transport-cc\\r\\na=fmtp:49 repair-window=10000000\\r\\n"
//        )
//
//        val rtcDto = RtcDto(
//            type = "cmd",
//            transId = 0,
//            name = "join",
//            dataDto = dataDto
//        )

        // send socket cmd create offer

        binding.sendCmd.setOnClickListener {
//            socketClient?.sendMessageToSocket(rtcDto)
            callRequest()
        }
    }

    override fun onMessage(rtcDto: RtcDto) {
        Log.d(TAG, "onMessage() called with: rtcDto = $rtcDto")
    }

    private fun callRequest() {
        runOnUiThread {
//            setWhoToCallLayoutGone()
//            setCallLayoutVisible()
            binding.apply {
//                rtcClient?.initializeSurfaceView(localView)
//                rtcClient?.initializeSurfaceView(remoteView)
//                rtcClient?.startLocalVideo(localView)
                rtcClient?.call()
            }
        }
    }

    private fun offerResponse() {
        runOnUiThread {
            setIncomingCallLayoutVisible()
            binding.incomingNameTV.text = "... is calling you"

            binding.acceptButton.setOnClickListener {
                setIncomingCallLayoutGone()
                setCallLayoutVisible()
                setWhoToCallLayoutGone()

                binding.apply {
                    rtcClient?.initializeSurfaceView(localView)
                    rtcClient?.initializeSurfaceView(remoteView)
                    rtcClient?.startLocalVideo(localView)
                }

                val sdp = "sdp..."
//                Log.d(TAG, "sdp: $sdp")

                val session = SessionDescription(SessionDescription.Type.OFFER, sdp)
                rtcClient?.onRemoteSessionReceived(session)
                rtcClient?.answer()
                hideLoading()

            }
            binding.rejectButton.setOnClickListener {
                setIncomingCallLayoutGone()
            }
        }
    }

    private fun answerResponse() {
        val sdp = "sdp..."
//        Log.d(TAG, "sdp: $sdp")

        val session = SessionDescription(SessionDescription.Type.ANSWER, sdp)
        rtcClient?.onRemoteSessionReceived(session)
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