package com.jyeory.chat.common.client;

import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import com.jyeory.chat.common.component.SaveFile;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FileReceiveClient extends Frame implements Runnable{
	
	private static final long serialVersionUID = 732759811297018638L;
	
	private TextArea msgView=new TextArea();     // 파일을 보여주는 텍스트영역
	private TextField sendBox=new TextField();   // 파일이름을 입력하는 텍스트필드
	private BufferedReader reader;               // 입력 스트림
	private BufferedWriter writer;               		 // 출력 스트림
	
	DataInputStream bin;						 //데이터 스트림.
	OutputStream out;							
	InputStream inst;
	Socket socket;
	
	public FileReceiveClient(String ip, int port) throws IOException {
		System.out.println("파일 클라이언트 생성자 호출");
		socket=new Socket(ip, port);		//소켓 설정.
		System.out.println("[FILECLIENT] " + "소켓 : " + socket + " 에 연결됨");
		writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	
	class NetworkMsgListener{
		long size = 0;
		public void receive_msg() {
			String line;
			try{
				while( (line = reader.readLine()) != null ){
					System.out.println(line);		//확인용
					String[] parsingData = line.split("/");
					if(line.startsWith("[NAME]")){
						//	[NAME] / 파일이름 / 크기
						Thread.sleep(500);
						String name = parsingData[1];
						SaveFile.filename.setText(name);			//파일 이름 설정.
						Thread.sleep(500);
						String str_size = parsingData[2];
						SaveFile.allsize.setText(str_size);			//파일 크기 설정.
 						try{
						this.size = Integer.parseInt(str_size);
						SaveFile.statusBar.setMaximum(Integer.parseInt(str_size)/1024);			//상태바의 최대 크기.
						SaveFile.statusBar.setMinimum(0);					//상태바의 최소 크
						}catch(NullPointerException shit){shit.printStackTrace(); System.out.println("에러");}
					}else if(line.startsWith("[EXIT]")){
						exitClient();					//클라이언트 종료.
					}else if(line.startsWith("[FILE]")){
						receiveFile();					//파일 전송 요청.
					}
				}
			}catch(Exception e){ 
				e.printStackTrace();	System.out.println("에러");
			}finally{ 
				exitClient();	
			}
		}
		private void receiveFile() throws Exception{
			inst = socket.getInputStream();			//인풋 스트림 생성.
			bin = new DataInputStream(inst);		//데이터 수신을 위한 데이터스트림(인풋 스트림을 꽂은채) 생성!
			File f= makeFile(inst);					//새로운 파일을 생성한다.(인풋 스트림으로)
	/*-----------------------------------------------------
	 * 	InputStream으로 생성된 File(f)를 목적지로 하여
	 *  DataInputStream으로 받은 데이터를 File(f)를 향해
	 *  받은 데이터를 그대로 File형태로 내보낸다.
	 *-----------------------------------------------------*/
			out = new FileOutputStream(f);			
			if( bin != null){
				int temp = 0;		//1024씩 온다.
				int size = 0;		//사이즈를 계속 더할 목적.
				byte[] buffer = new byte[1024];		//1024 크기의 byte.
				while( (temp = inst.read(buffer) ) != -1 ){
					size += temp;		//1024씩 계속 더한다.
					SaveFile.size.setText(String.valueOf(size));		//화면에 size(전송량)을 설정.
					SaveFile.statusBar.setValue(SaveFile.statusBar.getValue()+1);		//상태바 +1
					out.write(buffer);
				}
				//다 받았을때
				if( size >= Integer.parseInt(SaveFile.allsize.getText()) ){
					SaveFile.hideFrame();
				}
				/*	자 원 정 리	 */
				out.close();
				bin.close();
				System.out.println("파일 전송 완료");
			}
		}
		private File makeFile(InputStream inst) {
			File tmpFile = null;
				String filePath = SaveFile.path;
				tmpFile = new File( filePath ); 	//저장할 파일의 객체 생성함
				if(!tmpFile.exists()){				//파일이 없다면 생성
					try {
						tmpFile.createNewFile();
					} catch (IOException e) {e.printStackTrace();}
				}
				//컴퓨터에 저장할 파일 경로 (디렉토리 생성을 위해서)
				String path = tmpFile.getPath().substring( 0, (tmpFile.getPath().length() - tmpFile.getName().length() -1) );
				File directory = new File( path );
				if (!directory.isDirectory())
				{ //해당경로에 디렉토기가 없다면 생성
					directory.mkdirs();
				}
//			System.out.println(tmpFile);		//확인용
			return tmpFile;
		}
	}
	private void exitClient(){
		System.out.println("[파일클라이언트 종료]");
		try{
			/* 자원정리 */
			socket.close();
		}catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public void run() {
		NetworkMsgListener listener = new NetworkMsgListener();
		listener.receive_msg();
	}
}
