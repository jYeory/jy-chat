package com.jyeory.chat.client;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.GregorianCalendar;

import com.jyeory.chat.client.component.ChatRoom;
import com.jyeory.chat.client.component.SelectChatUser;
import com.jyeory.chat.client.component.SendMemo;
import com.jyeory.chat.client.component.TransferFile;
import com.jyeory.chat.common.MsgInfo;
/*
 * 파일 메뉴를 이용해서
 * 대화방에서 채팅을 하더라도 다른 유저에게 메신저 기능을 사용 가능케 하는 부분.
 */
public class ChatEventClass implements ActionListener {
	String received;
	String roomtitle;
	Frame[] frame;
	FileDialog fdial;
	public ChatEventClass(String title, Frame[] frames) {
		this.roomtitle = title;
		frame = frames;
	}
	public void actionPerformed(ActionEvent e) {
		String id = ChatRoom.inputid.getText();
		String selectmenu = e.getActionCommand();
		/*
		 *	파일 저장 다이얼로그를 띄워서 현재 대화 내용을 저장한다. 
		 */
		if (selectmenu.equals("대화내용 저장")){
	//윈도우 JVM에서는 FilenameFilter가 적용되지 않는다.
	//굳이 파일 필터를 적용하고자 한다면 JFileChooser를 사용해서 만들면 된다.... 젠장..			
			fdial = new FileDialog(frame[0], "저장", FileDialog.SAVE);		//저장모드.	
			fdial.setVisible(true);
			/*
			 * 	날짜와 시각을 위해 GregorianCalendar와 DateFormat을 이용한다.
			 */
			GregorianCalendar gc = new GregorianCalendar();		
			DateFormat df = DateFormat.getInstance();
			String data = df.format(gc.getTime())+"\r\n["+roomtitle+"]"+"에서의 대화내용"+"\r\n";			//시간 추가
			data += ChatRoom.showText.getText().replaceAll("\n", "\r\n");		
			// \n의 값을 \r\n으로 해야 메모장에서 엔터 기능이 이루어 진다.
			BufferedWriter bw;
			try {
				try{
			//BufferedWriter를 저장 위치로 설정.
				bw = new BufferedWriter(new FileWriter(fdial.getDirectory()+"\\"+fdial.getFile()));
				bw.write(data);
				bw.close();
				}catch(FileNotFoundException file){}
			} catch (IOException e1) {e1.printStackTrace();}
		// 접속자 메뉴 클릭시.
		}else if(selectmenu.equals("전체 접속자")){
			try {
				MultiClient.sendMsg(MsgInfo.SHOWUSER, null);
			} catch (IOException e1) { e1.printStackTrace(); }
		// 쪽지 보내기 클릭시.
		}else if(selectmenu.equals("쪽지 보내기")){
			SendMemo memo = new SendMemo();
			memo.showFrame(null, id);
		// 대화하기 클릭시.
		}else if(selectmenu.equals("대화하기")){
			SelectChatUser seluser = new SelectChatUser(id);
		// 파일 보내기 클릭시.	
		}else if(selectmenu.equals("파일 보내기")){
			TransferFile sendfile = new TransferFile("파일보내기", null, id);
			sendfile.showFrame();
		}
	}
}