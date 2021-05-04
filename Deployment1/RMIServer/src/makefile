
rmiserver:
	javac RMIServer.java RMIClient.java AdminConsole.java
	java RMIServer
	
admin:
	javac AdminConsole.java RMIClient.java RMIServer.java 
	java AdminConsole

mcserver:
	javac MCServer.java RMIClient.java RMIServer.java AdminConsole.java MCServerData.java
	java MCServer

mcclient:
	javac MCServer.java MCClient.java MCServerData.java AdminConsole.java
	java MCClient

clear:
	rm classes/*.class
	rm *.class