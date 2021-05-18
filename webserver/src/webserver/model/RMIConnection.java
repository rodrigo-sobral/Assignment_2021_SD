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
                return false;        
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
                return false;        
            } catch (Exception e) {
                e.printStackTrace();
                connectToRMI();
            }
        }
    }

    public ArrayList<User> getUsers() {
        while (true) {
            try {                 
                ArrayList<User> users= new ArrayList<>();
                for (College college : rmiserver.getColleges()) {
                    for (Department department : college.getDepartments()) {
                        users.addAll(department.getStudents());
                        users.addAll(department.getTeachers());
                        users.addAll(department.getStaff());
                    }
                } return users;
            } catch (Exception e) {
                e.printStackTrace();
                connectToRMI();
            }
        }
    }
    public ArrayList<Election> getElectionsByState(String election_state) {
        while (true) {
            try { 
                if (election_state.compareTo("unstarted")==0) return rmiserver.getUnstartedElections(); 
                if (election_state.compareTo("running")==0) return rmiserver.getRunningElections(); 
                return rmiserver.getFinishedElections(); 
            } 
            catch (Exception e) {
                e.printStackTrace();
                connectToRMI();
            }
        }
    }
    public ArrayList<Election> getElectionToVoteTable(String department) {
        while (true) {
            try { return rmiserver.getElectionToVoteTable(department); } 
            catch (Exception e) {
                e.printStackTrace();
                connectToRMI();
            }
        }
    }
    public Election getElectionByName(String election_name, String election_state) {
        while (true) {
            try { 
                if (election_state.compareTo("unstarted")==0) {
                    for (Election election : rmiserver.getUnstartedElections()) {
                        if (election.getTitle().compareTo(election_name)==0) return election;
                    } return null;
                }
                if (election_state.compareTo("running")==0) {
                    for (Election election : rmiserver.getRunningElections()) {
                        if (election.getTitle().compareTo(election_name)==0) return election;
                    } return null;
                }
                for (Election election : rmiserver.getFinishedElections()) {
                    if (election.getTitle().compareTo(election_name)==0) return election;
                } return null;
            } catch (Exception e) {
                e.printStackTrace();
                connectToRMI();
            }
        }
    }
    
    public boolean updateElection(Election edited_election, boolean candidature) {
        while (true) {
            try { 
                String result= rmiserver.setUpdatedElection(edited_election, candidature);
                if (result.split(":")[0].compareTo("200")==0) return true;
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                connectToRMI();
            }
        }
    }

    public ArrayList<Department> getDepartmentsWithOrNotVoteTable(boolean with) {
        while(true) {
            try{ return rmiserver.getDepartmentsWithOrNotVoteTable(with); }
            catch (Exception e) {
                e.printStackTrace();
                connectToRMI();
            }
        }
    }
    public Department getUniqueDepartment(String department_name) {
        while(true) {
            try{ return rmiserver.getUniqueDepartment(department_name); }
            catch (Exception e) {
                e.printStackTrace();
                connectToRMI();
            }
        }
    }
    public boolean updateDepartment(Department updated_department, boolean new_vote_table) {
        while (true) {
            try { 
                String result= rmiserver.setUpdatedDepartment(updated_department, new_vote_table);
                if (result.split(":")[0].compareTo("200")==0) return true;
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                connectToRMI();
            }
        }
    }
    public boolean sendVote(Election updated_election) {
        while (true) {
            try { 
                String result= rmiserver.receiveVote(updated_election);
                if (result.split(":")[0].compareTo("200")==0) return true;
                return false;
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
