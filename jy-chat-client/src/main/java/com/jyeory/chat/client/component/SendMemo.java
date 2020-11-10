package com.jyeory.chat.client.component;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import com.jyeory.chat.client.MultiClient;
import com.jyeory.chat.common.MsgInfo;

public class SendMemo extends Frame implements ActionListener{
	static JTextField inputId;
	JTextArea inputtext;
	JLabel receiveduser;
	JButton selectid;
	JButton send;
	JButton exit;
	String roomtitle;
	String senduser;
	public SendMemo(){
		super("쪽지 보내기");
	}
	public void showFrame(String receiveuser, String senduser){
		this.senduser = senduser;
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		receiveduser = new JLabel("받는 사람 ");
		inputId = new JTextField(13);
		inputtext = new JTextArea(10, 20);
		selectid = new JButton("선택");
		send = new JButton("보내기");
		exit = new JButton("닫기");

		if(receiveuser == null){
			inputId.setText("상대를 선택하세요.");
		}else{
			inputId.setText(receiveuser);
		}

		JPanel north = new JPanel();
		north.setLayout(new FlowLayout());
		north.add(receiveduser); north.add(inputId);	north.add(selectid);
		north.setBorder(loweredetched);
		selectid.addActionListener(this);
		inputtext.setLineWrap(true);

		JPanel south = new JPanel();
		south.setLayout(new FlowLayout());
		south.add(send);	south.add(exit);
		send.addActionListener(this);
		exit.addActionListener(this);
		south.setBorder(loweredetched);

		setLayout(new BorderLayout());
		setBounds(200, 200, 300, 400);
		add(north, "North");
		add(inputtext, "Center");
		add(south, "South");

		setVisible(true);

	}
	public void actionPerformed(ActionEvent e) {
		try {
			if(e.getSource() == send){
				if( inputId.getText().equals(senduser) ){		
					//받는 사람이 자기 자신과 같을 경우.
					JOptionPane.showMessageDialog(null, "자기 자신에게 보낼 수 없습니다.");
				}else{
					String text = inputtext.getText();
					System.out.println(inputId.getText());
					MultiClient.sendMsg(MsgInfo.SENDMEMO, inputId.getText()+"/"+senduser+"/"+text);
					dispose();
				}
			}else if(e.getSource() == selectid){
				//받는 사람 선택했을 경우.
				MultiClient.sendMsg(MsgInfo.SELUSER, null);
			}else if(e.getSource() == exit){
				dispose();
			}
		} catch (IOException e1) {	e1.printStackTrace();	}
	}
}
