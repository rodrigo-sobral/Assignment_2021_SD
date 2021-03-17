//package hello2;
import java.rmi.*;

public interface RMIServer_I extends Remote {
	public String printOnServer() throws java.rmi.RemoteException;
}
