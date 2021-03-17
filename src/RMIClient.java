import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class RMIClient extends UnicastRemoteObject {
    private static final long serialVersionUID = 1L;

    private static RMIServer_I server1, server2;
    //private static RMIClient client;
	private static int port=1099;
	private static String rmiregistry1="rmiconnection1", rmiregistry2="rmiconnection2";

    RMIClient() throws RemoteException { super(); }

    public static void main(String[] args) {
        //client = new RMIClient();
        connect2Servers();
    }

    protected static void connect2Servers() {
		try {
	    	server1 = (RMIServer_I) LocateRegistry.getRegistry(port).lookup(rmiregistry1);
			String a= server1.printOnServer();
			System.out.println(a);
	    } catch (Exception e1) {
			try {
				server2 = (RMIServer_I) LocateRegistry.getRegistry(port).lookup(rmiregistry2);
				String a= server2.printOnServer();
				System.out.println(a);
			} catch (Exception e2) {
                System.out.println("Nao existem servidores.\n"+e2); 
                e2.printStackTrace();
            }
		}
	}
}
