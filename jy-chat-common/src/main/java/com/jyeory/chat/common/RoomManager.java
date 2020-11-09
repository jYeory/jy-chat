package com.jyeory.chat.common;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class RoomManager {
	//전체 접속자!!
	public static Vector<ClientConnection> allUserList = new Vector<ClientConnection>();
	
	/*
	 *  접속자!! 이부분은 쪽지나 1:1 대화를 위해 필요로 한다.
	 *  대화명으로 접속정보를 가져와야 하기 때문에 Map(아이디, 접속정보) 형식으로 등록한다.
	 */
	public static Map<String, ClientConnection> connectedUser = new Hashtable<String, ClientConnection>();
	
	/*
	 * 아래는 방을 만들고 지울때 사용하는 맵이다.
	 * 등록은 put(방이름, 방정보를 가진 벡터) 제거할때는 remove(방이름)으로 사용한다.
	 */
	public static Map<String, Room> roomMap = new Hashtable<String, Room>();
	
	/*
	 * 1:1대화를 요청한 유저들의 커넥션.
	 */
	public static Vector<ClientConnection> user = new Vector<ClientConnection>();
	
	/*-----------------------------------------------------
	 * 			방을 만들고 Map에 집어 넣기.
	 *-----------------------------------------------------*/
	public static void makeRoom(String roomName){
		Room newRoom = new Room(roomName);
		roomMap.put(roomName, newRoom);
	}
	/*-----------------------------------------------------
	 * 			방이름을 리턴.
	 *-----------------------------------------------------*/
	public static Room getRoom(String roomName){		
		return roomMap.get(roomName);
	}
	/*-----------------------------------------------------
	 * 			방을 지운다. remove(방이름)
	 *-----------------------------------------------------*/
	public static void removeRoom(String roomName){
		roomMap.remove(roomName);
	}
	/*-----------------------------------------------------
	 *	접속하는 유저를 connectedUser(Map)에 put시킨다. 
	 *-----------------------------------------------------*/
	public static void addallUserList(String username, ClientConnection client){
		connectedUser.put(username, client);
	}
	/*-----------------------------------------------------
	 *	접속 종료시 connectedUser(Map)에서 remove한다.
	 *-----------------------------------------------------*/
	public static void removeallUSerList(String username){
		connectedUser.remove(username);
	}
	/*-----------------------------------------------------
	 * 			ID-List를 가져오는 부분.
	 *-----------------------------------------------------*/	
	public static String getIDlist(){
		Set<String> lists = connectedUser.keySet();			//키셋을 가져온다.
		String data = lists.toString();						//String으로 변환
		String list = data.substring(1, data.length()-1);	//[ 와 ]를 빼고.
		String[] userlists = list.split(", ");				// aaa, bbb 의 구분은 , 이므로 잘라낸다.
		String sendlist = "";
		for(int i =0; i<userlists.length; i++){
			sendlist += userlists[i]+"/";					//유저들을 추가
		}
		return sendlist;
	}
	/*-----------------------------------------------------
	 * 			쪽지에 관련된 기능!
	 *-----------------------------------------------------*/
	public static boolean whisper(String receiveuser, String senduser, String msg){
		ClientConnection user = findUser(receiveuser);		//받는 사람의 커넥션을 찾는다.
		if(user == null){
			return false;
		}
		user.sendMsg(MsgInfo.SENDMEMO+"/"+receiveuser+"/"+senduser+"/"+msg);
		return true;
	}
	/*-----------------------------------------------------
	 * 	1:1대화를 하는 두 유저의 커넥션을 찾아서 등록시킨다.
	 *-----------------------------------------------------*/
	public static void addManToManUser(String receiveuser, String senduser){
		user.add(findUser(receiveuser));
		user.add(findUser(senduser));
	}
	/*-----------------------------------------------------
	 * 	user에는 1:1대화만 하는 두명이 있기에 그 두명에게만
	 *  정보를 전송하면 된다.
	 *-----------------------------------------------------*/
	public static void mantomanChat(String receiveuser, String senduser, String msg){
		for(int i=0; i<user.size(); i++){
			user.get(i).sendMsg(MsgInfo.MANTOMAN+"/"+receiveuser+"/"+senduser+"/"+msg);
//			System.out.println(user.get(i)+"에게 정보를 보냈다.");	//확인용
		}
	}
	/*-----------------------------------------------------
	 * 1:1 대화에서 한명이라도 나가게 되면 remove한다.
	 *-----------------------------------------------------*/
	public static void removeChat(String username){
		user.remove(findUser(username));
//		System.out.println("user의 사이즈 : " + user.sㅋize());
	}
	/*-----------------------------------------------------
	 * 	1:1 대화를 신청하는 부분.
	 *  받는 사람의 커넥션을 찾아서 요청한다.
	 *-----------------------------------------------------*/
	public static void askForChat(String receiveuser, String senduser){
		ClientConnection user = findUser(receiveuser);			
			user.sendMsg(MsgInfo.CHATQUESTION+"/"+receiveuser+"/"+senduser);
//		System.out.println("1:1대화 요청 완료.");		//확인용
	}
	/*-----------------------------------------------------
	 * 	파일 수신 여부를 요청하는 부분.
	 * 역시 받는 사람의 커넥션을 찾아서 메세지를 전송한다.
	 *-----------------------------------------------------*/
	public static void askForFile(String targetuser, String senduser, int port){
		ClientConnection user = findUser(targetuser);
			user.sendMsg(MsgInfo.FILEQUESTION+"/"+port+"/"+targetuser+"/"+senduser);
	}
	/*-----------------------------------------------------
	 * 	방에 관한 정보를 리턴한다.
	 *-----------------------------------------------------*/
	public static String getRoomList(){
		StringBuffer sb = new StringBuffer();
		for(Room room : roomMap.values()){
			sb.append( room.getRoomName() + "/");
		}
		return sb.toString(); 
	}
	/*-----------------------------------------------------
	 * 맵이기 때문에 이름만 입력하면 자동적으로 대화명에 
	 * 맞는 접속정보(키)가 나오게 된다.	
	 *-----------------------------------------------------*/
	private static ClientConnection findUser(String userName){
		return connectedUser.get(userName);
	}
}
