package com.jyeory.chat.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.jyeory.chat.common.MsgInfo;
import com.jyeory.chat.common.Room;
import com.jyeory.chat.common.RoomManager;

public class ClientConnection implements Runnable{
	private Socket socket;
	private PrintWriter networkWriter;
	private BufferedReader networkReader;
	private String name;
	String myRoomName;
	private String roomName;
	Room room;
	
	public ClientConnection(Socket socket) {
		this.socket = socket;
		try {
			networkWriter = new PrintWriter(socket.getOutputStream());
			networkReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMsg(String msg){
		networkWriter.print(msg + "\n");
		networkWriter.flush();
	}
/*==================================================================
 *			NEW에서는 waitRoom으로 오게 된다.
 *			여기서는 Main 대화방의 주소를 가져와서 작업을 하게 된다.
 * ==================================================================*/
	private void waitRoom(String username){
		RoomManager.addallUserList(name, this);								//전체 유저에 자기 자신 추가!! (쪽지 때문에!);
		Room waitroom = RoomManager.getRoom("Main");							//Main의 주소를 가져옴
		waitroom.addUser(this);													//Room에 자기 자신을 추가
//		System.out.println("대기실에서의 userlist : " + waitroom.userList.size());
		waitroom.addWaitUser(username, this);									//Room에 대기 유저에 자기 자신을 추가
		waitroom.broadCast(MsgInfo.MAIN+"/"+name + " 님께서 " +waitroom.getRoomName()+ "에 입장하셨습니다.");
		waitroom.WaitUserList();												//대기실에 대기 유저 정보 전송
		waitroom.broadCast(MsgInfo.ROOMLIST+"/"+RoomManager.getRoomList());		//대기실에 대화방 목록 전송
	}
/*==================================================================
 *			MAKEROOM에서는 makeRoom으로 오게 된다.
 *		여기서는 Main에서의 작업과 만드려고 하는 방에 대한 작업을 한다.
 * ==================================================================*/
	private void makeRoom(String roomName, String username){
		Room waitroom = RoomManager.getRoom("Main");		//메인의 주소를 가져옴
		waitroom.removeUser(this);							//Main에서 자기 자신 제거.
//		System.out.println("방 만들기에서의 userlist : " + waitroom.userList.size());
		waitroom.removeWaitUser(username);					//대기실 채팅 유저목록에서 자기 자신 삭제
		waitroom.WaitUserList();							//대기실에  대기 유저 정보 전송
		waitroom.broadCast(MsgInfo.ROOMLIST+"/"+RoomManager.getRoomList());			//대화방 리스트 갱신
		RoomManager.makeRoom(roomName);						//RoomManager에 방 생성
//		System.out.println(name + "님께서 " + roomName +"방을 제작합니다.");		//서버 확인용.
		this.sendMsg(MsgInfo.MAKEROOM+"/"+roomName);		//자기 자신에게 방 만들라는 정보 전송
		room = RoomManager.getRoom(roomName);				//만든 방의 주소를 가져옴
		room.addUser(this);									//만든 방의 유저 목록에 자기 자신 등록
		room.addChatUser(username, this);					//만든 방의 채팅 유저 목록에 자기 자신 등록
		room.ChatUserList(roomName);						//만든 방에 채팅 유저(자기 자신) 리스트 전송
		room.broadCast(roomName+"/"+"["+name+"]" + " 님께서 "+"["+room.getRoomName()+"]"+ " 대화방에 입장하셨습니다.");
		room.broadCast(roomName+"/"+"["+Room.chief+"]"+"님이 방장입니다.");
	}
	/*==================================================================
	 *			ENTERROOMM에서 enterRoom으로 오게 된다.
	 *		여기서는 대기실에서의 작업과 접속하려고 하는 방에 대한 작업을 한다.
	 * ==================================================================*/
	private void enterRoom(String roomName){
		Room waitroom = RoomManager.getRoom("Main");		//대기실의 주소를 가져옴
		waitroom.removeUser(this);							//대기실의 유저 목록에서 자기 자신 제거
//		System.out.println("입장 눌렀을때의 uesrlist 사이즈 : " + waitroom.userList.size());		//확인용
		waitroom.removeWaitUser(name);						//대기실 대기 유저에서 자기 자신 제거.
		waitroom.WaitUserList();							//대기실에 대기 유저 정보 전송.
		this.sendMsg(MsgInfo.ENTER+"/"+roomName);			//자기 자신에게 정보 전송
		room = RoomManager.getRoom(roomName);				//입장하려는 방의 주소를 가져옴
		room.addUser(this);									//입장하는 방의 유저 목록에 자기 자신 등록
		room.addChatUser(name, this);						//입장하는 방의 채팅 유저 목록에 자기 자신 등록
		room.broadCast(roomName+"/"+"["+Room.chief+"]"+"님이 방장입니다.");
		room.broadCast(roomName+"/"+"["+name+"]" + " 님께서 "+"["+room.getRoomName()+"]"+ " 대화방에 입장하셨습니다.");
		room.ChatUserList(roomName);						//입장한 유저들에게 현재 방의 채팅 유저 리스트 전송
	}
	/*==================================================================
	 *			GOWAIT에서는 goWaitRoom으로 오게 된다.
	 *		여기서는 현재 방의 작업과 대기실(Main의 작업을 하게 된다.)
	 * ==================================================================*/	
	private void goWaitRoom(String username, String preRoom){
		room = RoomManager.roomMap.get(preRoom);			//이전 방의 주소를 가져옴.
		room.removeChatUser(name);							//이전방의 채팅 유저 목록에서 자기 자신 제거.
		room.removechief(name);								//방장 이었다면 방장 삭제.
		//대화방의 사용자가 0이라면 방이 없어진것이기 때문에 맵에서 삭제해야 한다.
		if(room.user_In_room.size() == 0){
			RoomManager.removeRoom(preRoom);				//맵에서 방 삭제
			System.out.println("방 삭제 완료");				//서버 확인용
		}else{
			room.broadCast(preRoom+"/"+"["+name+"]"+"님께서 [대기실]로 나가셨습니다.");
			room.removeUser(this);							//이전 방에서 유저 목록에서 자기 자신 삭제
			room.ChatUserList(preRoom);						//이전 방의 채팅 유저 목록 전송.
		}
		this.sendMsg(MsgInfo.GOWAIT+"/"+ name);				//자기 자신에게 정보를 전송
		Room waitroom = RoomManager.roomMap.get("Main");
		waitroom.addUser(this);								//대기실 유저에 자기 자신을 등록
		waitroom.addWaitUser(username, this);				//대기실 대기 유저 목록에 자기 자신 등록.
		waitroom.broadCast("Main"+"/"+name + " 님께서 " +waitroom.getRoomName()+ "에 입장하셨습니다.");
		waitroom.WaitUserList();							//대기실 대기 유저 목록 전송
		waitroom.broadCast(MsgInfo.ROOMLIST+"/"+RoomManager.getRoomList());		//대화방 리스트 갱신
	}
	/*==================================================================
	 *			SENDMEMO에서는 sendMemo로 오게 된다.
	 *		여기서는 쪽지의 발신자, 수신자, 내용을 가지고 작업을 한다.
	 * ==================================================================*/
	private void sendMemo(String receiveuser, String senduser, String text){
		System.out.println("받는 사람 : " + receiveuser);		//확인용
		System.out.println("보내는 사람 : " + senduser);		//확인용
		System.out.println("내용 : " + text);					//확인용
		RoomManager.whisper(receiveuser, senduser, text);
		/*
		 *  whisper(받는사람, 보내는 사람, 내용)이다.
		 */
	}
	/*==================================================================
	 *			MANTOMAN에서는 mantomanChat로 오게 된다.
	 *			대화상대에게 메세지를 전달하는 부분이다.
	 * ==================================================================*/	
	private void mantomanChat(String receiveuser, String senduser, String text){
		System.out.println("받는 사람 : " + receiveuser);		//확인용
		System.out.println("보내는 사람 : " + senduser);		//확인용
		System.out.println("내용 : " + text);					//확인용
		RoomManager.mantomanChat(receiveuser, senduser, text);
	}
	/*==================================================================
	 *			MAKECHAT에서는 makeChat으로 오게 된다.
	 *		상대방에게 대화 여부를 묻게 되고 자신의 대화창을 만든다.
	 * ==================================================================*/		
	private void makeChat(String receiveuser, String senduser){
		this.sendMsg(MsgInfo.MAKECHAT+"/"+receiveuser+"/"+senduser);
	}
	/*==================================================================
	 *				KICK에서는 kick()으로 오게 된다.
	 *		강퇴 당하는 유저의 client주소를 가져와서 작업을 한다.
	 * ==================================================================*/			
	private void kick(String target, String roomname){
		ClientConnection kicktarget = Room.user_In_room.get(target);		//강퇴 유저의 clinet주소 가져오기
		kicktarget.goWaitRoom(target, roomname);		//강퇴 유저의 clinet주소를 이용해 goWaitRoom호출
	}
	/*==================================================================
	 *				ALLUSER에서는 allUser()으로 오게 된다.
	 *		선택할 수 있는 모든 clinet의 주소에 list를 전송한다.
	 * ==================================================================*/		
	private void allUser(ClientConnection client){
		String idlist = RoomManager.getIDlist();
//		System.out.println(idlist);			//확인용
		this.sendMsg(MsgInfo.SELUSER+"/"+idlist);
	}
	/*==================================================================
	 *				SHOWUSER에서는 showUser()으로 오게 된다.
	 *		모든 접속자 list를 요청한 clinet의 주소에 list를 전송한다.
	 * ==================================================================*/		
	private void showUser(ClientConnection client){
		String idlist = RoomManager.getIDlist();
//		System.out.println(idlist);			//확인용
		this.sendMsg(MsgInfo.SHOWUSER+"/"+idlist);
	}
	
	public void run() {
		observeClientMessage();
	}

	private void observeClientMessage(){
		try{
			String msg;
			myRoomName = "Main"; 
			while( (msg = networkReader.readLine()) != null){
//				System.out.println(name + " 에게서 온 정보 " + msg);
				String parsingData[] = msg.split("/");
	/*==================================================================
	 *			N	E	W       -------->>		waitRoom() 메소드로..
	 * ==================================================================*/
				if(MsgInfo.NEW.equals( parsingData[0] )){
					this.name = parsingData[1];
					waitRoom(name);
	/*==================================================================
	 *			E	X	I	T   -------->>		break로 while문 종료시킴.
	 * ==================================================================*/
				}else if(MsgInfo.EXIT.equals( parsingData[0] )){
					RoomManager.removeallUSerList(name);		//전체 유저에서 자기 자신 재거.
					room = RoomManager.getRoom("Main");		
					room.removeWaitUser(name);					//대기 유저에서 자기 자신 제거.
					room.removeUser(this);						//Room에 등록된 자기 자신 제거.
//					System.out.println(name + " 종료시도");		//확인용
					room.broadCast(myRoomName+"/"+name + " 님께서 종료하셨습니다.");
					room.WaitUserList();						//대화방 유저 리스트 갱신.
					break;
	/*==================================================================
	 *						C	H	A	T
	 * ==================================================================*/				
				}else if(MsgInfo.CHAT.equals( parsingData[0] )){
					// [CHAT]/방이름/내용!!
					//현재 자신이 있는 방 이름을 찾아서 대화를 주고 받음.
					myRoomName = parsingData[1];		//현재 방 저장
					String sendtext = "";
					try{
						sendtext = parsingData[2];		//공백이면 Exception 발생!!
					}catch(ArrayIndexOutOfBoundsException arr){
						sendtext=" ";					//빈칸으로 대체한다.
					}
//					System.out.println("현재 있는 방 : " + myRoomName);		//확인용
					room = RoomManager.roomMap.get(myRoomName);
					room.broadCast(myRoomName+"/"+name + " : " + sendtext);
	/*==================================================================
	 *				MAKEROOM	-------->>		makeRoom()메소드로..
	 * ==================================================================*/
				}else if(MsgInfo.MAKEROOM.equals( parsingData[0])){
					RoomManager.makeRoom(parsingData[1]);
					roomName = parsingData[1];
					myRoomName = roomName;
					makeRoom(roomName, name);
		/*==================================================================
		 *			ENTER		-------->>		enterRoom()메소드로.		
		 * ==================================================================*/
				}else if(MsgInfo.ENTER.equals( parsingData[0])){
					roomName = parsingData[1];
					enterRoom(roomName);
		/*==================================================================
		 *			GOWAIT		-------->>		goWaitRoom()메소드로..
		 * ==================================================================*/
				}else if(MsgInfo.GOWAIT.equals( parsingData[0]) ){
					//[GOWAIT]/대화명/현재 대화방 형식으로 정보가 옴.
					this.name = parsingData[1];			//자신의 대화명을 name으로 설정.
					myRoomName = "Main";				//대화방을 Main으로 설정.
//					System.out.println("GoWait후의 대화방과 대화명 : " + "Main" + " : " + name);
					goWaitRoom(name, parsingData[2]);
		/*==================================================================
		 *			SENDMEMO	-------->>		sendMemo()메소드로..
		 * ==================================================================*/
				}else if(MsgInfo.SENDMEMO.equals( parsingData[0]) ){
					// [SENDMEMO]/받는사람/보내는사람/전달할내용 의 형식임.
					String receiveid = parsingData[1];		//받는 사람
					String senduser = parsingData[2];		//보내는 사람
					String text = parsingData[3];			//내용
					sendMemo(receiveid, senduser, text);
		/*==================================================================
		 *			MAKECHAT	-------->>		makeChat()메소드로..
		 * ==================================================================*/				
				}else if(MsgInfo.MAKECHAT.equals( parsingData[0])){
					String receiveid = parsingData[1];		//받는 사람
					String senduser = parsingData[2];		//보내는 사람
					makeChat(receiveid, senduser);
		/*==================================================================
		 *			MANTOMAN	-------->>		mantomanChat()메소드로..
		 * ==================================================================*/						
				}else if(MsgInfo.MANTOMAN.equals( parsingData[0]) ){
					// [1:1]/받는사람/보내는사람/전달할내용
					String receiveid = parsingData[1];
					String senduser = parsingData[2];
					String text;
					try{
						text = parsingData[3];	//공백이 오면 Exception발생
					}catch(ArrayIndexOutOfBoundsException e){
						text = " ";
					}
					mantomanChat(receiveid, senduser, text);
		/*==================================================================
		 *			CHATQUESTION 	상대방에 대화를 할지 묻는다.
		 * ==================================================================*/				
				}else if(MsgInfo.CHATQUESTION.equals( parsingData[0]) ){
					String receiveid = parsingData[1];
					String myname = parsingData[2];
					RoomManager.askForChat(receiveid, myname);
		/*==================================================================
		 *			FILEQUESTION 	상대방에 대화를 할지 묻는다.
		 * ==================================================================*/				
				}else if(MsgInfo.FILEQUESTION.equals( parsingData[0]) ){
					int port = Integer.parseInt(parsingData[1]);
					String target = parsingData[2];
					String senduser = parsingData[3];
//					System.out.println("파일 받을 사람 : " +target);		//확인용
//					System.out.println("파일 전송하는 사람 : " + senduser);	//확인용
					RoomManager.askForFile(target, senduser, port);
		/*==================================================================
		 *			EXITCHAT 	1:1 대화에서 상대방이 닫기를 눌렀을때
		 * ==================================================================*/						
				}else if(MsgInfo.EXITCHAT.equals( parsingData[0]) ){
					String exituser = parsingData[1];
					RoomManager.removeChat(exituser);
		/*==================================================================
		 *			ADDCHAT 1:1 대화를 신청했을때 자신과 상대를 추가함.
		 * ==================================================================*/							
				}else if(MsgInfo.ADDCHAT.equals( parsingData[0]) ){
					RoomManager.addManToManUser(parsingData[1], parsingData[2]);
		/*==================================================================
		 *					KCIK 강퇴 -------> kick()메서드로
		 * ==================================================================*/					
				}else if(MsgInfo.KICK.equals( parsingData[0]) ){
					String target = parsingData[1];
					String roomname = parsingData[2];
					kick(target, roomname);
		/*==================================================================
		 *					SELUSER -------> selUser()메서드로
		 *						접속자 선택 창으로 가기
		 * ==================================================================*/						
				}else if(MsgInfo.SELUSER.equals( parsingData[0]) ){
					allUser(this);
		/*==================================================================
		 *					SHOWUSER -------> showUser()메서드로
		 *						모든 접속자 창으로 가기
		 * ==================================================================*/				
				}else if(MsgInfo.SHOWUSER.equals( parsingData[0]) ){
					showUser(this);
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			exitClient();
		}
	}
/* ----------------------------------------------------------------------------------------
 * 							EXIT 명령 처리후 이 부분이 출력됨.
 * ----------------------------------------------------------------------------------------	*/	
	private void exitClient(){
		room = RoomManager.getRoom("Main");			//메인의 주소를 가져옴
		System.out.println("남은유저" + room.userList.size());		//확인용
			/* 자 원 정 리 */
		networkWriter.close();
		try {networkReader.close();} catch (IOException e1) {}
		try {socket.close();} catch (IOException e) {}
	}
}