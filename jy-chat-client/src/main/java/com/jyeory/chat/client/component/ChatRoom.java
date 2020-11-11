package com.jyeory.chat.client.component;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

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

import com.jyeory.chat.client.ChatEventClass;
import com.jyeory.chat.client.MultiClient;
import com.jyeory.chat.client.MyMenuBar;
import com.jyeory.chat.client.SendMessage;
import com.jyeory.chat.common.MsgInfo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoom extends JFrame implements KeyListener, ActionListener{
	public static JTextArea showText = new JTextArea(10, 20);
	public static JLabel inputid;
	public static String title;
	
	private int chiefCode = 0;
	private List idlist = new List();
	JTextField inputText = new JTextField();
	JButton exit = new JButton("나가기");
	
	String id;
	JPopupMenu chatPopup;  
	JMenuItem sendMemo, oneOnOne, transferFile, kick, change;
	JScrollPane chatJsp;
	JScrollBar chatJsb;
	JScrollPane userJsp;
	JScrollBar userJsb;
	
	
	MyMenuBar mb = new MyMenuBar(this);
	
	public ChatRoom(String title){
		super("당신이 키보드를 두들기고 있는 방은 : "+ title);
		this.title = title;
	}
	
	public void showFrame(String name){
		ActionListener ac = new ChatEventClass(title, this.getFrames());

		try {
			//메뉴바 추가(MyMenuBar클래스 이용)
			mb.addMenus(new String[]{"파일", "접속자", "메신저"});
			mb.addMenuItems(0, new String[]{"대화내용 저장"});
			mb.addMenuItems(1, new String[]{"전체 접속자"});
			mb.addMenuItems(2, new String[]{"쪽지 보내기", "대화하기", "파일 보내기"});
		} catch (UnsupportedEncodingException ue) {
			ue.printStackTrace();
		}
		mb.addActionListener(ac);
		
		id = name;
		inputid = new JLabel(id);
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		inputText.addKeyListener(this);
		
		//	오른쪽 버튼을 클릭 했을때.
		chatPopup=new JPopupMenu();
		sendMemo=new JMenuItem("쪽지보내기");  		sendMemo.addActionListener(this);
		oneOnOne=new JMenuItem("1:1대화");   		oneOnOne.addActionListener(this);
		transferFile=new JMenuItem("파일전송");  	transferFile.addActionListener(this);
		kick = new JMenuItem("강퇴");				kick.addActionListener(this);
		
		//스크롤바 추가
		chatJsp = new JScrollPane(showText);
		chatJsp.setWheelScrollingEnabled(true);
		chatJsb = chatJsp.getVerticalScrollBar();  
		
		JPanel left = new JPanel();
			left.setLayout(new BorderLayout());
			left.add(inputid, "North");
			left.add(chatJsp, "Center");
			left.add(inputText, "South");
			left.setBorder(loweredetched);
			
		//스크롤바 추가
		userJsp = new JScrollPane(idlist);
		userJsp.setWheelScrollingEnabled(true);
		userJsb = userJsp.getVerticalScrollBar();
			
		JPanel right = new JPanel();
			right.setLayout(new GridLayout(1,1));
			right.add(userJsp);
			right.setBorder(loweredetched);
		
		idlist.addMouseListener(
				new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if(e.getButton() == 3){
//					System.out.println("방장 코드 : " + chiefcode);		
					if( chiefCode == 1 ){		//방장이면 전부다 출력.
						chatPopup.add(sendMemo);
						chatPopup.add(oneOnOne);
						chatPopup.add(transferFile);
						chatPopup.add(kick);
						chatPopup.show(e.getComponent(), e.getX(), e.getY());
					}else{						//아니면 기본적인것만.
						chatPopup.add(sendMemo);
						chatPopup.add(oneOnOne);
						chatPopup.add(transferFile);
						chatPopup.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			}
		});
		
		JPanel south = new JPanel();
			south.setLayout(new FlowLayout());
			south.add(exit);
			exit.addActionListener(this);
			south.setBorder(loweredetched);
			
		setBounds(100, 100, 500, 421);
		setLayout(new BorderLayout());
		add(left, "Center");
		add(right, "East");
		add(south, "South");
		setVisible(true);
		
		// X를 눌렀을때는 현재 대화방에서 나가기.
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				setVisible(false);
				System.out.println("ChatRoom Frame 종료");
				try {
					MultiClient.sendMsg(MsgInfo.GOWAIT, inputid.getText()+"/"+title);
				} catch (IOException e1) { e1.printStackTrace(); }
			}
		});
	}
	
	public void keyPressed(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {
		if(e.getKeyChar() == '\n'){
			chatJsb.setValue(chatJsb.getMaximum());			//엔터를 치켠 스크롤바도 밑으로 내려오게 하는 것.
			String consoleData = title+"/"+inputText.getText();		//입력한걸 저장
			inputText.setText("");							//저장후 지워야 함.
			SendMessage.chattingStart(consoleData);			//채팅 시작
		}
	}
	public void actionPerformed(ActionEvent e) {
		String receiveid = idlist.getSelectedItem();
		if(e.getActionCommand().equals("나가기")){
			this.setVisible(false);
//			System.out.println("ChatRoom Frame 종료");		//확인
			try {
				MultiClient.sendMsg(MsgInfo.GOWAIT, inputid.getText()+"/"+title);
			} catch (IOException e1) { e1.printStackTrace(); }
		}
/*==================================================================
 *				 (오른쪽 버튼) 쪽지 보내기 눌렀을때
 * ==================================================================*/
		else if(e.getSource()==sendMemo){
			if(receiveid == null){		//받는 사람이 없을때
				JOptionPane.showMessageDialog(null, "대상을 선택하세요.");
			}else if(receiveid.startsWith("[방장]")){		//[방장]이란 글자 떼어내고!
				receiveid = receiveid.substring(4);
			}else{
				SendMemo memo = new SendMemo();
				memo.showFrame(receiveid, id);
			}
		}
/*==================================================================
 *				 (오른쪽 버튼) 1:1대화 눌렀을때
 * ==================================================================*/
		else if(e.getSource()==oneOnOne){
			if(receiveid == null){		//받는 사람이 없을때
				JOptionPane.showMessageDialog(null, "대상을 선택하세요.");
			}else if(receiveid.startsWith("[방장]")){
				receiveid = receiveid.substring(4);		//[방장]이란 글자 떼어내고!
			}else{
				try {
					MultiClient.sendMsg(MsgInfo.ADDCHAT, receiveid+"/"+id);		//1:1 채팅 유저 둘 추가
					MultiClient.sendMsg(MsgInfo.MAKECHAT, receiveid+"/"+id);	//[MAKECHAT]/받는사람/자기대화명
					MultiClient.sendMsg(MsgInfo.CHATQUESTION, receiveid+"/"+id);	//[QUESTION]/받는사람/자기대화명
				} catch (IOException e1) { e1.printStackTrace();	}
			}
		}
/*==================================================================
 *				 (오른쪽 버튼) 파일 전송 눌렀을때
 * ==================================================================*/
		else if(e.getSource()==transferFile){
			if(receiveid == null){		//받는 사람이 없을때
				JOptionPane.showMessageDialog(null, "대상을 선택하세요.");
			}else if(receiveid.startsWith("[방장]")){
				receiveid = receiveid.substring(4);		//[방장]이란 글자 떼어내고!
			}else{
				TransferFile sendfile = new TransferFile("파일보내기", receiveid, id);
				sendfile.showFrame();
			}
		}
/*==================================================================
 *				 방장이 (오른쪽 버튼) 강퇴 눌렀을때
 * ==================================================================*/
		else if(e.getSource()==kick){
			if(receiveid == null){		//받는 사람이 없을때
				JOptionPane.showMessageDialog(null, "대상을 선택하세요.");
			}else if(receiveid.startsWith("[방장]")){
				JOptionPane.showMessageDialog(null, "자신을 강퇴 시킬 수 없잖아!!");
			}else{
				try {
					MultiClient.sendMsg(MsgInfo.KICK, receiveid+"/"+title);		// 강퇴 명령어 전송
				} catch (IOException e1) { e1.printStackTrace(); }		
			}
		}
	}
}
