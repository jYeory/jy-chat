package com.jyeory.chat.client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jyeory.chat.common.FileReceiveClient;
import com.jyeory.chat.common.SaveFile;

public class AskForFile extends Frame implements ActionListener {
	
	JLabel subject;
	JButton accept;
	JButton reject;
	String target;
	String senduser;
	FileReceiveClient client;
	int port;
	
	AskForFile(String target, String shooter, int portnum){
		super(shooter+"의 파일 수신 요청.");
		this.target = target;
		this.senduser = shooter;
		this.port = portnum;
	}
	public void showFrame(){
		subject = new JLabel(senduser+"님의 파일을 받으시겠습니까?");
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
		this.setBounds(200, 200, 200, 80);
		this.setVisible(true);
		
		
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
		
	}
	public void actionPerformed(ActionEvent e) {
		try {
			/*
			 * 어느걸 클릭하든 접속은 해야 된다.
			 */
			client = new FileReceiveClient(MultiClient.ip, port);
			SaveFile savefile = new SaveFile("파일 저장", client);
			new Thread(client).start();
			if(e.getSource() == accept){
				savefile.showFrame();
				this.dispose();
			}else if(e.getSource() == reject){
				//거절하면 2를 파일 서버로 보낸다.
				client.getWriter().write(2);
				client.getWriter().flush();
				this.dispose();
			}
		} catch (Exception e1) {	e1.printStackTrace();	}
	}
}
