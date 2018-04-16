package com.nb.robot.server;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.nb.robot.common.Constants;
import com.nb.robot.common.FunctionModule;
import com.nb.robot.common.UtilStatus;
import com.nb.robot.service.HumanDetectionModule;

// Socket server that accepts client connection and sends notification message.
// This is a singleton class.
public class SocketServerModule implements FunctionModule {
	private static Logger logger = Logger.getLogger(SocketServerModule.class);

	private static volatile SocketServerModule instance = null;
	private final int THREAD_POOL_SIZE = 5;

	private ServerSocket serverSocket;
	private ExecutorService executorService;
	public Hashtable<String, Socket> clientSocketList;
	private boolean isRunning;
	private String errorMessage = "";

	public static SocketServerModule getInstance() {
		if (instance == null) {
			synchronized (SocketServerModule.class) {
				if (instance == null) {
					instance = new SocketServerModule();
				}
			}
		}
		return instance;
	}

	private SocketServerModule() {
		isRunning = false;
	};

	@Override
	public synchronized boolean start() {
		executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		clientSocketList = new Hashtable<>();
		try {
			serverSocket = new ServerSocket();
			InetSocketAddress isa = new InetSocketAddress(Constants.DEFAULT_SERVER_SOCKET_PORT);
			serverSocket.bind(isa, THREAD_POOL_SIZE);
		} catch (IOException e) {
			e.printStackTrace();
			errorMessage = "Failed to initialilze server socket: " + e.getMessage();
			logger.error(errorMessage);
			return false;
		}
		isRunning = true;
		errorMessage = "";
		executorService.execute(new listeningTask());
		logger.info("SocketServerModule started");
		return true;
	}

	@Override
	public synchronized void stop() {
		isRunning = false;
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		executorService.shutdown();

		clientSocketList = null;
		serverSocket = null;
		executorService = null;
		logger.info("SocketServerModule stopped");
	}

	@Override
	public synchronized boolean isHealthy() {
		if (serverSocket == null || serverSocket.isClosed()) {
			return false;
		}
		return true;
	}

	@Override
	public synchronized String errorMessage() {
		return errorMessage;
	}

	// Listening for clients to connect.
	public class listeningTask implements Runnable {
		@Override
		public void run() {
			while (isRunning) {
				try {
					Socket socket = serverSocket.accept();
					String remoteIP = socket.getInetAddress().getHostAddress().toString();
					clientSocketList.put(remoteIP, socket);// 将与客户端连接匹配的socket对象保存
					logger.debug("服务器接收到连接请求. IP = " + remoteIP);

					// NOTE: currently we do not expect clients to send message
					// via socket.
					// executorService.execute(new readTask(remoteIP));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// Sends message to all connected clients.
	public synchronized void sendMessage(String message) {
		executorService.execute(new WriteTask( message));
	}

	// Sends message to the given IP.
	public synchronized void sendMessage(String ip, String message) {
		executorService.execute(new WriteTask(ip, message));
	}

	// Writes message to client socket.
	private class WriteTask implements Runnable {
		private String message;
		private String clientIP = null;

		// Writes message to all connected client sockets.
		public WriteTask(String msg) {
			message = msg;
			clientIP = null;
		}

		// Writes message to the given connected client socket.
		public WriteTask(String ip, String msg) {
			message = msg;
			clientIP = ip;
		}

		@Override
		public void run() {
			Set<String> clientIPs = null;
			if (clientIP == null) {
				clientIPs = clientSocketList.keySet();
			} else {
				clientIPs = new HashSet<String>();
				clientIPs.add(clientIP);
			}
			for (String ip : clientIPs) {
				Socket socket = (Socket) clientSocketList.get(ip);
				if (socket == null) {
					logger.error("Failed to find connected socket for IP: " + ip);
					continue;
				}
				UtilStatus status = writeMessageToSocket(socket, message);
				if (!status.isOK()) {
					logger.error(status.toString());
					continue;
				}
			}
		}
	}

	private UtilStatus writeMessageToSocket(Socket socket, String message) {
		try {
			OutputStream outputStream = socket.getOutputStream();
			PrintWriter writer = new PrintWriter(outputStream);
			writer.println(message);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
			return new UtilStatus(-1,
					"Failed to write message to socket " + socket.getInetAddress() + ": " + e.getMessage());
		}
		return new UtilStatus();
	}

	// Reads message from client socket.
	private class readTask implements Runnable {
		private String IP;

		public readTask(String ip) {
			this.IP = ip;
		}

		@Override
		public void run() {
			while (isRunning) {
				logger.debug("监听客户端，等待接收数据。。。");
				try {
					Socket socket = (Socket) clientSocketList.get(IP);// 获取指定客户端IP对应的socket
					BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					String index;
					while ((index = reader.readLine()) != null) {// socket没有关闭的话会一直阻塞，等待接收数据
						logger.debug(
								"服务器接收到来自客户端 [ " + this.IP + " ] 的数据 ---- " + index + " , 数据长度 = " + index.length());
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
