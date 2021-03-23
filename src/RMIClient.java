import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class RMIClient extends UnicastRemoteObject implements RMIClient_I {
    private static final long serialVersionUID = 1L;

    private static RMIClient client;
    private RMIServer_I server1, server2;
	private int port=1099;
	private String rmiregistry1="rmiconnection1", rmiregistry2="rmiconnection2";
    protected boolean isAdmin;

    RMIClient() throws RemoteException { super(); isAdmin= false; }

    public static void main(String[] args) throws RemoteException {
        System.getProperties().put("java.security.policy","RMIServer.policy");
        if(System.getSecurityManager() == null) System.setSecurityManager(new SecurityManager()); 

        client = new RMIClient();
        client.connect2Servers(client);

    }

    protected void connect2Servers(RMIClient client) {
		try { 
            server1 = (RMIServer_I) LocateRegistry.getRegistry(port).lookup(rmiregistry1); 
            System.out.println(server1.subscribeNewClient(client));
        } 
        catch (Exception e1) {
            System.out.println("Servidor1 Fechado");
			try { 
                server2 = (RMIServer_I) LocateRegistry.getRegistry(port).lookup(rmiregistry2); 
                System.out.println(server2.subscribeNewClient(client));
            } 
            catch (Exception e2) { System.out.println("Servidor2 Fechado."); }
		}
	}

	public RMIServer_I getServer1() { return server1; }
	public RMIServer_I getServer2() { return server2; }
	public boolean getIsAdmin() { return isAdmin; }

    synchronized public String ping() throws RemoteException { return "ACK"; }
}
