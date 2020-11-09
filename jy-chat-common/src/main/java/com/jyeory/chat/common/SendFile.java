package com.jyeory.chat.common;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

public class SendFile extends Frame implements ActionListener{
	static JTextField filename;
	static JTextField filepath;
	JButton exit;
	String targetuser;
	String targetfile;
	String myname;
	File file;
	FileWriter filereader;
	FileReceiveClient client;
	static JProgressBar statusBar;
	static JTextField size;
	static JTextField allsize;
	String path;
	int port;
	FileSendServer fileserver;
	public SendFile(String title, int port, String filepath) throws IOException{
		super(title);
		this.port = port;
		this.path = filepath;
		file = new File(filepath);
	}
	public void showFrame() throws IOException{
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		JLabel first = new JLabel("보낼 파일");
		filename = new JTextField(20);
		filename.setText(file.getName());
		
		JPanel north = new JPanel();
		north.setLayout(new FlowLayout());
		north.add(first);	north.add(filename);
		north.setBorder(loweredetched);

		JLabel rec_size = new JLabel("보낸 크기");
		JLabel all_size = new JLabel("/");
		JLabel allsizebytes = new JLabel("Bytes");

		size = new JTextField(10);
		allsize = new JTextField(10);
		allsize.setText(String.valueOf(file.length()));
		JPanel center_2 = new JPanel();
		center_2.setBorder(loweredetched);
		center_2.setLayout(new FlowLayout());
		center_2.add(rec_size); center_2.add(size);		center_2.add(allsizebytes);
		center_2.add(all_size);center_2.add(allsize);	center_2.add(allsizebytes);

		statusBar = new JProgressBar();
		statusBar.setStringPainted(true);
		statusBar.setForeground(Color.BLUE);

		JPanel center_3 = new JPanel();
		center_3.setBorder(loweredetched);
		center_3.setLayout(new FlowLayout());
		JLabel status = new JLabel("상태");
		center_3.add(status);	center_3.add(statusBar);

		JPanel center = new JPanel();
		center.setLayout(new GridLayout(2,1));
		center.add(center_2);
		center.add(center_3);
		center.setBorder(loweredetched);

		exit = new JButton("닫기");
		JPanel south = new JPanel();
		south.setLayout(new FlowLayout());
		south.add(exit);
		south.setBorder(loweredetched);
		exit.addActionListener(this);

		add(north,"North");	add(center, "Center");	add(south, "South");
		pack();
		setVisible(true);

		fileserver = new FileSendServer(port, path);
		new Thread(fileserver).start();		//Thread를 실행 시키지 않으면 채팅이 불가능하다.
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == exit){
			dispose();
		}
	}
	/*
	 * 	상대방이 파일 수신을 거부하면 아래가 호출됨.
	 */
	public static void reject_File(){
		JOptionPane.showMessageDialog(null, "상대방이 거절 하였습니다.");
	}
	/*
	 *  파일을 다 전송했을때 아래가 호출됨.
	 */
	public static void hideFrame(){
		JOptionPane.showMessageDialog(null, "전송 끝!");
	}
}