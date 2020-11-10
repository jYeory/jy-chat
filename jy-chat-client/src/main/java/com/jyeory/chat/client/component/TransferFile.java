package com.jyeory.chat.client.component;

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

import com.jyeory.chat.client.MultiClient;
import com.jyeory.chat.common.MsgInfo;
import com.jyeory.chat.common.component.SendFile;

public class TransferFile extends Frame implements ActionListener{
	public static JTextField targetTxtFld;
	
	private JTextField filePpathTxtFld;
	private JButton selectFileBtn;
	private JButton sendFileBtn;
	private JButton exitBtn;
	String targetfile;
	
	private JLabel firstLbl;
	private JLabel secondLbl;
	private JButton selectTargetBtn;
	private String targetUser;
	private String myName;
	
	public TransferFile(String title, String targetuser, String myname){
		super(title);
		this.targetUser = targetuser;
		this.myName = myname;
	}
	
	public void showFrame(){
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		firstLbl = new JLabel("받을 사람");
		targetTxtFld = new JTextField(20);
		if(targetUser == null){
			targetTxtFld.setText("대상을 선택하세요.");
		}else{
			targetTxtFld.setText(targetUser);
		}
		selectTargetBtn = new JButton("선택");

		JPanel north = new JPanel();
		north.setLayout(new FlowLayout());
		north.add(firstLbl);	north.add(targetTxtFld);	north.add(selectTargetBtn);
		north.setBorder(loweredetched);

		secondLbl = new JLabel("보낼 파일");
		filePpathTxtFld = new JTextField(20);
		selectFileBtn = new JButton("선택");
		JPanel center = new JPanel();
		center.setLayout(new FlowLayout());
		center.add(secondLbl);	center.add(filePpathTxtFld);	center.add(selectFileBtn);
		center.setBorder(loweredetched);

		sendFileBtn = new JButton("보내기");
		exitBtn = new JButton("닫기");
		JPanel south = new JPanel();
		south.setLayout(new FlowLayout());
		south.add(sendFileBtn);	south.add(exitBtn);
		south.setBorder(loweredetched);
		selectFileBtn.addActionListener(this);
		selectTargetBtn.addActionListener(this);
		sendFileBtn.addActionListener(this);
		exitBtn.addActionListener(this);

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
		if(targetTxtFld.getText() == myName){
			JOptionPane.showMessageDialog(null, "자기 자신에게 보낼 수 없습니다.");
		}else{
			if(e.getSource().equals(sendFileBtn)){
				a = Math.random()*32105;			//포트 번호 랜덤 고르기;	32105는 내 맘대로 준것.
				while(a == 3334){					//포트 번호가 서버 포트와 같다면 다시 골라야 한다.
					a = Math.random()*32105;		
				}
				int portnum = (int)a;
				try {
					SendFile sf = new SendFile("전송 창", portnum, filePpathTxtFld.getText());
					sf.showFrame();
					MultiClient.sendMsg(MsgInfo.FILEQUESTION, portnum+"/"+targetTxtFld.getText()+"/"+myName); 	
					//[SENDFILE]/포트번호/받는사람/자기대화명
					dispose();
				} catch (IOException e1) { e1.printStackTrace(); }	

			}else if(e.getSource() == selectFileBtn){
				SelectSendFile select = new SelectSendFile("파일 열기");
				String targetfile = select.showFrame();
				/*
				 * 열기 눌렀을때 아무것도 선택하지 않고 취소하면 
				 * null/null 로 들어가게 되므로..
				 */
				if(targetfile.startsWith("null")){
					targetfile="";
				}else{
					filePpathTxtFld.setText(targetfile);
				}
			}else if(e.getSource() == exitBtn){
				dispose();
			}else if(e.getSource() == selectTargetBtn){
				try {
					MultiClient.sendMsg(MsgInfo.SELUSER, null);
				} catch (IOException e1) {	e1.printStackTrace(); }
			}
		}
	}
}
