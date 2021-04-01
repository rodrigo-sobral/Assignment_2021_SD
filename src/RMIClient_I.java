import java.rmi.*;


public interface RMIClient_I extends Remote {
    public String ping() throws RemoteException;
    public String getMyRmiIp() throws RemoteException;
    public String getRemotedRmiIp() throws RemoteException;
    public void setMyRmiIp(String my_rmi_ip) throws RemoteException;
    public void setRemotedRmiIp(String remoted_rmi_ip) throws RemoteException;
}
