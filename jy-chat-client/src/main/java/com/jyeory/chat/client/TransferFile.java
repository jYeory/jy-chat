package com.jyeory.chat.client;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import com.jyeory.chat.common.MsgInfo;
import com.jyeory.chat.common.SendFile;

public class TransferFile extends Frame implements ActionListener{
	JLabel first;
	static JTextField target;
	JButton select_target;
	JLabel second;
	JTextField filepath;
	JButton select_file;
	JButton sendfile;
	JButton exit;
	String targetuser;
	String targetfile;
	String myname;
	public TransferFile(String title, String targetuser, String myname){
		super(title);
		this.targetuser = targetuser;
		this.myname = myname;
	}
	public void showFrame(){
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		first = new JLabel("받을 사람");
		target = new JTextField(20);
		if(targetuser == null){
			target.setText("대상을 선택하세요.");
		}else{
			target.setText(targetuser);
		}
		select_target = new JButton("선택");

		JPanel north = new JPanel();
		north.setLayout(new FlowLayout());
		north.add(first);	north.add(target);	north.add(select_target);
		north.setBorder(loweredetched);

		second = new JLabel("보낼 파일");
		filepath = new JTextField(20);
		select_file = new JButton("선택");
		JPanel center = new JPanel();
		center.setLayout(new FlowLayout());
		center.add(second);	center.add(filepath);	center.add(select_file);
		center.setBorder(loweredetched);

		sendfile = new JButton("보내기");
		exit = new JButton("닫기");
		JPanel south = new JPanel();
		south.setLayout(new FlowLayout());
		south.add(sendfile);	south.add(exit);
		south.setBorder(loweredetched);
		select_file.addActionListener(this);
		select_target.addActionListener(this);
		sendfile.addActionListener(this);
		exit.addActionListener(this);

		add(north,"North");	add(center, "Center");	add(south, "South");
		setBounds(100, 200, 400, 150);
		setVisible(true);

		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
	}
	public void actionPerformed(ActionEvent e) {
		double a = 0;
		if(target.getText() == myname){
			JOptionPane.showMessageDialog(null, "자기 자신에게 보낼 수 없습니다.");
		}else{
			if(e.getSource().equals(sendfile)){
				a = Math.random()*32105;			//포트 번호 랜덤 고르기;	32105는 내 맘대로 준것.
				while(a == 3334){					//포트 번호가 서버 포트와 같다면 다시 골라야 한다.
					a = Math.random()*32105;		
				}
				int portnum = (int)a;
				try {
					SendFile sf = new SendFile("전송 창", portnum, filepath.getText());
					sf.showFrame();
					MultiClient.sendMsg(MsgInfo.FILEQUESTION, portnum+"/"+target.getText()+"/"+myname); 	
					//[SENDFILE]/포트번호/받는사람/자기대화명
					dispose();
				} catch (IOException e1) { e1.printStackTrace(); }	

			}else if(e.getSource() == select_file){
				SelectSendFile select = new SelectSendFile("파일 열기");
				String targetfile = select.showFrame();
				/*
				 * 열기 눌렀을때 아무것도 선택하지 않고 취소하면 
				 * null/null 로 들어가게 되므로..
				 */
				if(targetfile.startsWith("null")){
					targetfile="";
				}else{
					filepath.setText(targetfile);
				}
			}else if(e.getSource() == exit){
				dispose();
			}else if(e.getSource() == select_target){
				try {
					MultiClient.sendMsg(MsgInfo.SELUSER, null);
				} catch (IOException e1) {	e1.printStackTrace(); }
			}
		}
	}
}
