package com.jyeory.chat.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

import com.jyeory.chat.common.MsgInfo;

class ListenerOfClient extends Thread{
	BufferedWriter networkWriter;
	BufferedReader networkReader;
	Socket socket;
	String name;
	static String[] parsingData;
	static ChatRoom startchat;
	static OneOnOneChatSend mantomanchat;
	WaitRoom waitroom;
	ListenerOfClient(BufferedWriter networkWriter, BufferedReader networkReader, Socket socket ,WaitRoom waitroom){
		this.networkWriter = networkWriter;
		this.networkReader = networkReader;
		this.socket = socket;
		this.waitroom = waitroom;
	}
	public void run() {
		try {
			String line;
			while( (line = networkReader.readLine()) != null ){
				System.out.println("서버에서 온 메세지 : " + line);
				parsingData = line.split("/");
	/*==================================================================
	 *				 대기실에 출력해야할 메세지
	 * ==================================================================*/
				if( line.startsWith(MsgInfo.MAIN)){		
					waitroom.showText.append(parsingData[1]+"\n");
				}
	/*==================================================================
	 *				 linse이 USERLIST로 시작하면..
	 * ==================================================================*/					
				else if(line.startsWith(MsgInfo.USERLIST)){		//접속자 정보
					waitroom.IDlist.removeAll();			//대기실의 IDlist 초기
					String data = parsingData[1].substring(1, parsingData[1].length()-1);
					String lists = new String();
					// Main, aaa 의 구분은 , 이므로 잘라낸다.
					String[] roomlists = data.split(", ");
					for(int i = 0; i<roomlists.length; i++){
						lists = roomlists[i];				
						waitroom.IDlist.add(lists);			//대기실의 idlist에 등록
					}
				}
	/*==================================================================
	 *				 line이 ROOMLIST로 시작하면 방 정보를 출력
	 * ==================================================================*/
				else if(line.startsWith(MsgInfo.ROOMLIST)){	
					waitroom.roomList.removeAll();
					for(int i=1; i<parsingData.length; i++){
						waitroom.roomList.add(parsingData[i]);	//list에 등록
					}
					waitroom.roomList.remove("Main");
				}
	/*==================================================================
	 *				 line이 MAKEROOM으로 시작하면 방 만들
	 * ==================================================================*/
				else if( line.startsWith(MsgInfo.MAKEROOM)){		
					// [MAKEROOM]/방이름 형식.
					startchat = new ChatRoom(parsingData[1]);
					startchat.showFrame(MultiClient.name);
				}
	/*==================================================================
	 *				 line이 ENTER으로 시작하면 방에 접속
	 * ==================================================================*/
				else if( line.startsWith(MsgInfo.ENTER)) {
					// [ENTER]/방이름 형식이므로
					startchat = new ChatRoom(parsingData[1]);
					startchat.dispose();
					startchat.showFrame(MultiClient.name);
				}
	/*==================================================================
	 *				 line이 CHATUSER로 시작하면 대화방에 출력해야 함.
	 * ==================================================================*/			
				else if( line.startsWith(MsgInfo.CHATUSER)){
			// [CHATUSER]/유저1/유저2/유저3/.....유저x/방장대화명
					startchat.IDlist.removeAll();						//대화방 ID리스트 초기화
					for(int i=1; i<parsingData.length-1; i++){			//유저1부터 유저x까지
//						System.out.print(parsingData[i]+"\t");			//확인용
//						System.out.println();							//확인용
						startchat.IDlist.add(parsingData[i]);			//list에 등록 유저1~유저x까지만
					}
					startchat.IDlist.remove(parsingData[parsingData.length-1]);		//방장대화명을 가진 유저를 지움.
					startchat.IDlist.add("[방장]"+parsingData[parsingData.length-1]);	//그리고 방장대화명을 등록.
//					System.out.println("리스트에 유저리스트 등록 완료");	//확인
				}
	/*==================================================================
	 *				 line이 GOWAIT로 시작하면.. 대기실로 가야 함.
	 * ==================================================================*/
				else if( line.startsWith(MsgInfo.GOWAIT)){
					startchat.setVisible(false);	//대화방 창 숨기기
					waitroom.setVisible(true);		//숨겼던걸 다시 보이게 함.
					waitroom.showText.setText("");
				}
	/*==================================================================
	 *				line이 SENDMEMO로 시작하면.. 쪽지 받기 임.
	 * ==================================================================*/
				else if( line.startsWith(MsgInfo.SENDMEMO)){
					String receiveuser = parsingData[1];	//받는이
					String senduser = parsingData[2];		//보내는이
					String text = parsingData[3];			//내용
					ReceiveMemo receive = new ReceiveMemo();
					receive.showFrame(receiveuser, senduser, text);		
					//받는이, 보내는이, 내용
	/*==================================================================
	 *				line이 MAKECHAT로 시작하면.. 1:1대화방 만들
	 * ==================================================================*/				
				}else if( line.startsWith(MsgInfo.MAKECHAT)){
					String receiveuser = parsingData[1];
					String senduser = parsingData[2];
//					System.out.println("MakeChat에서 받는 사람 : " + receiveuser);	//확인용
//					System.out.println("MakeChat에서 보내는 유저 : " + senduser);	//확인용
					mantomanchat = new OneOnOneChatSend(receiveuser, senduser);
					mantomanchat.showFrame();
	/*==================================================================
	 *				line이 MANTOMAN이면 1:1대화방에서의 채팅임.
	 * ==================================================================*/				
				}else if( line.startsWith(MsgInfo.MANTOMAN)){
					String receiveuser = parsingData[1];	//보내는이
					String senduser = parsingData[2];		//받는이
//					System.out.println("MANTOMAN에서 받는 사람 : " + receiveuser);	//확인용
//					System.out.println("MANTOMAN에서 보내는 유저 : " + senduser);	//확인용
					String text = parsingData[3];			//내용
					try{
						mantomanchat.showtext.append("["+senduser+"] : "+text+"\n");
						//대화를 요청한 사용자가 상대방의 응답이 있기 전에
						//대화를 입력할 경우를 위해 catch를 해야 한다.
					}catch(NullPointerException e){}
	/*==================================================================
	 *			line이 CHATQUESTION이면 상대방에게 대화 여부를 묻는다.
	 * ==================================================================*/							
				}else if( line.startsWith(MsgInfo.CHATQUESTION)){
					String receiveuser = parsingData[1];
					String myname = parsingData[2];
//					System.out.println("대화를 요청받은 유저 : " + receiveuser);	//확인용
//					System.out.println("대화를 요청한 유저 : " + myname);			//확인용
					AskForChat askchat = new AskForChat(receiveuser, myname);
					askchat.showFrame();
	/*==================================================================
	 *		line이 FILEQUESTION이면 상대방에게 파일 전송  여부를 묻는다.
	 * ==================================================================*/				
				}else if( line.startsWith(MsgInfo.FILEQUESTION) ){
					int portnum = Integer.parseInt(parsingData[1]);
					String targetuser = parsingData[2];
					String myname = parsingData[3];
//					System.out.println("파일 전송을 요청 받은 유저 : " + receiveuser);	//확인용
//					System.out.println("파일을 보내는 유저 : " + myname);			//확인용
					try {
						AskForFile askfile = new AskForFile(targetuser, myname, portnum);
						askfile.showFrame();
					} catch (Exception e) {}
	/*==================================================================
	 *				line이 CHIEF이면 자신의 방장코드를 1 또는 0으로 입력한다.
	 * ==================================================================*/					
				}else if( line.startsWith(MsgInfo.CHIEF) ){
					startchat.chiefcode = Integer.parseInt(parsingData[1]);
	/*==================================================================
	 *				line이 KICK이면 강퇴~! 당했으므로.. 대기실로..
	 * ==================================================================*/
				}else if( line.startsWith(MsgInfo.KICK) ){
					startchat.setVisible(false);	//대화방 창 숨기기
					waitroom.setVisible(true);		//숨겼던걸 다시 보이게 함.
					waitroom.showText.setText("");
				}else if( line.startsWith(MsgInfo.SHOWUSER)){
					ShowAll showuser= new ShowAll();
					for(int i = 1; i<parsingData.length; i++){
						showuser.idlist.add(parsingData[i]);
					}
	/*==================================================================
	 *				line이 SELUSER이면 접속자 선택창이 보이게
	 * ==================================================================*/			
				}else if( line.startsWith(MsgInfo.SELUSER) ){
					SelectID id = new SelectID();
					for(int i = 1; i<parsingData.length; i++){
						id.idlist.add(parsingData[i]);
					}
	/*==================================================================
	 *				 그 외에는 각 채팅방에 출력해야 됨.
	 * ==================================================================*/				
				}else{			
					startchat.showText.append(parsingData[1]+"\n");
				}
			}
			System.out.println("종료");
			networkWriter.close();
			try {networkReader.close();} catch (IOException e1) {}
			try {socket.close();} catch (IOException e) {}
		} catch (IOException e) {
			System.out.println("소켓 종료");
		}
	}
}