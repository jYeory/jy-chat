package com.jyeory.chat.client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
/*
 * 모든 접속자를 보여주는 클래스.
 * 메뉴바에서 호출된다.
 */
public class ShowAll extends Frame implements ActionListener{
	static List idlist;
	private JButton confirm;
	private JButton exit;
	public ShowAll(){
		JLabel name = new JLabel("전체 접속자");
		idlist = new List();

		JPanel south = new JPanel();
		south.setLayout(new FlowLayout());
		confirm = new JButton("확인");
		confirm.addActionListener(this);
		south.add(confirm);

		this.setLayout(new BorderLayout());
		add(name, "North");
		add(idlist, "Center");	add(south, "South");
		setBounds(300, 200, 200, 300);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		String id = idlist.getSelectedItem();
		if(e.getSource() == confirm){
			dispose();
		}
	}	
	// 액션 이벤트 종료
	
	public static void main(String[] args) {
		ShowAll id = new ShowAll();
	}
}
