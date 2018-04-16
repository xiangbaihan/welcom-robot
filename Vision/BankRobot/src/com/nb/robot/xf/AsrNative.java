package com.nb.robot.xf;

// Native interface for ASR (语音识别).
// Error code list: http://www.xfyun.cn/doccenter/faq?go=contitle66
public class AsrNative {
    public native void hello();
    
    // 登录
    public native int login();
    
    // 登出
    public native int logout();
    
    // 构建离线识别语法网络
    public native UserData buildGrammar(String bnfFile);

    // 更新离线语法词典
    // - grammar_slot: the predefined slot name in grammar, see bank.bnf file.
    // - keywords: string of keywords concatenated by "\n", like "<keyword1>\n<keyword2>".
    public native UserData updateLexicon(UserData udata, String slotName, String keywords);
    
    // 运行语音识别(SR).
    // SR terminates after 2~3s silence, or tries to recognize voice up to 10s
    // after calling this function.
    public native UserData runAsr(UserData udata);
    
    static {
        System.loadLibrary("asr_native");
    }
}
