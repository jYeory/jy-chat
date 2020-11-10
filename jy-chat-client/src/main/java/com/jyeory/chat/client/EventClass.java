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

import com.jyeory.chat.client.component.SelectChatUser;
import com.jyeory.chat.client.component.SendMemo;
import com.jyeory.chat.client.component.TransferFile;
import com.jyeory.chat.client.component.WaitRoom;
import com.jyeory.chat.common.MsgInfo;
/*
 * 	대기실에서의 메뉴 이벤트 처리 클래스.
 */
public class EventClass implements ActionListener {
	String received;
	FileDialog fdial;
	
	private String roomTitle;
	private Frame[] frame;
	private WaitRoom wRoom;
	
	public EventClass(String title, Frame[] frames, WaitRoom wRoom) {
		this.roomTitle = title;
		this.frame = frames;
		this.wRoom = wRoom;
	}

	public void actionPerformed(ActionEvent e) {
		String id = wRoom.getInputId().getText();
		String selectmenu = e.getActionCommand();
		/*
		 *	파일 저장 다이얼로그를 띄워서 현재 대화 내용을 저장한다. 
		 */
		if (selectmenu.equals("대화내용 저장")){
			fdial = new FileDialog(frame[0], "", FileDialog.SAVE);	
			fdial.setVisible(true);
		/*
		* 	날짜와 시각을 위해 GregorianCalendar와 DateFormat을 이용한다.
		*/
			GregorianCalendar gc = new GregorianCalendar();
			DateFormat df = DateFormat.getInstance();
			String data = df.format(gc.getTime())+"\r\n["+roomTitle+"]"+"에서의 대화내용"+"\r\n";
			// \n의 값을 \r\n으로 해야 메모장에서 엔터 기능이 이루어 진다.
			data += wRoom.getShowTextArea().getText().replaceAll("\n", "\r\n");
			BufferedWriter bw;
			try {
				try{
//				BufferedWriter를 저장 위치로 설정.
				bw = new BufferedWriter(new FileWriter(fdial.getDirectory()+"\\"+fdial.getFile()));
				bw.write(data);
				bw.close();
			}catch(FileNotFoundException file){}
			} catch (IOException e1) {e1.printStackTrace();}
			
		}else if(selectmenu.equals("닫기")){
			frame[0].dispose();
			String consoleData = "EXIT";
			SendMessage.chattingStart(consoleData);			//Exit 보내기
			System.exit(0);
			
		}else if(selectmenu.equals("전체 접속자")){
			try {
				MultiClient.sendMsg(MsgInfo.SHOWUSER, null);
			} catch (IOException e1) { e1.printStackTrace(); }
			
		}else if(selectmenu.equals("쪽지 보내기")){
			SendMemo memo = new SendMemo();
			memo.showFrame(null, id);
			
		}else if(selectmenu.equals("대화하기")){
			SelectChatUser seluser = new SelectChatUser(id);
			
		}else if(selectmenu.equals("파일 보내기")){
			TransferFile sendfile = new TransferFile("파일보내기", null, id);
			sendfile.showFrame();
		}
	}
}