package com.jyeory.chat.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/*
 * 파일 보내기에서 선택을 눌렀을때 호출되는 클래스!
 */
public class SelectSendFile extends Frame{
	FileDialog fileopen;
	JTextArea ta;
	String filepath;
	public SelectSendFile(String title){
		super(title);
		ta=new JTextArea();
		add(ta);
		setSize(300,300);
	}
	public String showFrame(){
		fileopen = new FileDialog(this, "문서열기", FileDialog.LOAD);		//열기모드
		fileopen.setVisible(true);
		
		String filename = fileopen.getFile();
		String filedir = fileopen.getDirectory();

		filepath = filedir+"\\"+filename;
		return filepath;
	}
}
