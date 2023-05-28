package com.codewithkael.webrtcprojectforrecord.trios

import android.app.Application
import com.codewithkael.webrtcprojectforrecord.trios.model.DataDto
import com.codewithkael.webrtcprojectforrecord.trios.model.RtcDto
import org.webrtc.AudioTrack
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraVideoCapturer
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import org.webrtc.PeerConnection.RTCConfiguration
import org.webrtc.PeerConnectionFactory
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import org.webrtc.SurfaceTextureHelper
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoTrack

class TriosRTCClient(
    private val application: Application,
    private val socket: TriosSocket,
    private val observer: PeerConnection.Observer
) {

    private companion object {
        private const val TAG = "TriosRTCClient"
        private const val RTC_URL = "turn:dev.turn2.gtrios.io:3478"
        private const val USERNAME = "bgldemo"
        private const val PASSWORD = "bgltest"
    }

    init {
        initPeerConnectionFactory(application)
    }

    private val eglContext = EglBase.create()
    private val peerConnectionFactory by lazy { createPeerConnectionFactory() }
    private val iceServer = listOf(
        PeerConnection.IceServer.builder(RTC_URL)
            .setUsername(USERNAME)
            .setPassword(PASSWORD)
            .createIceServer()
    )
    private val peerConnection by lazy { createPeerConnection(observer) }
    private val localVideoSource by lazy { peerConnectionFactory.createVideoSource(false) }
    private val localAudioSource by lazy { peerConnectionFactory.createAudioSource(MediaConstraints()) }
    private var videoCapturer: CameraVideoCapturer? = null
    private var localAudioTrack: AudioTrack? = null
    private var localVideoTrack: VideoTrack? = null


    private fun initPeerConnectionFactory(application: Application) {
        val peerConnectionOption = PeerConnectionFactory.InitializationOptions.builder(application)
            .setEnableInternalTracer(true)
            .setFieldTrials("WebRTC-H264HighProfile/Enabled/")
            .createInitializationOptions()

        PeerConnectionFactory.initialize(peerConnectionOption)
    }

    private fun createPeerConnectionFactory(): PeerConnectionFactory {
        val encoderFactory = DefaultVideoEncoderFactory(eglContext.eglBaseContext, true, true)
        val decoderFactory = DefaultVideoDecoderFactory(eglContext.eglBaseContext)
        val option = PeerConnectionFactory.Options()

        val builder: PeerConnectionFactory.Builder = PeerConnectionFactory.builder()
            .setVideoEncoderFactory(encoderFactory)
            .setVideoDecoderFactory(decoderFactory)
            .setOptions(option)

        return builder.createPeerConnectionFactory()
    }

    private fun createPeerConnection(observer: PeerConnection.Observer): PeerConnection? {
        val rtcConfiguration = RTCConfiguration(iceServer).apply {
            iceTransportsType = PeerConnection.IceTransportsType.ALL
            bundlePolicy = PeerConnection.BundlePolicy.MAXCOMPAT
            disableIpv6 = true
            disableIPv6OnWifi = true
            sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
            iceBackupCandidatePairPingInterval = 1000
            candidateNetworkPolicy = PeerConnection.CandidateNetworkPolicy.ALL
        }
        return peerConnectionFactory.createPeerConnection(rtcConfiguration, observer)
    }

    fun initializeSurfaceView(surface: SurfaceViewRenderer) {
        surface.run {
            setEnableHardwareScaler(true)
            setMirror(true)
            init(eglContext.eglBaseContext, null)
        }
    }

    fun startLocalVideo(surface: SurfaceViewRenderer) {
        val surfaceTextureHelper = SurfaceTextureHelper.create(Thread.currentThread().name, eglContext.eglBaseContext)
        videoCapturer = getVideoCapturer(application)
        videoCapturer?.initialize(surfaceTextureHelper, surface.context, localVideoSource.capturerObserver)
        videoCapturer?.startCapture(1920, 1080, 60)

        localVideoTrack = peerConnectionFactory.createVideoTrack("local_track", localVideoSource)
        localVideoTrack?.addSink(surface)
        localAudioTrack = peerConnectionFactory.createAudioTrack("local_track_audio", localAudioSource)

        val localStream = peerConnectionFactory.createLocalMediaStream("local_stream")
        localStream.addTrack(localAudioTrack)
        localStream.addTrack(localVideoTrack)

        peerConnection?.addStream(localStream)
    }

    private fun getVideoCapturer(application: Application): CameraVideoCapturer {
        return Camera2Enumerator(application).run {
            deviceNames.find { isFrontFacing(it) }?.let {
                createCapturer(it, null)
            } ?: throw IllegalStateException()
        }
    }

    fun call(target: String? = null) {
        val mediaConstraints = MediaConstraints()
        mediaConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))

        val sdpObserver = object : SdpObserverImpl() {
            override fun onCreateSuccess(desc: SessionDescription?) {
                setLocalDesc(desc, target)
            }
        }

        peerConnection?.createOffer(sdpObserver, mediaConstraints)
    }

    fun setLocalDesc(desc: SessionDescription?, target: String?) {
        val sdpObserver = object : SdpObserver {
            override fun onCreateSuccess(p0: SessionDescription?) {}
            override fun onSetSuccess() {
                val dataDto = DataDto(
                    name = target,
                    sdp = desc?.description
                )

                val rtcDto = RtcDto(
                    type = "cmd",
                    transId = 0,
                    name = "join",
                    dataDto = dataDto
                )

                // send socket cmd create offer
                socket.sendMessageToSocket(rtcDto)
            }

            override fun onCreateFailure(p0: String?) {}
            override fun onSetFailure(p0: String?) {}
        }

        peerConnection?.setLocalDescription(sdpObserver, desc)
    }

    fun onRemoteSessionReceived(session: SessionDescription) {
        val sdpObserver = object : SdpObserver {
            override fun onCreateSuccess(p0: SessionDescription?) {}
            override fun onSetSuccess() {}
            override fun onCreateFailure(p0: String?) {}
            override fun onSetFailure(p0: String?) {}
        }

        peerConnection?.setRemoteDescription(sdpObserver, session)
    }

    fun answer(target: String? = null) {
        val constraints = MediaConstraints()
        constraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))

        val sdpObserver = object : SdpObserver {
            override fun onCreateSuccess(desc: SessionDescription?) {
                setLocalDesc(desc)
            }

            override fun onSetSuccess() {}
            override fun onCreateFailure(p0: String?) {}
            override fun onSetFailure(p0: String?) {}
        }

        peerConnection?.createAnswer(sdpObserver, constraints)
    }

    private fun setLocalDesc(desc: SessionDescription?) {
        val sdpObserver = object : SdpObserver {
            override fun onCreateSuccess(p0: SessionDescription?) {}
            override fun onSetSuccess() {
//                val answer = hashMapOf("sdp" to desc?.description, "type" to desc?.type)

                // send socket cmd create answer
//                        socket.sendMessageToSocket("")
            }

            override fun onCreateFailure(p0: String?) {}
            override fun onSetFailure(p0: String?) {}
        }

        peerConnection?.setLocalDescription(sdpObserver, desc)
    }

    fun addIceCandidate(p0: IceCandidate?) {
        peerConnection?.addIceCandidate(p0)
    }

    fun switchCamera() {
        videoCapturer?.switchCamera(null)
    }

    fun toggleAudio(mute: Boolean) {
        localAudioTrack?.setEnabled(mute)
    }

    fun toggleCamera(cameraPause: Boolean) {
        localVideoTrack?.setEnabled(cameraPause)
    }

    fun endCall() {
        peerConnection?.close()
    }
}
