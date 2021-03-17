import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIServer extends UnicastRemoteObject implements RMIServer_I{   
    private static final long serialVersionUID = 1L;

	private static int port=1099;
	private static String rmiregistry1="rmiconnection1", rmiregistry2="rmiconnection2";
    private static RMIServer server1, server2; 

    public RMIServer() throws RemoteException { super(); }

    public static void main(String[] args) throws Exception {
        System.getProperties().put("java.security.policy","RMIServer.policy");
        if(System.getSecurityManager() == null) System.setSecurityManager(new SecurityManager()); 
        
        boolean result;
        do result= initServers(); while (!result);
    }

    private static boolean initServers() {
        try {
            Registry regist = LocateRegistry.createRegistry(port);
            server1 = new RMIServer();  // this would firstly be the main rmi server
            server2 = new RMIServer();  // this would firstly be the secundary rmi server
            regist.rebind(rmiregistry1, server1);
            System.out.println("Hello Server1 ready.");
            regist.rebind(rmiregistry2, server2);
            System.out.println("Hello Server2 ready.");
            return true;
        } catch (Exception e2) { System.out.println("Error turning servers on port "+port); return false; }
    }

    public void connectWithMe(RMIClient user) throws RemoteException {
		
	}
    
    public String printOnServer() throws RemoteException {
		System.out.println("vou te comer");
        return "e comi";
	}
}
