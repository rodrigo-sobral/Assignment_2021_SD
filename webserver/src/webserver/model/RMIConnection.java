package webserver.model;

import java.util.ArrayList;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

import src.rmiserver.RMIClient;

public class RMIConnection extends RMIClient {
    private static final long serialVersionUID = 1L;

	private int port=1099;
	private String rmiregistry1="rmiconnection1", rmiregistry2="rmiconnection2";
	
	private String username, password;
	private RMIConnection rmi_connection= new RMIConnection();

	public RMIConnection() throws RemoteException { super(); }

	private void connectToRMI() {
        boolean connected = false;
        while(!connected){
            try {
                connected = true;
            } catch (RemoteException | NotBoundException e) {
                System.out.println("Primary is now down.");
            }
        }
    }
}
