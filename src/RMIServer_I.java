import java.rmi.*;

import classes.User;

public interface RMIServer_I extends Remote {
	public String registUser(User new_user) throws RemoteException;
	public String printOnServer(String message) throws RemoteException;
}
