package webserver.model;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import rmiserver.*;
import rmiserver.classes.*;

public class RMIConnection extends UnicastRemoteObject implements RMIClient_I {
    private static final long serialVersionUID = 1L;

    private RMIServer_I rmiserver;
    private short port= 1099;
    private String rmiserver_ip= "localhost";
	private String rmiregistry1="rmiconnection1";
	
    public RMIConnection() throws RemoteException { 
        this.connectToRMI();
    }

	private void connectToRMI() { 
        System.out.println("vou criar uma conexao");
        try {
            this.rmiserver= (RMIServer_I) LocateRegistry.getRegistry(rmiserver_ip, port).lookup(rmiregistry1);
            System.out.println("liguei");
        } catch (Exception e1) {
            System.out.println("Primary is now down.");
            e1.printStackTrace();
        }
    }

    public boolean registarPessoa(User new_user) throws RemoteException {
        System.out.println("yey nome: "+ new_user.getName());
        while (true) {
            try {
                String result = rmiserver.registUser(new_user.getCollege(), new_user.getDepartment(), new_user);
                System.out.println(result);
                if (result.split(":")[0].compareTo("200")==0) return true;
                else return false;        
            } catch (Exception e) {
                this.connectToRMI();
            }
        }
    }

    public String getCollege() {
        if (this.rmiserver!=null) {
            try {
                return this.rmiserver.getColleges().get(0).getName();
            } catch (Exception e) { e.printStackTrace(); return "foda-se 2";}
        }
        else return "foda-se";
    }


    @Override
    public boolean setNewServer(String new_server_ip) throws RemoteException { return false; }
    @Override
    public String ping() throws RemoteException { return "ACK"; }
}
