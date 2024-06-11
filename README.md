# jy-chat
소켓을 이용한 채팅 프로그램 (AWT)  
학생 때 소켓 프로그래밍 공부하면서 만든 간단한 메신저/채팅 프로그램.  
기초 소켓 프로그래밍 예제를 찾는 분들에게 도움되길...  

# 모듈
공통 : jy-chat-common    
서버 : jy-chat-server
클라이언트 : jy-chat-client

# build (순서대로)
공통 : mvn clean package -pl jy-chat-common -am  
서버 : mvn clean package -pl jy-chat-server -am  
클라 : mvn clean package -pl jy-chat-client -am  

# 실행 (각각 실행)
서버 : java -jar -Dfile.encoding=UTF-8 {module_dir}/target/jy-chat-server-0.0.1-jar-with-dependencies.jar  
클라 : java -jar -Dfile.encoding=UTF-8 {module_dir}/target/jy-chat-client-0.0.1-jar-with-dependencies.jar   
