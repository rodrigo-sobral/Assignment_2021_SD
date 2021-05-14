package webserver.model;

import java.rmi.Naming;
import java.rmi.RemoteException;
//import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import rmiserver.*;
import rmiserver.classes.*;

public class RMIConnection extends UnicastRemoteObject implements RMIClient_I {
    private static final long serialVersionUID = 1L;

    private RMIServer_I rmiserver= null;
    //private short port= 1099;
    //private String rmiserver_ip= "localhost", rmiregistry1= "rmiconnection1";
	
    public RMIConnection() throws RemoteException { 
        connectToRMI();
    }

	private void connectToRMI() { 
        System.out.println("vou criar uma conexao");
        try {
            //this.rmiserver= (RMIServer_I) LocateRegistry.getRegistry(rmiserver_ip, port).lookup(rmiregistry1);
            this.rmiserver= (RMIServer_I) Naming.lookup("rmi://localhost:1099/rmiconnection1");
            System.out.println("liguei");
        } catch (Exception e1) {
            System.out.println("Primary is now down.");
            e1.printStackTrace();
        }
    }

    public boolean registUser(User new_user) {
        System.out.println("hey nome: "+ new_user.getName());
        while (true) {
            try {
                String result = rmiserver.registUser(new_user.getCollege(), new_user.getDepartment(), new_user);
                System.out.println(result);
                if (result.split(":")[0].compareTo("200")==0) return true;
                else return false;        
            } catch (Exception e) {
                //connectToRMI();
                e.printStackTrace();
                return false;
            }
        }
    }
    public boolean registElection(Election new_election) throws RemoteException {
        System.out.println("hey titulo: "+ new_election.getTitle());
        //while (true) {
            try {
                String result = rmiserver.registElection(new_election);
                System.out.println(result);
                if (result.split(":")[0].compareTo("200")==0) return true;
                else return false;        
            } catch (Exception e) {
                //this.connectToRMI();
                e.printStackTrace();
                return false;
            }
        //}
    }

    public boolean login_user(String username,String password){
        boolean result;
        while(true){
            try {
                result = rmiserver.authenticateUser(username, password);
                System.out.println(result);
                return result;
                    
            } catch (Exception e) {
                this.connectToRMI();
            }
        }
    }
    @Override
    public boolean setNewServer(String new_server_ip) throws RemoteException { return false; }
    @Override
    public String ping() throws RemoteException { return "ACK"; }
}
