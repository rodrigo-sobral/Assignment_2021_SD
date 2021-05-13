package webserver.model;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import rmiserver.*;
import rmiserver.classes.*;

public class RMIConnection extends UnicastRemoteObject implements RMIClient_I {
    private static final long serialVersionUID = 1L;

    private RMIServer_I rmi_connection;
    private short port= 1099;
    private String rmiserver_ip= "localhost";
	private String rmiregistry1="rmiconnection1", rmiregistry2="rmiconnection2";
	
    public RMIConnection() throws RemoteException { 
        this.connectToRMI();
    }

	private void connectToRMI() { 
        //boolean connected = false;
            try {
                this.rmi_connection= (RMIServer_I) LocateRegistry.getRegistry(rmiserver_ip, port).lookup(rmiregistry1);
                //connected = true;
            } catch (Exception e1) {
                System.out.println("Primary is now down.");
                e1.printStackTrace();
                try {
                    this.rmi_connection= (RMIServer_I) LocateRegistry.getRegistry(rmiserver_ip, port).lookup(rmiregistry2);
                    //connected = true;
                } catch (Exception e2) {
                    System.out.println("Secundary is now down.");
                    e2.printStackTrace();
                }
            }
        
    }

    public boolean registarPessoa(User new_user) throws RemoteException {
        String result = this.rmi_connection.registUser(new_user.getCollege(), new_user.getDepartment(), new_user);
        if (result.split(":")[0].compareTo("200")==0) return true;
        else return false;
    }




    @Override
    public boolean setNewServer(String new_server_ip) throws RemoteException { return false; }
    @Override
    public String ping() throws RemoteException { return "ACK"; }
}
