package com.nb.robot.demoapplication;

/**
 * Created by linshangjun on 2017/7/11.
 */

public class Consts {
    public static final String SERVER_IP="http://192.168.1.110:8080/";

    public static final String BROADCAST_ACTION = "com.nb.robot.demoapplication.fragment";

    public static final String MOTION_CONTROL=SERVER_IP+"motion/chasis";
    public static final String MOTION_RESET = SERVER_IP+"motion/resetCoordinate";
    public static final String ARM_CONTROL=SERVER_IP+"motion/arm";
    public static final String STATIC_EXPRESSION_CONTROL=SERVER_IP+"expression/staticExpresssion";
    public static final String DYNAMIC_EXPRESSION_CONTROL=SERVER_IP+"expression/dynamicExpresssion";
    public static final String SPEECH_UPLOAD = SERVER_IP+"speech/upload";
    public static final String DANCE_CONTROL =SERVER_IP+"dance/control";
    public static final String DANCE_DANCEFLAG =SERVER_IP+"dance/danceFlag";
    public static final String DANCE_MUSICFLAG =SERVER_IP+"dance/musicFlag";
    public static final String SPEAKER_TALK = SERVER_IP+"speaker/talk";
    public static final String SPEAKER_CONTROL = SERVER_IP+"speaker/control";
    public static final String LED_CONTROL = SERVER_IP+"led/control";
    public static final String MOUDLE_CONTROL = SERVER_IP+"control";
    public static final String BATTERY_CONTROL = SERVER_IP+"battery";

}
