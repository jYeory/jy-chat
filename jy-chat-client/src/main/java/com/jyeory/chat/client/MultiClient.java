package com.jyeory.chat.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import com.jyeory.chat.client.component.InputInfo;
import com.jyeory.chat.client.component.WaitingRoom;
import com.jyeory.chat.common.MsgInfo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MultiClient{
	public static String ip = "localhost";					// IP
	public static int port	= 3334;							// PORT번호
	
	private WaitingRoom waitroom = new WaitingRoom("대기실");
	private String name;
	
	static Socket socket;							
	static BufferedReader networkReader;			
	static BufferedWriter networkWriter;			
	
	
	public MultiClient(String name) throws IOException {
		this.name = name;				//사용자 ID
		waitroom.showFrame(name);
		setSocket(ip, port, name);
	}
	
	public MultiClient(String name, String ip, String port) throws NumberFormatException, IOException {
		this.name = name;				//사용자 ID
		waitroom.showFrame(name);
		setSocket(ip, Integer.parseInt(port), name);
	}

	public void setSocket(String ip, int port, String name) throws IOException{
		try{
			socket = new Socket(ip,port);
			networkWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			networkReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			ListenerOfClient listener = new ListenerOfClient(networkWriter, networkReader, socket, waitroom, this);
			//만약 생성자를 waitroom으로만 하게 되면 Listener_Of_Client에서는 서버명.networkWriter[Reader]로 해야함.
			listener.setDaemon(true);
			listener.start();

			sendMsg(MsgInfo.NEW, name);		//채팅자가 입장했음을 알림.
		}catch(IOException e){
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	public static void sendMsg(String token, String msg) throws IOException {
		if(msg == null){
			msg = "";
		}
		String data = new String(token + "/" + msg + "\n");
		networkWriter.write(new String(data.getBytes(), "UTF-8"));
		networkWriter.flush();
	}

	public static void main(String[] args) throws IOException {
		InputInfo getid = new InputInfo();
		getid.setVisible(true);
	}
}
