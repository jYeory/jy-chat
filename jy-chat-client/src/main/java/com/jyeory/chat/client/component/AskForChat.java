package com.jyeory.chat.client.component;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jyeory.chat.client.MultiClient;
import com.jyeory.chat.common.MsgInfo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AskForChat extends Frame implements ActionListener {
	private JLabel subject;
	private JButton accept;
	private JButton reject;
	private String target;
	private String shooter;
	
	public AskForChat(String target, String shooter){
		super(shooter+"님의 대화 요청.");
		this.target = target;
		this.shooter = shooter;
	}
	
	public void showFrame(){
		subject = new JLabel(shooter+"님 께서 대화를 요청하셨습니다.");
		accept = new JButton("승인");
		reject = new JButton("거부");
		
		this.setLayout(new BorderLayout());
		add(subject, "North");
		
		JPanel center = new JPanel();
		center.setLayout(new FlowLayout());
		center.add(accept);	center.add(reject);
		accept.addActionListener(this);
		reject.addActionListener(this);
		add(center, "Center");
		this.pack();
		this.setVisible(true);
		
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
		
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == accept){
			try {
				MultiClient.sendMsg(MsgInfo.MAKECHAT, shooter+"/"+target);	//[MAKECHAT]/받는사람/자기대화명
				this.dispose();
			} catch (IOException e1) {	e1.printStackTrace(); }	
		}else if(e.getSource() == reject){
			try {
				MultiClient.sendMsg(MsgInfo.EXITCHAT, target);
				MultiClient.sendMsg(MsgInfo.MANTOMAN, shooter+"/"+target+"/"+"상대방이 대화를 거절하였습니다.");
				this.dispose();
			} catch (IOException e1) {
				e1.printStackTrace();
			}	//[MAKECHAT]/받는사람/자기대화명
		}
	}
}
