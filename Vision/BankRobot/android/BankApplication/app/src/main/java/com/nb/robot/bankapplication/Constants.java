package com.nb.robot.bankapplication;

public class Constants {
    static final String SERVER_IP_ADDRESS = "192.168.31.9";
    static final int SERVER_PORT_SERVICE = 8080;
    static final int SERVER_PORT_SOCKET = 9090;

    static final String BROADCAST_ACTION_SOCKET =
            "com.nb.robot.bankapplication.socketAction";
    static final String BROADCAST_INTENT_SOCKET =
            "com.nb.robot.bankapplication.socketIntent";

    // Speaker.
    static final String SPEAKER_TALK_URI = "speaker/talk";
    static final int SPEAKER_SPEED = 50;
    static final int SPEAKER_VOLUME = 50;
    static final int SPEAKER_PITCH = 50;
    static final String SPEAKER_GOODBYE = "再见，欢迎下次使用";

    // Expression.
    static final String EXPRESSION_DYNAMIC_URI = "expression/dynamicExpresssion";

    // Dance.
    static final String DANCE_URI = "dance/control";

    // Arm.
    static final String ARM_URI = "motion/arm";

    // Speech recognition patterns. The string will be split based on comma.
    // TODO(Yang): use demoApp.bnf in Server for this demo Application.
    static final String SPEECH_KEYWORD_BANK_SERVICE = "银行,业务";
    static final String SPEECH_KEYWORD_ENTERTAINMENT = "娱乐,模式";
    static final String SPEECH_KEYWORD_BALANCE = "余额";
    static final String SPEECH_KEYWORD_TRANSACTION = "交易,记录";
    static final String SPEECH_KEYWORD_DANCE = "跳,舞";
    static final String SPEECH_KEYWORD_SMILE = "笑";
    static final String SPEECH_KEYWORD_RETURN = "返回，主菜单";
}
