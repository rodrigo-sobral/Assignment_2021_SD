import java.rmi.*;


public interface RMIClient_I extends Remote {
    public boolean getIsAdmin() throws RemoteException;
    public String ping() throws RemoteException;
}
