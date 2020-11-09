package com.jyeory.chat.common;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class Room {
	/*-----------------------------------------------------
	 *각 방에 입장하는 유저들의 커넥션을 등록
	 * 이는 각 방에 입장한 유저들끼리의 채팅을 위한 것.
	 *-----------------------------------------------------*/
	public Vector<ClientConnection> userList = new Vector<ClientConnection>();
	/*-----------------------------------------------------
	 * 방을 만들거나 어느 방에 들어갈때는 여기에도 추가.
	 * 대기실을 벗어나게 되는 경우에 사용된다. 
	 *-----------------------------------------------------*/	
	public static Map<String, ClientConnection> user_In_room = new Hashtable<String, ClientConnection>();
	/*-----------------------------------------------------
	 * 대기실에 있는 유저들을 따로 Map에 저장.
	 * 따로 저장하지 않고 한번에 하면 접속자 목록이 섞이게 된다. 
	 *-----------------------------------------------------*/
	public static Map<String, ClientConnection> waituser = new Hashtable<String, ClientConnection>();
	/*-----------------------------------------------------
	 * 	방장의 이름과 커넥션을 저장하는 Map
	 *  이 Map은 단 1개의 공간만 있으면 된다.
	 *-----------------------------------------------------*/
	public static Map<String, ClientConnection> roomchief = new Hashtable<String, ClientConnection>(); 
	
	private String roomName;
	static String chief; 				//방장이름
	public Room(String roomName) {
		this.roomName = roomName;
	}
	/*-----------------------------------------------------
	 * 각 방에 입장한 유저의 커넥션을 저장함.
	 *-----------------------------------------------------*/
	public void addUser(ClientConnection user){
		userList.add(user);
	}
	
	/*-----------------------------------------------------
	 * 각 방에서 퇴장한 유저들의 커넥션을 제거함. 
	 *-----------------------------------------------------*/
	public void removeUser(ClientConnection user){
		userList.remove(user);
	}
	/*-----------------------------------------------------
	 * 각 방에 있는 유저들에게만 정보를 전송한다.
	 *-----------------------------------------------------*/
	public void broadCast(String msg){
		for(ClientConnection user : userList){
			user.sendMsg(msg);
		}
	}
	/*-----------------------------------------------------
	 * 		방장을 추가한다.	(이름, 접속정보)
	 *-----------------------------------------------------*/
	public void addchief(String name, ClientConnection client){
		roomchief.put(name, client);
	}
	/*-----------------------------------------------------
	 * 		방장이 나가게 되면 방장을 삭제한다.
	 *-----------------------------------------------------*/
	public void removechief(String name){
//		System.out.println("방장 권한 삭제 : " + name);		//확인용.
		roomchief.remove(name);
	}
	/*-----------------------------------------------------
	 *  각 접속자와 방장을 비교해서 방장의 커넥션에게만
	 *  정보를 전송한다.
	 *-----------------------------------------------------*/
	public static void getchief(String username, String roomname){
		if(roomchief.get(chief) == user_In_room.get(username)){		//비교
			user_In_room.get(username).sendMsg(MsgInfo.CHIEF+"/"+1);	//전송
		}else{
			System.out.println("없잖아!!");							//확인용
		}
	}
	public String getRoomName() {
		return roomName;
	}
	/*-----------------------------------------------------
	 * 	대기실 대기 유저에 커넥션을 등록.
	 *-----------------------------------------------------*/
	public void addWaitUser(String username, ClientConnection client){
		waituser.put(username, client);
	}
	/*-----------------------------------------------------
	 * 	방을 만들거나 방에 입장하게 되면 대기실 유저에서 삭제
	 *-----------------------------------------------------*/
	public void removeWaitUser(String username){
		waituser.remove(username);
	}
	/*-----------------------------------------------------
	 * 	방을 만들거나, 방에 이장하는 유저들을 put함.
	 *-----------------------------------------------------*/
	public void addChatUser(String username, ClientConnection client){
		user_In_room.put(username, client);
	}
	/*-----------------------------------------------------
	 *  방에서 나가는 경우에는 제거.
	 *-----------------------------------------------------*/
	public void removeChatUser(String username){
		user_In_room.remove(username);
	}
	
	/* -----------------------------------------------------------------------------
	 * 				**********		방 법 1		***********
	 * 	채팅 유저를 구하는 부분.
	 * user_In_room에는 각 방의 접속자 이름과 커넥션이 있다. 그렇기에 키셋만 받아오면
	 * 각 유저의 대화명을 가져올 수 있다. 
	 * --------------------------------------------------------------------------- */
	public void ChatUserList(String roomname){
		Set<String> lists = user_In_room.keySet();					//키셋 가져옴.				
		String data = lists.toString();								//String으로 변환.
		String list = data.substring(1, data.length()-1);			//[ ] 제거.
		String[] userlists = list.split(", ");						// , 이므로 잘라낸다.
//		System.out.println("접속자의 수 : " + userlists.length);	//확인용
		if(roomchief.size() == 0){	//roomchief에 방장이 있다면 size가 1이다.
			chief = userlists[0];
//			System.out.println("방장으로 셋팅할 유저 : " + chief);		//확인용
			addchief(userlists[0], user_In_room.get(userlists[0]));		//방장 세팅!
		}else{
//			System.out.println("방장이 이미 있다.");				//확인용
		}
		String sendlist = "";
		for(int i =0; i<userlists.length; i++){
			sendlist += userlists[i]+"/";
		}
//		System.out.println("접속자 : " + sendlist);					//확인용
		broadCast(MsgInfo.CHATUSER+"/"+sendlist+chief);	 	//각 방에 있는 유저에게만 전송.
		getchief(chief, roomname);
	}
	
	/* ----------------------------------------------------------------------------------------
	 *			***********			방 법 2		****************** 
	 * 	대기실 유저를 구하는 부분.
	 * 그냥 키셋만 구해서 대기실에 있는 유저들에게만 전송하고 받는 측에서
	 * 키셋을 처리한 후에 화면에 출력하는 방법.
	 * ---------------------------------------------------------------------------------------- */
	public void WaitUserList(){
		broadCast(MsgInfo.USERLIST+"/"+waituser.keySet());
	}
}
