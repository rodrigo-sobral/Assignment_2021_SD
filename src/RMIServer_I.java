import java.rmi.*;
import java.util.ArrayList;

import classes.Election;
import classes.User;

public interface RMIServer_I extends Remote {
	public String subscribeNewClient(RMIClient_I new_client) throws RemoteException;
	
	public String registUser(String new_college, String new_department, User new_user) throws RemoteException;
	public String registElection(Election new_election) throws RemoteException;
	public String ping() throws RemoteException;
	
	public ArrayList<College> getColleges() throws RemoteException;
	public ArrayList<String> getCollegesNames() throws RemoteException;
	public College getUniqueCollege(String college_name) throws RemoteException;
	
	public ArrayList<String> getDepartmentsNames() throws RemoteException;
	public Department getUniqueDepartment(String department_name) throws RemoteException;
	
	public ArrayList<Election> getRunningElections() throws RemoteException;
	public ArrayList<Election> getFinishedElections() throws RemoteException; 
	public ArrayList<Election> getUnstartedElections() throws RemoteException;
	public ArrayList<String> getElectionNames(String election_state) throws RemoteException;
	public String setUpdatedElection(Election updated_election, boolean is_candidature) throws RemoteException;
}
