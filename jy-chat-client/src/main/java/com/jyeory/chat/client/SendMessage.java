package com.jyeory.chat.client;

import java.io.IOException;

import com.jyeory.chat.common.MsgInfo;

public class SendMessage {
	public static void chattingStart(String consoleData){
		System.out.println("SendMessage.chattingStart() : "+consoleData);
		try{
			if("".equals(consoleData)){
				consoleData = " ";
				MultiClient.sendMsg(MsgInfo.CHAT,consoleData);		//그냥 엔터를 치더라도 값이 나와야 함.
			}else if("EXIT".equals(consoleData)){		//exit는 종료
				MultiClient.sendMsg(MsgInfo.EXIT,null);
			}else{										//그 외는 대화이므로..
				MultiClient.sendMsg(MsgInfo.CHAT,consoleData);
			}
		}catch(Exception e1){
			try {MultiClient.networkWriter.close();} catch (IOException e) {}
			try {MultiClient.networkReader.close();} catch (IOException e) {}
			try {MultiClient.socket.close();       } catch (IOException e) {}
		}
	}
}
