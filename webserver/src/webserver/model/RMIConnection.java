package webserver.model;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

import rmiserver.RMIServer_I;
import rmiserver.classes.User;

public class RMIConnection {
    private RMIServer_I rmi_connection;
	private String rmiregistry1="rmiconnection1", rmiregistry2="rmiconnection2";
	
    public RMIConnection() throws RemoteException { 
        connectToRMI();
    }

	public void connectToRMI() { 
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

    public boolean registarPessoa(User new_user) throws RemoteException {
        String result = rmi_connection.registUser(new_user.getCollege(), new_user.getDepartment(), new_user);
        if (result.split(":")[0].compareTo("200")==0) return true;
        else return false;
    }
}
