package com.jyeory.chat.client.component;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import com.jyeory.chat.client.MultiClient;
import com.jyeory.chat.common.MsgInfo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OneOnOneChatSend extends Frame implements KeyListener, ActionListener{
	private JTextArea showTextArea;
	
	JTextField inputid;
	JTextField inputtext;
	JLabel receivedid;
	JButton selectid;
	JButton send;
	JButton exit;
	String roomtitle;
	String senduser;
	String receiveduser;
	JScrollPane mantomanJsp;
	JScrollBar mantomanJsb;
	
	public OneOnOneChatSend(String receiveduser, String senduser){
		super(receiveduser+" 과(와)의 대화 창");
		this.senduser = senduser;				//보내는 사람
		this.receiveduser = receiveduser;		//받는 사람
	}
	
	public void showFrame(){
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		receivedid = new JLabel("대화 상대 ");
		inputid = new JTextField(13);
		showTextArea = new JTextArea(10, 20);
		inputtext = new JTextField(20);
		inputtext.addKeyListener(this);
		selectid = new JButton("선택");
		send = new JButton("보내기");
		exit = new JButton("닫기");
		
		if(receiveduser == null){
			inputid.setText("");
		}else{
			inputid.setText(receiveduser);
		}
		
		JPanel north = new JPanel();
		north.setLayout(new FlowLayout());
		north.add(receivedid); north.add(inputid);	north.add(selectid);
		north.setBorder(loweredetched);
		
		//스크롤바 추가
		mantomanJsp = new JScrollPane(showTextArea);
		mantomanJsp.setWheelScrollingEnabled(true);				//휠로도 스크롤 움직이게.
		mantomanJsb = mantomanJsp.getVerticalScrollBar(); 
		
		JPanel center = new JPanel();
		center.setLayout(new BorderLayout());
		center.add(mantomanJsp,"Center");
		center.add(inputtext, "South");
		showTextArea.setLineWrap(true);
		
		JPanel south = new JPanel();
		south.setLayout(new FlowLayout());
		south.add(send);	south.add(exit);
		send.addActionListener(this);
		exit.addActionListener(this);
		south.setBorder(loweredetched);
		
		setLayout(new BorderLayout());
		setBounds(200, 200, 300, 400);
		
		add(north, "North");
		add(center, "Center");
		add(south, "South");
		
		setVisible(true);
		
	}
	public void keyPressed(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {
		if(e.getKeyChar() == '\n'){
			mantomanJsb.setValue(mantomanJsb.getMaximum());		//enter를 치면 스크롤바가 제일 아래로 감
			System.out.println(inputtext.getText() == null);
			String consoleData = inputtext.getText();		//입력한걸 저장
			System.out.println(consoleData == "");
			if(consoleData == null){
				consoleData = " ";
			}
			inputtext.setText("");							//저장후 지워야 함.
			try {
				MultiClient.sendMsg(MsgInfo.MANTOMAN, inputid.getText()+"/"+senduser+"/"+consoleData);
			} catch (IOException e1) {
				System.out.println("1:1대화 보낼때 에러");
				e1.printStackTrace();
			}
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == send){
			mantomanJsb.setValue(mantomanJsb.getMaximum());		//보내기를 눌러도 스크롤바가 제일 아래로 감
			String consoleData = inputtext.getText();		//입력한걸 저장
			System.out.println(inputtext.getText().equals(null));
			inputtext.setText("");							//저장후 지워야 함.
			try {
				MultiClient.sendMsg(MsgInfo.MANTOMAN, inputid.getText()+"/"+senduser+"/"+consoleData);
			} catch (IOException e1) {	e1.printStackTrace();	}
		}else if(e.getSource() == exit){
			dispose();
			try {
				MultiClient.sendMsg(MsgInfo.EXITCHAT, senduser);
				MultiClient.sendMsg(MsgInfo.MANTOMAN, inputid.getText()+"/"+senduser+"/"+"대화를 종료 하였습니다.");
			} catch (IOException e1) { e1.printStackTrace(); }
		}
	}
}
