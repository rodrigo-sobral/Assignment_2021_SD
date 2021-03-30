
rmiserver:
	javac RMIServer.java RMIClient.java AdminConsole.java
	java RMIServer
	
admin:
	clear
	javac AdminConsole.java RMIClient.java RMIServer.java
	java AdminConsole

mcserver:
	clear
	javac MCServer.java RMIClient.java RMIServer.java AdminConsole.java
	java MCServer

clear:
	rm classes/*.class
	rm *.class