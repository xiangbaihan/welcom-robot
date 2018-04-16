package com.nb.robot.xf;

// Native interface for TTS (语音合成).
// Error code list: http://www.xfyun.cn/doccenter/faq?go=contitle66
public class TtsNative {
    public native void hello();
    
    // 登录
    public native int login();
    
    // 登出
    public native int logout();
    
    // Runs TTS demo.
    public native int runTts(String filename, String text, int speed, int volume, int pitch);
    
    static {
        System.loadLibrary("tts_native");
    }
}