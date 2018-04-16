package com.nb.robot.server;

import java.io.IOException;
import java.util.Scanner;

public class SocketServerTest {  

    public static  void main(String argv[]) throws IOException, InterruptedException {
    	SocketServerModule socketServer = SocketServerModule.getInstance();
    	socketServer.start();
        while (true) {
            Scanner scanner = new Scanner(System.in);
            String ip = scanner.next();
            System.out.println("向客户端 [ "+ip+" ] 发送消息");
//          client  "192.168.0.102"
            for (int i=0;i<50;i++) {
            	socketServer.sendMessage(ip, "这是一条来自服务器的消息。。。"+i);
            }
        }
    }

}
