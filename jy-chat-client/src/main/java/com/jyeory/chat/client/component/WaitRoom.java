package com.jyeory.chat.client.component;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.jyeory.chat.client.EventClass;
import com.jyeory.chat.client.MultiClient;
import com.jyeory.chat.client.MyMenuBar;
import com.jyeory.chat.client.SendMessage;
import com.jyeory.chat.common.MsgInfo;
import com.jyeory.chat.common.Room;
import com.jyeory.chat.common.RoomManager;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WaitRoom extends JFrame implements KeyListener, ActionListener{
	private JTextArea showTextArea = new JTextArea(10, 20);
	private JLabel inputId;
	private String id;
	
	JTextField inputText = new JTextField();
	
	private List roomList = new List(8);
	private List idlist = new List(8);
	
	JButton make = new JButton("방 만들기");
	JButton enter = new JButton("들어가기");
	JButton exit = new JButton("나가기");
	JPopupMenu waitpopup;  
	JMenuItem sendmemo, mantoman, transferFile;
	String roomtitle;
	JScrollPane waitJsp;
	JScrollPane userListJsp;
	JScrollPane roomListJsp;
	JScrollBar waitJsb;
	JScrollBar userListJsb;
	JScrollBar roomListJsb;

	MyMenuBar mb = new MyMenuBar(this);
	
	public WaitRoom(String title){
		super(title);
		roomtitle = title;
	}

	//대기실 클라이언트
	public void showFrame(String name){
		ActionListener ac = new EventClass("대기실", this.getFrames(), this);
		/*
		 * MyMenuBar 클래스의 메소드를 이용해 메뉴 추가.
		 */
		mb.addMenus(new String[]{"파일", "접속자", "메신저"});
		mb.addMenuItems(0, new String[]{"대화내용 저장",null, "닫기"});
		mb.addMenuItems(1, new String[]{"전체 접속자"});
		mb.addMenuItems(2, new String[]{"쪽지 보내기", "대화하기", "파일 보내기"});
		mb.addActionListener(ac);

		this.id = name;
		inputId = new JLabel(id);
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		inputText.addKeyListener(this);

		waitpopup=new JPopupMenu();
		sendmemo=new JMenuItem("쪽지보내기");  		sendmemo.addActionListener(this);
		mantoman=new JMenuItem("1:1대화");   		mantoman.addActionListener(this);
		transferFile=new JMenuItem("파일전송");  	transferFile.addActionListener(this);

		//대기실 채팅창에 스크롤바 추가.
		waitJsp = new JScrollPane(showTextArea);
		waitJsp.setWheelScrollingEnabled(true);
		waitJsb = waitJsp.getVerticalScrollBar();  
		
		JPanel left = new JPanel();
		left.setLayout(new BorderLayout());
		left.add(inputId, "North");
		left.add(waitJsp, "Center");
		left.add(inputText, "South");
		left.setBorder(loweredetched);
		
		//접속자 스크롤바 추가.
		userListJsp = new JScrollPane(idlist);
		userListJsp.setWheelScrollingEnabled(true);
		userListJsb = userListJsp.createVerticalScrollBar();
		
		JPanel right_up = new JPanel();
		right_up.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "접속자"));
		right_up.add(userListJsp);
		
		//오른쪽 버튼 클릭했을때
		idlist.addMouseListener(
				new MouseAdapter(){
					public void mouseClicked(MouseEvent e){
						if(e.getButton() == 3){
							waitpopup.add(sendmemo);
							waitpopup.add(mantoman);
							waitpopup.add(transferFile);
							waitpopup.show(e.getComponent(), e.getX(), e.getY());
						}
					}
				});

		//방 목록 스크롤바 추가.
		roomListJsp = new JScrollPane(roomList);
		roomListJsp.setWheelScrollingEnabled(true);
		roomListJsb = roomListJsp.createVerticalScrollBar();
		
		JPanel right_down = new JPanel(); 
		right_down.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "대화방"));
		right_down.add(roomListJsp);

		JPanel right = new JPanel();
		right.setBorder(loweredetched);
		right.setLayout(new GridLayout(2,1));
		right.add(right_up);	
		right.add(right_down);
		
		JPanel south = new JPanel();
		south.setLayout(new FlowLayout());
		south.add(make);	
		south.add(enter);	
		south.add(exit);
		enter.addActionListener(this);
		make.addActionListener(this);
		exit.addActionListener(this);
		
		south.setBorder(loweredetched);
		setBounds(100, 100, 500, 421);
		setLayout(new BorderLayout());
		add(left, "Center");
		add(right, "East");
		add(south, "South");
		setVisible(true);

		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				dispose();
				String consoleData = "EXIT";
				SendMessage.chattingStart(consoleData);			//Exit 보내
			}
		});
	}

	//키 이벤트
	public void keyPressed(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {
		if(e.getKeyChar() == '\n'){
			waitJsb.setValue(waitJsb.getMaximum());
			String consoleData = "Main"+"/"+inputText.getText();		//입력한걸 저장
			inputText.setText("");							//저장후 지워야 함.
			SendMessage.chattingStart(consoleData);			//채팅 시작
		}
	}
	
	public void closeWindow() {
		this.dispose();
	}

	//버튼에 대한 이벤트
	public void actionPerformed(ActionEvent ac) {
		String receiveid = idlist.getSelectedItem();
		if(ac.getActionCommand().equals("방 만들기")){
			final JFrame makeroom = new JFrame();
			final JTextField roomname;	//방이름
			
//			dispose();
//			dispose();
			
			Button ok;		//확인
			JLabel title = new JLabel("방이름을 입력하세요");
			roomname = new JTextField(10);
			ok = new Button("OK");
			ok.addActionListener(this);
			
			ok = new Button("OK");
			ok.addActionListener(this);
			
			makeroom.add(title, "North");
			Panel southPanel = new Panel();
			southPanel.add(roomname);	southPanel.add(ok);
			makeroom.add(southPanel, "Center");
			makeroom.setBounds(500, 300, 200, 120);
			makeroom.setVisible(true);
			
			/*==================================================================
			 *				 방 이름 입력하고 Ok누를때
			 * ==================================================================*/
			ok.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(e.getActionCommand().equals("OK")){
						closeWindow();
						makeroom.dispose();	//이 창은 없어져야 한다.
						Room room = RoomManager.roomMap.get("Main");
						System.out.println(room);
						try {
							MultiClient.sendMsg(MsgInfo.MAKEROOM, roomname.getText());
						} catch (IOException e1) { e1.printStackTrace(); }
					}
				}
			});
			
			/*==================================================================
			 *				 방 클릭하고 들어가기누를때
			 * ==================================================================*/
		}else if(ac.getActionCommand().equals("들어가기")){
			String roomname = roomList.getSelectedItem();
			try{
				if(roomname == null){		//방이 없는데 없는걸 선택해서 들어가기 누르는걸 방지.
					JOptionPane.showMessageDialog(null, "선택된 방이 없습니다. 방을 만드세요.");
				}else{
					this.setVisible(false);
					MultiClient.sendMsg(MsgInfo.ENTER, roomname);
				}
			}catch(IOException e1) { e1.printStackTrace(); }
			/*==================================================================
			 *				 나가기 눌렀을때
			 * ==================================================================*/
		}else if(ac.getActionCommand().equals("나가기")){
			dispose();
			String consoleData = "EXIT";
			SendMessage.chattingStart(consoleData);			//Exit 보내기
			System.exit(0);
		}
		/*==================================================================
		 *				 (오른쪽 버튼) 쪽지 보내기 눌렀을때
		 * ==================================================================*/
		else if(ac.getSource()==sendmemo){
			SendMemo memo = new SendMemo();
			if(receiveid == null){		//받는 사람이 없을때
				memo.showFrame(null, id);
			}else{
				memo.showFrame(receiveid, id);
			}
		}
		/*==================================================================
		 *				 (오른쪽 버튼) 1:1대화 눌렀을때
		 * ==================================================================*/
		else if(ac.getSource()==mantoman ){
			if(receiveid == null){		//받는 사람이 없을때
				SelectChatUser seluser = new SelectChatUser(id, this);
			}else{
				try {
					MultiClient.sendMsg(MsgInfo.ADDCHAT, receiveid+"/"+id);		//1:1 채팅 유저 둘 추가
					MultiClient.sendMsg(MsgInfo.MAKECHAT, receiveid+"/"+id);	//[MAKECHAT]/받는사람/자기대화명
					MultiClient.sendMsg(MsgInfo.CHATQUESTION, receiveid+"/"+id);	//[QUESTION]/받는사람/자기대화명
				} catch (IOException e) { e.printStackTrace();	}
			}
		}
		/*==================================================================
		 *				 (오른쪽 버튼) 파일 전송 눌렀을때
		 * ==================================================================*/
		else if(ac.getSource()==transferFile){
			TransferFile sendfile;
			if(receiveid == null){		//받는 사람이 없을때
				sendfile = new TransferFile("파일보내기", null, id);
			}else{
				sendfile = new TransferFile("파일보내기", receiveid, id);
			}
			sendfile.showFrame();
		}
	}
}
