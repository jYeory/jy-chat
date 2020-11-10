package com.jyeory.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.jyeory.chat.common.ClientConnection;
import com.jyeory.chat.common.RoomManager;

public class MultiServer {
	final String SERVER = "[SERVER] ";
	private String username;
	private ServerSocket filesocket;			//파일 서버
	private RoomManager rm;
	
	public void clientConnectionStart(){
		ServerSocket serverSocket=null;
		try{
			System.out.println("서버 대기상태");
			serverSocket = new ServerSocket(3334);
			
			while(true){
				Socket socket = serverSocket.accept();
				System.out.println(SERVER + "소켓 : " + socket + " 에 연결됨");

				ClientConnection client = new ClientConnection(socket, this.rm);
				new Thread(client).start(); 		//클라이언트 쓰레드를 시작합니다.
				
//				RoomManager.allUserList.add(client);
				this.rm.allUserList.add(client);
			}
			
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try {serverSocket.close();} catch (IOException e) {}
		}
	}
	
	public static void main(String[] args){
		MultiServer server = new MultiServer();
		server.rm = new RoomManager();
		server.rm.makeRoom("Main");
		
		System.out.println("Main 대화방 생성 완료");
		server.clientConnectionStart();
	}
}
