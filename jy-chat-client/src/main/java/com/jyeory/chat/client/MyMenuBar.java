package com.jyeory.chat.client;

import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionListener;
/*
 * 	메뉴바를 추가하는 부분.
 *  메뉴바는 배열로 했기 때문에 for문을 이용했다.
 */
class MyMenuBar extends MenuBar{
	MyMenuBar(Frame f){
		f.setMenuBar(this);
	}
	public void addMenus(String[] menus){
		for(int i=0; i<menus.length; i++){
			add(new Menu(menus[i]));
		}
	}
	public void addMenuItems(int menu_num, String[] menus){
		for(int i=0; i<menus.length; i++){
			if (menus[i] == null) 
				getMenu(menu_num).addSeparator();
			else
				getMenu(menu_num).add(new MenuItem(menus[i]));
		}
	}
	public void addActionListener(ActionListener al){
		for(int i=0; i<getMenuCount(); i++)
			for(int j=0; j<getMenu(i).countItems(); j++)
				getMenu(i).getItem(j).addActionListener(al);
	}

}