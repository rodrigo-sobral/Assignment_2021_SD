SRC= rmiserver/

server:
	javac ${SRC}RMIServer.java ${SRC}RMIClient.java ${SRC}AdminConsole.java
	java ${SRC}RMIServer
	
admin:
	javac ${SRC}AdminConsole.java ${SRC}RMIClient.java ${SRC}RMIServer.java 
	java ${SRC}AdminConsole

mcserver:
	javac ${SRC}MCServer.java ${SRC}RMIClient.java ${SRC}RMIServer.java ${SRC}AdminConsole.java ${SRC}MCServerData.java
	java ${SRC}MCServer

mcclient:
	javac ${SRC}MCServer.java ${SRC}MCClient.java ${SRC}MCServerData.java ${SRC}AdminConsole.java
	java ${SRC}MCClient

clear:
	rm ${SRC}classes/*.class
	rm ${SRC}*.class