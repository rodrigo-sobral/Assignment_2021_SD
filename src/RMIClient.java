import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class RMIClient extends UnicastRemoteObject implements RMIClient_I {
    private static final long serialVersionUID = 1L;

    private RMIServer_I server1, server2;
	private int port=1099;
	private String rmiregistry1="rmiconnection1", rmiregistry2="rmiconnection2";

    RMIClient() throws RemoteException { 
        super();
        System.getProperties().put("java.security.policy","AdminConsole.policy");
        if(System.getSecurityManager() == null) System.setSecurityManager(new SecurityManager()); 
    }

    protected void connect2Servers() {
		try { server1 = (RMIServer_I) LocateRegistry.getRegistry(port).lookup(rmiregistry1); } 
        catch (Exception e1) {
            System.out.println("Servidor1 Fechado");
			try { server2 = (RMIServer_I) LocateRegistry.getRegistry(port).lookup(rmiregistry2); } 
            catch (Exception e2) { System.out.println("Servidor2 Fechado."); }
		}
	}
    protected boolean subscribe2Servers(RMIClient client, String depart_name) {
        String result=null;
        try {
            result = server1.subscribeNewClient(client, depart_name);
            System.out.println(result); 
        } catch (Exception e1) {
            System.out.println("Servidor1 Fechado");
			try { 
                result = server2.subscribeNewClient(client, depart_name);
                System.out.println(result); 
            } catch (Exception e2) { System.out.println("Servidor2 Fechado."); }
		}
        if (depart_name!=null && result!=null) {
            String[] http_code= result.split(":");
            if (http_code[0].compareTo("200")==0) return true;
            else return false;
        } return false;
    }

	public RMIServer_I getServer1() { return server1; }
	public RMIServer_I getServer2() { return server2; }

    synchronized public String ping() throws RemoteException { return "ACK"; }
}
