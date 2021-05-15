package webserver.model;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import rmiserver.*;
import rmiserver.classes.*;

public class RMIConnection extends UnicastRemoteObject implements RMIClient_I {
    private static final long serialVersionUID = 1L;

    private RMIServer_I rmiserver= null;
    private short port= 1099;
    private String rmiserver_ip= "localhost", rmiregistry1= "rmiconnection1", rmiregistry2= "rmiconnection2";
	
    public RMIConnection() throws RemoteException { 
        connectToRMI();
    }

	private void connectToRMI() { 
        System.out.println("vou criar uma conexao");
        try { this.rmiserver= (RMIServer_I) Naming.lookup(getRegistryFromIP(rmiserver_ip, rmiregistry1)); } 
        catch (Exception e1) {
            System.out.println("Primary is now down.");
            e1.printStackTrace();
            try { this.rmiserver= (RMIServer_I) Naming.lookup(getRegistryFromIP(rmiserver_ip, rmiregistry2)); } 
            catch (Exception e2) {
                System.out.println("Secundary is now down.");
                e2.printStackTrace();
            }
        }
    }
    private String getRegistryFromIP(String ip, String registryName) { return "rmi://"+ip+":"+port+"/"+registryName; }

    //  =========================================================================================================
    
    public boolean registUser(User new_user) {
        while (true) {
            try {
                String result = rmiserver.registUser(new_user.getCollege(), new_user.getDepartment(), new_user);
                if (result.split(":")[0].compareTo("200")==0) return true;
                else return false;        
            } catch (Exception e) {
                e.printStackTrace();
                connectToRMI();
            }
        }
    }
    public boolean registElection(Election new_election) throws RemoteException {
        while (true) {
            try {
                String result = rmiserver.registElection(new_election);
                if (result.split(":")[0].compareTo("200")==0) return true;
                else return false;        
            } catch (Exception e) {
                e.printStackTrace();
                connectToRMI();
            }
        }
    }

    public ArrayList<String> getElectionsByState(String election_state) {
        while (true) {
            try { return rmiserver.getElectionNames(election_state); } 
            catch (Exception e) {
                e.printStackTrace();
                connectToRMI();
            }
        }
    }
    public Election getElectionByName(String election_name) {
        while (true) {
            try { 
                for (Election election : rmiserver.getRunningElections()) {
                    if (election.getTitle().compareTo(election_name)==0) return election;
                }
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                connectToRMI();
            }
        }
    }
    
    public boolean updateElection(Election edited_election) {
        while (true) {
            try { 
                String result= rmiserver.setUpdatedElection(edited_election, false);
                if (result.split(":")[0].compareTo("200")==0) return true;
                else return false; 
            } catch (Exception e) {
                e.printStackTrace();
                connectToRMI();
            }
        }
    }


    //  =========================================================================================================

    @Override
    public boolean setNewServer(String new_server_ip) throws RemoteException { return false; }
    @Override
    public String ping() throws RemoteException { return "ACK"; }
}
