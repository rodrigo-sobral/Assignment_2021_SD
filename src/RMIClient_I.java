import java.rmi.*;


public interface RMIClient_I extends Remote {
    public boolean setNewServer(String new_server_ip) throws RemoteException;
    public String ping() throws RemoteException;
}
