package com.jyeory.chat.client.component;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

public class ReceiveMemo extends Frame implements ActionListener{
	JLabel sendeduser;
	JTextField inputid;
	JTextArea showtext;
	JButton selectid;
	JButton send;
	JButton exit;
	String senduser;
	String receiveruser;
	public ReceiveMemo(){
		super("받은 쪽지");
	}
	public void showFrame(String receiveruser, String senduser, String text){
		this.senduser = receiveruser;
		this.receiveruser = senduser;
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		sendeduser = new JLabel("보낸 사람 : ");
		inputid = new JTextField(10);
		inputid.setText(senduser);
		showtext = new JTextArea(10, 20);
		showtext.setText(text);
		selectid = new JButton("선택");
		send = new JButton("답장");
		exit = new JButton("닫기");
		send.addActionListener(this);
		exit.addActionListener(this);
		
		JPanel north = new JPanel();
		north.setLayout(new FlowLayout());
		north.add(sendeduser);	north.add(inputid);
		north.setBorder(loweredetched);
		showtext.setLineWrap(true);
		
		JPanel south = new JPanel();
		south.setLayout(new FlowLayout());
		south.add(send);	south.add(exit);
		south.setBorder(loweredetched);
		
		setLayout(new BorderLayout());
		setBounds(400, 200, 300, 400);
		add(north, "North");
		add(showtext, "Center");
		add(south, "South");
		
		setVisible(true);
		
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == send){
			dispose();			//현재창은 닫고
			SendMemo memo = new SendMemo();
			memo.showFrame(receiveruser, senduser);	//새로운 쪽지창을 띄움.
		}else if(e.getSource() == exit){
			dispose();
		}
		
	}
}
