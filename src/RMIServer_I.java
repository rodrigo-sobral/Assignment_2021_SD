import java.rmi.*;
import java.util.ArrayList;

import classes.Election;
import classes.User;

public interface RMIServer_I extends Remote {
	public String printOnServer(String message) throws RemoteException;
	public String registUser(String new_college, String new_department, User new_user) throws RemoteException;
	public String registElection(Election new_election) throws RemoteException;
	public ArrayList<College> getColleges();
	public ArrayList<Election> getElections();
}
