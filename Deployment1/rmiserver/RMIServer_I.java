package rmiserver;
import java.rmi.*;
import java.util.ArrayList;

import rmiserver.classes.Election;
import rmiserver.classes.User;

public interface RMIServer_I extends Remote {

	//	CONNECTION METHODS
	public String subscribeNewClient(RMIClient_I new_client, String depart_name) throws RemoteException;
	public boolean subscribeNewServer(String new_server_ip) throws RemoteException;
	public String ping() throws RemoteException;
	public void changeServerPriority() throws RemoteException;
	public boolean isMainServer() throws RemoteException;
	public RMIServer_I getPinger() throws RemoteException;
	public String getMy_rmi_ip() throws RemoteException;
    public String getRemoted_server_ip() throws RemoteException;
	public void setMy_rmi_ip(String full_ip) throws RemoteException;
	public void setRemoted_server_ip(String full_ip) throws RemoteException;

	//	CLIENTS METHODS
	public void setClientsList(ArrayList<RMIClient_I> clients_list, boolean admins) throws RemoteException;

	//	REGIST METHODS
	public String registUser(String new_college, String new_department, User new_user) throws RemoteException;
	public String registElection(Election new_election) throws RemoteException;
	
	//	COLLEGE METHODS
	public ArrayList<College> getColleges() throws RemoteException;
	public ArrayList<String> getCollegesNames() throws RemoteException;
	public College getUniqueCollege(String college_name) throws RemoteException;
	public void setColleges(ArrayList<College> colleges) throws RemoteException;
	
	//	DEPARTMENTS METHODS
	public ArrayList<String> getDepartmentsNames() throws RemoteException;
	public Department getUniqueDepartment(String department_name) throws RemoteException;
	public ArrayList<Department> getDepartmentsWithOrNotVoteTable(boolean with) throws RemoteException;
	public ArrayList<Election> getElectionToVoteTable(String depart_name) throws RemoteException;
	public String setUpdatedDepartment(Department updated_department, boolean new_vote_table) throws RemoteException;
	
	//	ELECTION METHODS
	public ArrayList<Election> getRunningElections() throws RemoteException;
	public ArrayList<Election> getFinishedElections() throws RemoteException; 
	public ArrayList<Election> getUnstartedElections() throws RemoteException;
	public ArrayList<String> getElectionNames(String election_state) throws RemoteException;
	public Election getUniqueElection(String election_name, String election_state) throws RemoteException;
	public String setUpdatedElection(Election updated_election, boolean is_candidature) throws RemoteException;
	public void setUnstarted_elections(ArrayList<Election> unstarted_elections) throws RemoteException;
	public void setRunning_elections(ArrayList<Election> running_elections) throws RemoteException;
	public void setFinished_elections(ArrayList<Election> finished_elections) throws RemoteException;

	public String receiveVote(Election updated_election) throws RemoteException;
	
	//	AUTHENTICATIONS
	public boolean authorizeUser(String cc_number, Election voting_election) throws RemoteException;
	public boolean authenticateUser(String username, String password) throws RemoteException;
}
