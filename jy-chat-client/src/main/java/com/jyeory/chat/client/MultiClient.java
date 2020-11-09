package com.jyeory.chat.client;

import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.jyeory.chat.common.MsgInfo;

class InputId extends Frame implements ActionListener{
	JTextField id;	
	JButton ok;
	JComboBox Gender;
	InputId(){
		Gender = new JComboBox();
		Gender.addItem("남자");
		Gender.addItem("여자");

		JLabel title = new JLabel("ID를 입력하세요.");
		JLabel caution = new JLabel("한글 6자, 영어 12자 이내!!");
		id = new JTextField(10);
		ok = new JButton("OK");
		ok.addActionListener(this);
		Panel centerPanel = new Panel();
		centerPanel.add(Gender);	centerPanel.add(id);	centerPanel.add(ok);
		Panel southPanel = new Panel();
		southPanel.add(caution);

		add(title, "North");
		add(centerPanel, "Center");
		add(southPanel, "South");
		setBounds(500, 300, 250, 120);
		
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});
		
	}
	public void actionPerformed(ActionEvent e) {
		String gender;
		if(id.getText().length() > 12){
			JOptionPane.showMessageDialog(null,"ID가 길잖아!!");
			this.dispose();
			InputId getid = new InputId();
			getid.setVisible(true);
		}else{
			if(e.getActionCommand().equals("OK")){
				if(Gender.getSelectedIndex() == 0){
					gender = "♂";
				}else{
					gender = "♀";
				}
				try {
					this.dispose();			//이 창은 없어져야 한다.
					MultiClient client = new MultiClient(gender+id.getText());
				} catch (IOException e1) {}
			}
		}
	}
}

public class MultiClient{
	WaitRoom waitroom = new WaitRoom("대기실");
	static Socket socket;							
	static String name;								
	static BufferedReader networkReader;			
	static BufferedWriter networkWriter;			
	
	static String ip = "localhost";					// IP
	static int port	= 3334;							// PORT번호
	
	public MultiClient(String name) throws IOException {
		this.name = name;				//사용자 ID
		waitroom.showFrame(name);
		setSocket(ip, port, name);
	}
	
	public void setSocket(String ip, int port, String name) throws IOException{
		try{
			socket = new Socket(ip,port);
			networkWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			networkReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			ListenerOfClient listener = new ListenerOfClient(networkWriter, networkReader, socket ,waitroom);
			//만약 생성자를 waitroom으로만 하게 되면 Listener_Of_Client에서는 서버명.networkWriter[Reader]로 해야함.
			listener.setDaemon(true);
			listener.start();

			sendMsg(MsgInfo.NEW, name);		//채팅자가 입장했음을 알림.
		}catch(IOException e){
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	public static void sendMsg(String token, String msg) throws IOException {
		if(msg == null){
			msg = "";
		}
		networkWriter.write(token + "/" + msg + "\n");
		networkWriter.flush();
	}

	public static void main(String[] args) throws IOException {
		InputId getid = new InputId();
		getid.setVisible(true);
	}
}
