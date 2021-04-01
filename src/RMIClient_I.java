import java.rmi.*;


public interface RMIClient_I extends Remote {
    public String ping() throws RemoteException;
}
