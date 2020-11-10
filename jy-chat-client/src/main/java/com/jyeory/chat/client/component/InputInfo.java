package com.jyeory.chat.client.component;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jyeory.chat.client.MultiClient;

public class InputInfo extends JFrame implements ActionListener{
	
	private JTextField idTxtFld;	
	private JTextField ipTxtFld;
	private JTextField portTxtFld;
	private JButton okBtn;
	private JComboBox genderCmbBox;
	
	public InputInfo(){
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		genderCmbBox = new JComboBox();
		genderCmbBox.addItem("남자");
		genderCmbBox.addItem("여자");

		JLabel title = new JLabel("기본 정보를 입력하세요.");
		JLabel caution = new JLabel("한글 6자, 영어 12자 이내!!");
		
		idTxtFld = new JTextField(10);
		ipTxtFld = new JTextField(15);
		portTxtFld = new JTextField(5);
		
		okBtn = new JButton("OK");
		okBtn.addActionListener(this);
		
		JPanel centerPanel1 = new JPanel();
		centerPanel1.setLayout(new GridBagLayout());
		addItem(centerPanel1, new JLabel("성별 :"), 0, 0, 1, 1, GridBagConstraints.EAST);
		addItem(centerPanel1, genderCmbBox, 1, 0, 2, 1, GridBagConstraints.WEST);
		
	    addItem(centerPanel1, new JLabel("닉넴 :"), 0, 1, 1, 1, GridBagConstraints.EAST);
	    addItem(centerPanel1, idTxtFld, 1, 1, 10, 1, GridBagConstraints.WEST);
	    
	    addItem(centerPanel1, new JLabel("I  P :"), 0, 2, 1, 1, GridBagConstraints.EAST);
	    addItem(centerPanel1, ipTxtFld, 1, 2, 2, 1, GridBagConstraints.WEST);
	    
	    addItem(centerPanel1, new JLabel("Port :"), 0, 3, 1, 1, GridBagConstraints.EAST);
	    addItem(centerPanel1, portTxtFld, 1, 3, 1, 1, GridBagConstraints.WEST);
	    
		Box btnBox = Box.createHorizontalBox();
	    btnBox.add(okBtn);
	    btnBox.add(Box.createHorizontalStrut(10));
	    addItem(centerPanel1, btnBox, 2, 4, 1, 1, GridBagConstraints.NORTH);
		
		Panel southPanel = new Panel();
		southPanel.add(caution);

		add(title, "North");
		add(centerPanel1, "Center");
		add(southPanel, "South");
		setBounds(500, 300, 250, 250);
		
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});
		
	}
	
	private void addItem(JPanel p, JComponent c, int x, int y, int width, int height, int align) {
	    GridBagConstraints gc = new GridBagConstraints();
	    gc.gridx = x;
	    gc.gridy = y;
	    gc.gridwidth = width;
	    gc.gridheight = height;
	    gc.weightx = 100.0;
	    gc.weighty = 100.0;
	    gc.insets = new Insets(5, 5, 5, 5);
	    gc.anchor = align;
	    gc.fill = GridBagConstraints.NONE;
	    p.add(c, gc);
	  }
	
	public void actionPerformed(ActionEvent e) {
		if("".contentEquals(idTxtFld.getText())){
			JOptionPane.showMessageDialog(null, "ID 입력하세요");
			return;
		}
		if(idTxtFld.getText().length() > 12){
			JOptionPane.showMessageDialog(null, "ID가 길다.");
			return;
		}
		
		if("".contentEquals(ipTxtFld.getText())){
			JOptionPane.showMessageDialog(null, "IP 입력하세요");
			return;
		}
		if(ipTxtFld.getText().length() > 0 ) {
			String regex = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
			if (!Pattern.matches(regex, ipTxtFld.getText())) {
				JOptionPane.showMessageDialog(null, "IP 제대로 입력하세요");
				return;
			}
		}
		
		if("".contentEquals(portTxtFld.getText())) {
			JOptionPane.showMessageDialog(null, "포트 입력하세요");
			return;
		}
		if(portTxtFld.getText().length() > 0) {
			String regex = "^()([1-9]|[1-5]?[0-9]{2,4}|6[1-4][0-9]{3}|65[1-4][0-9]{2}|655[1-2][0-9]|6553[1-5])$";
			if (!Pattern.matches(regex, portTxtFld.getText())) {
				JOptionPane.showMessageDialog(null, "포트 제대로 입력하세요");
				return;
			}
		}
		
		String genderStr;
		if(e.getActionCommand().equals("OK")){
			genderStr = (this.genderCmbBox.getSelectedIndex() == 0) ? "[남]" : "[여]";
			
			try {
				this.dispose();			//이 창은 없어져야 한다.
				new MultiClient(genderStr+idTxtFld.getText());
			} catch (IOException e1) {}
		}
	}
	
}