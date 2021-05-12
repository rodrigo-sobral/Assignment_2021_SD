package webserver.model;

import java.util.ArrayList;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

import rmiserver.RMIServer_I;

public class RMIConnection {
    private RMIServer_I rmi_connection;
	private String rmiregistry1="rmiconnection1", rmiregistry2="rmiconnection2";
	
	public RMIConnection() throws RemoteException { 
        boolean connected = false;
        while(!connected){
            try {
                rmi_connection= (RMIServer_I) Naming.lookup(rmiregistry1);
                connected = true;
            } catch (RemoteException | NotBoundException | MalformedURLException e1) {
                System.out.println("Primary is now down.");
                try {
                    rmi_connection= (RMIServer_I) Naming.lookup(rmiregistry2);
                    connected = true;
                } catch (RemoteException | NotBoundException | MalformedURLException e2) {
                    System.out.println("Secundary is now down.");
                }
            }
        }
    }

}
