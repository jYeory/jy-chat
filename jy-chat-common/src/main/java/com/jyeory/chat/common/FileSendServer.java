package com.jyeory.chat.common;

import java.io.*;
import java.net.*;

import javax.swing.JOptionPane;

public class FileSendServer implements Runnable, Serializable{
	final String SERVER = "[FILESERVER] ";
	private String username;
	private int portnum = 0;
	private File file;
	private String path;
	private BufferedReader br;		//읽을 거[문자]
	private BufferedWriter bw;		//보낼 거[문자]
	ServerSocket serverSocket=null;
	InputStream inst = null;		//인풋 스트림.
	String filepath;				//파일 경로
	Socket socket;
	FileSendServer(int portnum, String path){
		this.portnum = portnum;
		this.filepath = path;
	}
	
	public void clientConnectionStart(){
		try{
			file = new File(filepath);			//파일 새로 생성.
			System.out.println("[파일서버 대기상태]");		//확인용
			System.out.println(portnum);					//확인용
			serverSocket = new ServerSocket(portnum);		//서버 소켓 설정.

			while(true){		//딱히 while할 필요가 없다.. 파일 수신자는 단 한명이니까..
				socket = serverSocket.accept();
				System.out.println(SERVER + "소켓 : " + socket + " 에 연결됨");
				
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				
				ClientConnection client = new ClientConnection(br, socket);
				new Thread(client).start(); 	//클라이언트 쓰레드를 시작
			}
			
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try {serverSocket.close();} catch (IOException e) {}
		}
	}
	
	private class ClientConnection extends Thread{
		private BufferedReader br;
		private Socket socket;
		
		public ClientConnection(BufferedReader br, Socket socket) {
			this.br = br;			//문자 읽을 BufferedReader 설정.
			this.socket = socket;	//소켓 설정.
		}
		
		public void run() {
			observeClientMessage();
		}

		private void observeClientMessage(){
			try{
				int msg;
				File newFile = new File(filepath);
				String filename = newFile.getName();
				long size = newFile.length();
				bw.write("[NAME]"+"/"+filename+"/"+size+"\n");	//파일 이름 보내기
				bw.flush();
				SendFile.statusBar.setMaximum((int)size/1024);
				SendFile.statusBar.setMinimum(0);
				while( (msg = br.read()) != -1 ){
					if( msg == 0){						//0이면 파일 전송
						getFileInfo(filepath);
						break;
					}else if( msg == 1){				//1이면 끝내기!
						break;							//중지
					}else if( msg == 2){
						SendFile.reject_File();		//2이면 수신 거부 이므로
						break;							//중지
					}
				}
			}catch(IOException e){
				System.out.println("소켓 닫혔음");
				e.printStackTrace();
			}finally{
				exitClient();
			}
		}
		private void getFileInfo(String fileName) throws IOException{
		    bw.write("[FILE]"+"\n");		//이 넘을 먼저 보내야 파일을 수신한다.
		    bw.flush();
			String fileInfo="";
		    try{
		    	byte[] buffer = new byte[1024];		//1024의 byte로 보내기 위해.
		    	/*
		    	 * 	인풋 스트림은 파일을 읽어오기 위한 FileInputStream으로 설정.
		    	 */
		    	inst =new FileInputStream(new File( filepath) ) ;
		    	/*
		    	 *  데이터를 보내기 위한 DataOutputStream
		    	 */
		    	OutputStream dos = new DataOutputStream( socket.getOutputStream() );
				int temp;
				int size = 0;
				while( (temp = inst.read(buffer) ) != -1){
					size += temp;		//보낸 크기(1024)씩 계속 저장.
					SendFile.size.setText(String.valueOf(size));		//화면에 전송량(size) 출력.
					SendFile.statusBar.setValue(SendFile.statusBar.getValue()+1);
					dos.write(buffer);
				}
				//size가 전체 사이즈 이상으로 올라가면 전송 끝
				if( size >= Integer.parseInt(SendFile.allsize.getText()) ){
					SendFile.hideFrame();
				}
				//다 보냈으니까 보내는 자원 정리
				dos.close();
				inst.close();
				bw.close();
		    }catch(FileNotFoundException fe){
		    	fileInfo="파일이 없습니다.";
		    }catch(IOException ie){}
		  }
		
		private void exitBuffered(){
			try{ br.close(); }catch(IOException e){}
		}
		
		private void exitClient(){
			System.out.println("[파일서버 종료]");
			//마지막 자원 정리.
			try {socket.close();} catch (IOException e) {}
		}
	}

	public void run() {
		clientConnectionStart();
	}
}
