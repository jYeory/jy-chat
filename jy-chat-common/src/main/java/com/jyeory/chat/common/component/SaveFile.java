package com.jyeory.chat.common.component;

import java.awt.Color;
import java.awt.FileDialog;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import com.jyeory.chat.common.client.FileReceiveClient;

public class SaveFile extends Frame implements ActionListener{
	public static JTextField filename;
	public static JTextField filepath;
	public static JProgressBar statusBar;
	public static JTextField size;
	public static JTextField allsize;
	public static String path;
	
	JButton savefile;
	JButton select_dir;
	JButton exit;
	String targetuser;
	String targetfile;
	String myname;
	File file;
	FileWriter filereader;
	FileReceiveClient client;
	
	public SaveFile(String title, 	FileReceiveClient client) throws IOException{
		super(title);
		this.client = client;
	}
	
	public void showFrame() throws IOException{
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		JLabel first = new JLabel("받을 파일");
		filename = new JTextField(20);
		filename.setText(targetuser);
		
		JPanel north = new JPanel();
		north.setLayout(new FlowLayout());
		north.add(first);	north.add(filename);
		north.setBorder(loweredetched);
		
		JLabel second = new JLabel("저장 위치");
		filepath = new JTextField(20);
		select_dir = new JButton("선택");
		
		JLabel rec_size = new JLabel("받은 크기");
		JLabel all_size = new JLabel("/");
		JLabel allsizebytes = new JLabel("Bytes");
		
		size = new JTextField(10);
		allsize = new JTextField(10);
		JPanel center_1 = new JPanel();
		center_1.setBorder(loweredetched);
		center_1.add(second);	center_1.add(filepath);	center_1.add(select_dir);
		
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
		center.setLayout(new GridLayout(3,1));
		center.add(center_1);	
		center.add(center_2);
		center.add(center_3);
		center.setBorder(loweredetched);
		
		savefile = new JButton("저장하기");
		exit = new JButton("닫기");
		JPanel south = new JPanel();
		south.setLayout(new FlowLayout());
		south.add(savefile);	south.add(exit);
		south.setBorder(loweredetched);
		select_dir.addActionListener(this);
		savefile.addActionListener(this);
		exit.addActionListener(this);
		
		add(north,"North");	add(center, "Center");	add(south, "South");
		pack();
		
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == savefile){
			try {
				//전송을 눌렀으므로 파일 서버에 0을 보냄.
				client.getWriter().write(0);
				client.getWriter().flush();
			} catch (IOException e1) {	e1.printStackTrace();	}
		}
		else if(e.getSource() == select_dir){
			SelectSaveLocation select = new SelectSaveLocation("저장 위치 선택");
			path = select.showFrame();
			filepath.setText(path);
		}
		else if(e.getSource() == exit){
			try {
				//이것도 파일 수신을 거부한 것이니까..
				client.getWriter().write(2);
				client.getWriter().flush();
				//그냥 닫기를 눌렀으니 파일 수신하지 않고 그냥 빠져 나감.
				client.getWriter().write(1);
				client.getWriter().flush();
			} catch (IOException e1) {	dispose(); 	}
			
			dispose();
		}
	}
	/*
	 * 	File_Receive_Client에서 파일을 다 받았을때 호출 됨.
	 */
	public static void hideFrame(){
		JOptionPane.showMessageDialog(null, "파일을 다 받았습니다.");
	}
	
	class SelectSaveLocation extends Frame{
		FileDialog fileopen;
		JTextArea ta;
		String filepath;
		public SelectSaveLocation(String title){
			super(title);
			ta=new JTextArea();
			add(ta);
			setSize(300,300);
		}
		/*
		 * 아래는 설명 생략.
		 */
		public String showFrame(){
			fileopen = new FileDialog(this, "저장 위치 선택", FileDialog.SAVE);		//열기모드
			fileopen.setVisible(true);
			String filename = fileopen.getFile();
			String filedir = fileopen.getDirectory();
			filepath = filedir+filename;
			/*
			 * 저장 위치를 선택할때 아무것도 선택하지 않고 취소하면 
			 * null/null 로 들어가게 되므로..
			 */
			if(filepath.startsWith("null")){
				filepath="";
				return filepath;
			}else{
				return filepath;
			}
		}
	}
}